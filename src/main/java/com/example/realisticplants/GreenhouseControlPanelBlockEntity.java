package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Marker block entity used by the analyzer to open the greenhouse summary. */
public class GreenhouseControlPanelBlockEntity extends BlockEntity implements MenuProvider {
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.realisticplants.greenhouse_control_panel");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new GreenhouseControlPanelMenu(containerId, inventory, level, worldPosition);
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
