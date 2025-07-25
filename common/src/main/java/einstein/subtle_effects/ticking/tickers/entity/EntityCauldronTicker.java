package einstein.subtle_effects.ticking.tickers.entity;

import einstein.subtle_effects.mixin.client.block.AbstractCauldronBlockAccessor;
import einstein.subtle_effects.mixin.client.entity.EntityAccessor;
import einstein.subtle_effects.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EntityCauldronTicker extends EntityTicker<Entity> {

    private boolean wasInWaterCauldron;

    public EntityCauldronTicker(Entity entity) {
        super(entity);
    }

    @Override
    public void entityTick() {
        BlockPos pos = entity.blockPosition();
        BlockState state = entity.getInBlockState();
        double height = Util.getCauldronFillHeight(state);

        if ((state.is(Blocks.POWDER_SNOW_CAULDRON) || state.is(Blocks.WATER_CAULDRON)) && height > 0) {
            if (entity.isOnFire() && ((AbstractCauldronBlockAccessor) state.getBlock()).isEntityInside(state, pos, entity)) {
                ((EntityAccessor) entity).playExtinguishedSound();
            }
        }

        if (state.is(Blocks.WATER_CAULDRON) && height > 0) {
            if (pos.getY() + height >= entity.getY()) {
                if (!wasInWaterCauldron) {
                    wasInWaterCauldron = true;
                    ((EntityAccessor) entity).doWaterSplashingEffects();
                }
                return;
            }
        }
        wasInWaterCauldron = false;
    }
}
