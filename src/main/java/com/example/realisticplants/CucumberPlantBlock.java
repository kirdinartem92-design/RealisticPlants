
package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CucumberPlantBlock extends CropPlantBlock {

    public static final IntegerProperty AGE = CropPlantBlock.AGE;

    public static final EnumProperty<PlantCondition> CONDITION =
            CropPlantBlock.CONDITION;

    public CucumberPlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state) {
        return new CucumberPlantBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {

        if (level.isClientSide()) {
            return null;
        }

        if (type != CucumberPlantBlockEntity.TYPE.get()) {
            return null;
        }

        return (world, pos, blockState, blockEntity) ->
                CucumberPlantBlockEntity.tick(
                        world,
                        pos,
                        blockState,
                        (CucumberPlantBlockEntity) blockEntity
                );
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return false;
    }
}
