package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared seed item used by all configurable vegetable crops.
 */
public class CropSeedsItem extends Item {

    private final Supplier<? extends NewCropPlantBlock> plantBlock;

    public CropSeedsItem(
            Properties properties,
            Supplier<? extends NewCropPlantBlock> plantBlock
    ) {
        super(properties);
        this.plantBlock = plantBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos farmlandPos = context.getClickedPos();
        BlockPos plantPos = farmlandPos.above();

        if (!level.getBlockState(farmlandPos).is(Blocks.FARMLAND)) {
            return InteractionResult.PASS;
        }

        if (!level.getBlockState(plantPos).isAir()) {
            return InteractionResult.PASS;
        }

        BlockState plantState = plantBlock.get().defaultBlockState();

        if (!plantState.canSurvive(level, plantPos)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlock(plantPos, plantState, 3);

            if (context.getPlayer() == null
                    || !context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
