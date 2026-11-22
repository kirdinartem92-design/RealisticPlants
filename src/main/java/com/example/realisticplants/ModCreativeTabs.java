
package com.realisticplants;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    RealisticPlants.MOD_ID
            );

    public static final DeferredRegister<CreativeModeTab> TABS =
            CREATIVE_TABS;

    public static final var REALISTIC_PLANTS_TAB =
            CREATIVE_TABS.register("realistic_plants",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable(
                                    "itemGroup.realisticplants"
                            ))
                            .icon(() -> new ItemStack(ModItems.TOMATO.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.TOMATO.get());
                                output.accept(ModItems.TOMATO_SEEDS.get());
                                output.accept(ModBlocks.TOMATO_PLANT.get());
                            })
                            .build()
            );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
