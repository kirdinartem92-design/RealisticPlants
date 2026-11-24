package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, RealisticPlants.MOD_ID);

    public static final Supplier<MenuType<GreenhouseControlPanelMenu>> GREENHOUSE_CONTROL_PANEL =
            MENUS.register("greenhouse_control_panel", () ->
                    new MenuType<>(GreenhouseControlPanelMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {}

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
