package com.realisticplants;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RealisticPlants.MOD_ID);
    public static final Supplier<CreativeModeTab> REALISTIC_PLANTS_TAB =
            CREATIVE_TABS.register("realistic_plants", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.realisticplants"))
                    .icon(() -> new ItemStack(ModItems.TOMATO.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TOMATO.get());
                        output.accept(ModItems.TOMATO_SEEDS.get());
                        output.accept(ModItems.TOMATO_PLANT_ITEM.get());
                        output.accept(ModItems.CUCUMBER.get());
                        output.accept(ModItems.CUCUMBER_SEEDS.get());
                        output.accept(ModItems.CUCUMBER_PLANT_ITEM.get());
                        output.accept(ModItems.POTATO.get());
                        output.accept(ModItems.CARROT.get());
                        output.accept(ModItems.BEETROOT.get());
                        output.accept(ModItems.ZUCCHINI.get());
                        output.accept(ModItems.EGGPLANT.get());
                        output.accept(ModItems.BELL_PEPPER.get());
                        output.accept(ModItems.CHILI_PEPPER.get());
                        output.accept(ModItems.BROCCOLI.get());
                        output.accept(ModItems.ZUCCHINI_SEEDS.get());
                        output.accept(ModItems.ZUCCHINI_PLANT_ITEM.get());
                        output.accept(ModItems.EGGPLANT_SEEDS.get());
                        output.accept(ModItems.EGGPLANT_PLANT_ITEM.get());
                        output.accept(ModItems.BELL_PEPPER_SEEDS.get());
                        output.accept(ModItems.BELL_PEPPER_PLANT_ITEM.get());
                        output.accept(ModItems.CHILI_PEPPER_SEEDS.get());
                        output.accept(ModItems.CHILI_PEPPER_PLANT_ITEM.get());
                        output.accept(ModItems.BROCCOLI_SEEDS.get());
                        output.accept(ModItems.BROCCOLI_PLANT_ITEM.get());
                        output.accept(ModItems.VEGETABLE_SALAD.get());
                        output.accept(ModItems.RATATOUILLE.get());
                        output.accept(ModItems.STUFFED_PEPPER.get());
                        output.accept(ModItems.GOLDEN_VEGETABLE.get());
                        output.accept(ModItems.MEGA_VEGETABLE.get());
                    }).build());
    private ModCreativeTabs() {}
    public static void register(IEventBus eventBus) { CREATIVE_TABS.register(eventBus); }
}
