
package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TomatoPlantBlock extends Block {

    public static final IntegerProperty AGE =
            IntegerProperty.create("age", 0, 6);

    private static final VoxelShape SHAPE =
            Block.box(4, 0, 4, 12, 12, 12);

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
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos) {
        return Block.canSupportCenter(
                level,
                pos.below(),
                Direction.UP
        );
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
