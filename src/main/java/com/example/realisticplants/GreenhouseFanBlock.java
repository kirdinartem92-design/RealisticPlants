package com.realisticplants;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Greenhouse ventilation fan. ACTIVE is controlled automatically by the
 * greenhouse environment when one or more crops need cooling.
 */
public class GreenhouseFanBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public GreenhouseFanBlock(Properties properties) {
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
