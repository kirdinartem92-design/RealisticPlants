package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Marker block entity used by the analyzer to open the greenhouse summary. */
public class GreenhouseControlPanelBlockEntity extends BlockEntity {
    private static final DeferredRegister<BlockEntityType<?>> TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RealisticPlants.MOD_ID);

    public static final Supplier<BlockEntityType<GreenhouseControlPanelBlockEntity>> TYPE =
            TYPES.register("greenhouse_control_panel", () ->
                    new BlockEntityType<>(
                            GreenhouseControlPanelBlockEntity::new,
                            ModBlocks.GREENHOUSE_CONTROL_PANEL.get()
                    ));

    public GreenhouseControlPanelBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
