
package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TomatoSeedsItem extends Item {

    public TomatoSeedsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();

        BlockPos farmlandPos = context.getClickedPos();
        BlockPos plantPos = farmlandPos.above();

        if (!level.getBlockState(farmlandPos)
                .is(Blocks.FARMLAND)) {
            return InteractionResult.PASS;
        }

        if (!level.getBlockState(plantPos).isAir()) {
            return InteractionResult.PASS;
        }

        BlockState plantState =
                ModBlocks.TOMATO_PLANT.get().defaultBlockState();

        if (!plantState.canSurvive(level, plantPos)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {

            level.setBlock(
                    plantPos,
                    plantState,
                    3
            );

            if (context.getPlayer() == null
                    || !context.getPlayer()
                    .getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(
                level.isClientSide()
        );
    }
}
