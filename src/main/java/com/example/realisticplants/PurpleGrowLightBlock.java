package com.realisticplants;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Automatically controlled grow light. It emits light at night and during
 * daytime whenever natural sky light is insufficient for greenhouse crops.
 */
public class PurpleGrowLightBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public PurpleGrowLightBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(ACTIVE);
    }
}
