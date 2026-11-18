
package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class TomatoPlantBlock extends Block {

    public static final IntegerProperty AGE =
            IntegerProperty.create("age", 0, 6);

    public TomatoPlantBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any().setValue(AGE, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 6;
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random) {

        int age = state.getValue(AGE);

        if (age < 6 && random.nextInt(3) == 0) {
            level.setBlock(
                    pos,
                    state.setValue(AGE, age + 1),
                    Block.UPDATE_ALL
            );
        }
    }
}
