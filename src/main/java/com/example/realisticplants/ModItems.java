
package com.realisticplants;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(RealisticPlants.MOD_ID);

    public static final DeferredItem<Item> TOMATO =
            ITEMS.registerSimpleItem(
                    "tomato",
                    properties -> properties.food(
                            createFoodProperties("tomato")
                    )
            );

    public static final DeferredItem<Item> CUCUMBER =
            ITEMS.registerSimpleItem(
                    "cucumber",
                    properties -> properties.food(
                            createFoodProperties("cucumber")
                    )
            );

    public static final DeferredItem<TomatoSeedsItem> TOMATO_SEEDS =
            ITEMS.registerItem(
                    "tomato_seeds",
                    TomatoSeedsItem::new
            );

    public static final DeferredItem<CucumberSeedsItem> CUCUMBER_SEEDS =
            ITEMS.registerItem(
                    "cucumber_seeds",
                    CucumberSeedsItem::new
            );

    public static final DeferredItem<BlockItem> TOMATO_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.TOMATO_PLANT);

    public static final DeferredItem<BlockItem> CUCUMBER_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.CUCUMBER_PLANT);

    private ModItems() {
    }

    private static FoodProperties createFoodProperties(String plantId) {
        PlantDefinition definition = PlantRegistry.get(plantId);

        if (definition == null) {
            throw new IllegalStateException(
                    "Unknown plant definition: " + plantId
            );
        }

        return new FoodProperties.Builder()
                .nutrition(definition.getNutrition())
                .saturationModifier(definition.getSaturationModifier())
                .build();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
