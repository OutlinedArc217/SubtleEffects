package einstein.ambient_sleep.util;

import commonnetwork.api.Dispatcher;
import einstein.ambient_sleep.particle.option.CommandBlockParticleOptions;
import einstein.ambient_sleep.init.ModParticles;
import einstein.ambient_sleep.networking.clientbound.ClientBoundEntityFellPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiPredicate;

import static einstein.ambient_sleep.init.ModConfigs.INSTANCE;
import static einstein.ambient_sleep.util.MathUtil.nextFloat;
import static einstein.ambient_sleep.util.MathUtil.nextSign;

public class ParticleSpawnUtil {

    public static void spawnSparks(Level level, RandomSource random, BlockPos pos, Vec3 offset, Vec3i maxSpeeds, int count, int size, boolean isSoulFlame, boolean longLife) {
        spawnSparks(level, random, pos, offset, maxSpeeds, count, size, size, isSoulFlame, longLife);
    }

    public static void spawnSparks(Level level, RandomSource random, BlockPos pos, Vec3 offset, Vec3i maxSpeeds, int count, int xSize, int zSize, boolean isSoulFlame, boolean longLife) {
        if (random.nextInt(1) == 0) {
            for (int i = 0; i < count; i++) {
                SimpleParticleType type = (isSoulFlame ? (longLife ? ModParticles.LONG_SOUL_SPARK : ModParticles.SHORT_SOUL_SPARK) : (longLife ? ModParticles.LONG_SPARK : ModParticles.SHORT_SPARK)).get();
                level.addParticle(type,
                        pos.getX() + offset.x() + random.nextDouble() / xSize * nextSign(),
                        pos.getY() + offset.y(),
                        pos.getZ() + offset.z() + random.nextDouble() / zSize * nextSign(),
                        nextFloat(maxSpeeds.getX()) * nextSign(),
                        nextFloat(maxSpeeds.getY()) * nextSign(),
                        nextFloat(maxSpeeds.getZ()) * nextSign()
                );
            }
        }
    }

    public static void spawnParticlesAroundBlock(ParticleOptions particle, Level level, BlockPos pos, RandomSource random) {
        for (Direction direction : Direction.values()) {
            BlockPos relativePos = pos.relative(direction);
            if (!level.getBlockState(relativePos).isSolidRender(level, relativePos)) {
                spawnParticlesOnSide(particle, 0.0625F, direction, level, pos, random, 0, 0, 0);
            }
        }
    }

    public static void spawnParticlesOnSide(ParticleOptions particle, float offset, Direction direction, Level level, BlockPos pos, RandomSource random, double xSpeed, double ySpeed, double zSpeed) {
        double offsetFromCenter = 0.5 + offset;
        Direction.Axis axis = direction.getAxis();
        double xOffset = axis == Direction.Axis.X ? 0.5 + offsetFromCenter * direction.getStepX() : random.nextFloat();
        double yOffset = axis == Direction.Axis.Y ? 0.5 + offsetFromCenter * direction.getStepY() : random.nextFloat();
        double zOffset = axis == Direction.Axis.Z ? 0.5 + offsetFromCenter * direction.getStepZ() : random.nextFloat();
        level.addParticle(particle, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, xSpeed, ySpeed, zSpeed);
    }

    public static void spawnFallDustClouds(LivingEntity entity, float distance, int fallDamage) {
        Level level = entity.level();
        if (level.isClientSide && entity.equals(Minecraft.getInstance().player)) {
            ParticleManager.entityFell(entity, entity.getY(), distance, fallDamage);
        }
        else if (!level.isClientSide && !entity.equals(Minecraft.getInstance().player)) {
            Dispatcher.sendToAllClients(new ClientBoundEntityFellPacket(entity.getId(), entity.getY(), distance, fallDamage), level.getServer());
        }
    }

    public static void spawnCreatureMovementDustClouds(LivingEntity entity, Level level, RandomSource random, int YSpeedModifier) {
        if (INSTANCE.mobSprintingDustClouds.get()) {
            level.addParticle(ModParticles.LARGE_DUST_CLOUD.get(),
                    entity.position().x + entity.getBbWidth() * random.nextDouble() - 1,
                    entity.getY() + Math.max(Math.min(random.nextFloat(), 0.5), 0.2),
                    entity.position().z + entity.getBbWidth() * random.nextDouble() - 1,
                    0,
                    random.nextDouble() * YSpeedModifier,
                    0
            );
        }
    }

    public static void spawnLavaSparks(Level level, BlockPos pos, RandomSource random, int count) {
        for (int i = 0; i < count; i++) {
            level.addParticle(ModParticles.FLOATING_SPARK.get(),
                    pos.getX() + 0.5 + random.nextDouble() / 2 * nextSign(),
                    pos.getY() + random.nextDouble() * random.nextInt(3),
                    pos.getZ() + 0.5 + random.nextDouble() / 2 * nextSign(),
                    nextFloat(10) * nextSign(),
                    nextFloat(7) ,
                    nextFloat(10) * nextSign()
            );
        }
    }

    public static void spawnCmdBlockParticles(Level level, Vec3 pos, RandomSource random, BiPredicate<Direction, Vec3> directionValidator) {
        for (Direction direction : Direction.values()) {
            Vec3 endPos = pos.relative(direction, 1);
            Vec3 relativePos = endPos.relative(direction, -0.5);

            if (directionValidator.test(direction, endPos)) {
                Vec3 speed = pos.vectorTo(relativePos).offsetRandom(random, 1);
                level.addParticle(new CommandBlockParticleOptions(direction), endPos.x(), endPos.y(), endPos.z(), speed.x(), speed.y(), speed.z());
            }
        }
    }
}
