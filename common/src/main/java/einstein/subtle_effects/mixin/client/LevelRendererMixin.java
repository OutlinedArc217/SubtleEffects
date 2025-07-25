package einstein.subtle_effects.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import einstein.subtle_effects.configs.ModBlockConfigs;
import einstein.subtle_effects.configs.ReplacedParticlesDisplayType;
import einstein.subtle_effects.init.ModConfigs;
import einstein.subtle_effects.init.ModParticles;
import einstein.subtle_effects.ticking.tickers.TickerManager;
import einstein.subtle_effects.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static einstein.subtle_effects.init.ModConfigs.ENVIRONMENT;
import static einstein.subtle_effects.init.ModConfigs.BLOCKS;
import static einstein.subtle_effects.util.MathUtil.nextNonAbsDouble;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements FrustumGetter {

    @Shadow
    @Nullable
    private ClientLevel level;

    @Shadow
    private Frustum cullingFrustum;

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "renderSnowAndRain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;RAIN_LOCATION:Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation replaceRainTexture(Operation<ResourceLocation> original) {
        if (ENVIRONMENT.biomeColorRain) {
            return Util.COLORLESS_RAIN_TEXTURE;
        }
        return original.call();
    }

    @WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer renderSnowAndRain(VertexConsumer instance, float red, float green, float blue, float alpha, Operation<VertexConsumer> original, @Local Biome biome, @Local Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN && ENVIRONMENT.biomeColorRain) {
            int waterColor = biome.getWaterColor();
            return instance.setColor((waterColor >> 16) / 255F, (waterColor >> 8) / 255F, waterColor / 255F, alpha);
        }
        return original.call(instance, red, green, blue, alpha);
    }

    @ModifyExpressionValue(method = "tickRain", at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;"))
    private SimpleParticleType a(SimpleParticleType original) {
        if (BLOCKS.steam.replaceRainEvaporationSteam) {
            return ModParticles.STEAM.get();
        }
        return original;
    }

    // 'original' does not capture the '!', so the returned expression must be written inverted
    @ModifyExpressionValue(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean b(boolean original, @Local BlockState state) {
        return original || (BLOCKS.steam.lavaCauldronsEvaporateRain && state.is(Blocks.LAVA_CAULDRON));
    }

    @Inject(method = "levelEvent", at = @At("TAIL"))
    private void levelEvent(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (level == null) {
            return;
        }

        RandomSource random = level.getRandom();
        BlockState state = level.getBlockState(pos);
        Player player = minecraft.player;

        switch (type) {
            case LevelEvent.SOUND_ANVIL_BROKEN: {
                if (BLOCKS.anvilBreakParticles) {
                    level.addDestroyBlockEffect(pos, state);
                }
                break;
            }
            case LevelEvent.SOUND_ANVIL_USED: {
                if (BLOCKS.anvilUseParticles) {
                    for (int i = 0; i < 3; i++) {
                        TickerManager.schedule(8 * i, () ->
                                ParticleSpawnUtil.spawnHammeringWorkstationParticles(pos, random, level)
                        );
                    }
                }
                break;
            }
            case LevelEvent.SOUND_GRINDSTONE_USED: {
                ParticleSpawnUtil.spawnGrindstoneUsedParticles(level, pos, state, random);
                break;
            }
            case LevelEvent.SOUND_SMITHING_TABLE_USED: {
                if (BLOCKS.smithingTableUseParticles) {
                    ParticleSpawnUtil.spawnHammeringWorkstationParticles(pos, random, level);
                }
                break;
            }
            case LevelEvent.END_PORTAL_FRAME_FILL: {
                TickerManager.scheduleNext(() -> ParticleSpawnUtil.spawnEnderEyePlacementParticles(pos, random, level, Util.getEyeColorHolder(level, pos).toInt()));
                break;
            }
        }
    }

    @WrapOperation(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ParticleUtils;spawnParticlesOnBlockFaces(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/util/valueproviders/IntProvider;)V"))
    private void cancelOrReplaceCopperParticles(Level level, BlockPos pos, ParticleOptions particle, IntProvider count, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) int type) {
        BlockState state = level.getBlockState(pos);
        RandomSource random = level.getRandom();

        if (type == LevelEvent.PARTICLES_SCRAPE) {
            if (ModConfigs.ITEMS.axeScrapeParticlesDisplayType != ReplacedParticlesDisplayType.DEFAULT) {
                subtleEffects$spawnCopperParticles(level, pos, count, state, random);
            }
            return;
        }
        else if (type == LevelEvent.PARTICLES_WAX_OFF) {
            if (ModConfigs.ITEMS.axeWaxOffParticlesDisplayType != ReplacedParticlesDisplayType.DEFAULT) {
                subtleEffects$spawnCopperParticles(level, pos, count, state, random);
            }
            return;
        }

        original.call(level, pos, particle, count);
    }

    @Unique
    private static void subtleEffects$spawnCopperParticles(Level level, BlockPos pos, IntProvider count, BlockState state, RandomSource random) {
        ParticleSpawnUtil.spawnParticlesAroundShape(ParticleTypes.WAX_OFF,
                level, pos, state, count.sample(random),
                () -> new Vec3(
                        nextNonAbsDouble(random, 0.5),
                        nextNonAbsDouble(random, 0.5),
                        nextNonAbsDouble(random, 0.5)
                ), 0.125F
        );
    }

    // Fabric didn't like using a slice for some reason, should try again at some point
    @WrapWithCondition(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private boolean shouldSpawnEndPortalFrameSmoke(ClientLevel level, ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Local(argsOnly = true, ordinal = 0) int type) {
        if (type == LevelEvent.END_PORTAL_FRAME_FILL) {
            return BLOCKS.enderEyePlacedParticlesDisplayType != ModBlockConfigs.EnderEyePlacedParticlesDisplayType.DOTS;
        }
        return true;
    }

    @WrapOperation(method = "levelEvent", at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;LARGE_SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;"))
    private SimpleParticleType replaceSmoke(Operation<SimpleParticleType> original) {
        if (BLOCKS.steam.lavaFizzSteam) {
            return ModParticles.STEAM.get();
        }
        return original.call();
    }

    @WrapOperation(method = "levelEvent", at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;CLOUD:Lnet/minecraft/core/particles/SimpleParticleType;"))
    private SimpleParticleType replaceCloud(Operation<SimpleParticleType> original) {
        if (BLOCKS.steam.spongeDryingOutSteam) {
            return ModParticles.STEAM.get();
        }
        return original.call();
    }

    @ModifyReturnValue(method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "RETURN", ordinal = 0))
    private Particle spawnForcedParticle(Particle particle) {
        if (particle != null) {
            ((ParticleAccessor) particle).subtleEffects$force();
        }
        return particle;
    }

    @Override
    public Frustum subtleEffects$getCullingFrustum() {
        return cullingFrustum;
    }
}
