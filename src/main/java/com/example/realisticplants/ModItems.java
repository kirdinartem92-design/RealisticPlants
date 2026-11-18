
package com.realisticplants;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(RealisticPlants.MOD_ID);

    public static final DeferredItem<Item> TOMATO =
            ITEMS.registerSimpleItem("tomato");

    private ModItems() {
    }

    public static void register(
            net.neoforged.bus.api.IEventBus eventBus
    ) {
        ITEMS.register(eventBus);
    }
}
