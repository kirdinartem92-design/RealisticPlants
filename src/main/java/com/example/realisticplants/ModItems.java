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
            ITEMS.registerSimpleItem("tomato",
                    properties -> properties.food(createFoodProperties("tomato")));

    public static final DeferredItem<Item> CUCUMBER =
            ITEMS.registerSimpleItem("cucumber",
                    properties -> properties.food(createFoodProperties("cucumber")));

    public static final DeferredItem<Item> POTATO =
            ITEMS.registerSimpleItem("potato",
                    properties -> properties.food(createFoodProperties("potato")));

    public static final DeferredItem<Item> CARROT =
            ITEMS.registerSimpleItem("carrot",
                    properties -> properties.food(createFoodProperties("carrot")));

    public static final DeferredItem<Item> BEETROOT =
            ITEMS.registerSimpleItem("beetroot",
                    properties -> properties.food(createFoodProperties("beetroot")));

    public static final DeferredItem<Item> ZUCCHINI =
            ITEMS.registerSimpleItem("zucchini",
                    properties -> properties.food(createFoodProperties("zucchini")));

    public static final DeferredItem<Item> EGGPLANT =
            ITEMS.registerSimpleItem("eggplant",
                    properties -> properties.food(createFoodProperties("eggplant")));

    public static final DeferredItem<Item> BELL_PEPPER =
            ITEMS.registerSimpleItem("bell_pepper",
                    properties -> properties.food(createFoodProperties("bell_pepper")));

    public static final DeferredItem<Item> CHILI_PEPPER =
            ITEMS.registerSimpleItem("chili_pepper",
                    properties -> properties.food(createFoodProperties("chili_pepper")));

    public static final DeferredItem<Item> BROCCOLI =
            ITEMS.registerSimpleItem("broccoli",
                    properties -> properties.food(createFoodProperties("broccoli")));

    public static final DeferredItem<RareVegetableItem> GOLDEN_VEGETABLE =
            ITEMS.registerItem("golden_vegetable",
                    properties -> new RareVegetableItem(
                            properties.food(new FoodProperties.Builder()
                                    .nutrition(8).saturationModifier(1.2f).build()),
                            RareVegetableItem.Variant.GOLDEN));

    public static final DeferredItem<RareVegetableItem> MEGA_VEGETABLE =
            ITEMS.registerItem("mega_vegetable",
                    properties -> new RareVegetableItem(
                            properties.food(new FoodProperties.Builder()
                                    .nutrition(20).saturationModifier(2.4f).build()),
                            RareVegetableItem.Variant.MEGA));

    public static final DeferredItem<TomatoSeedsItem> TOMATO_SEEDS =
            ITEMS.registerItem("tomato_seeds", TomatoSeedsItem::new);

    public static final DeferredItem<CucumberSeedsItem> CUCUMBER_SEEDS =
            ITEMS.registerItem("cucumber_seeds", CucumberSeedsItem::new);

    public static final DeferredItem<CropSeedsItem> ZUCCHINI_SEEDS =
            ITEMS.registerItem("zucchini_seeds",
                    properties -> new CropSeedsItem(properties, ModBlocks.ZUCCHINI_PLANT));
    public static final DeferredItem<CropSeedsItem> EGGPLANT_SEEDS =
            ITEMS.registerItem("eggplant_seeds",
                    properties -> new CropSeedsItem(properties, ModBlocks.EGGPLANT_PLANT));
    public static final DeferredItem<CropSeedsItem> BELL_PEPPER_SEEDS =
            ITEMS.registerItem("bell_pepper_seeds",
                    properties -> new CropSeedsItem(properties, ModBlocks.BELL_PEPPER_PLANT));
    public static final DeferredItem<CropSeedsItem> CHILI_PEPPER_SEEDS =
            ITEMS.registerItem("chili_pepper_seeds",
                    properties -> new CropSeedsItem(properties, ModBlocks.CHILI_PEPPER_PLANT));
    public static final DeferredItem<CropSeedsItem> BROCCOLI_SEEDS =
            ITEMS.registerItem("broccoli_seeds",
                    properties -> new CropSeedsItem(properties, ModBlocks.BROCCOLI_PLANT));

    public static final DeferredItem<BlockItem> TOMATO_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.TOMATO_PLANT);

    public static final DeferredItem<BlockItem> CUCUMBER_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.CUCUMBER_PLANT);

    public static final DeferredItem<BlockItem> ZUCCHINI_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.ZUCCHINI_PLANT);
    public static final DeferredItem<BlockItem> EGGPLANT_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.EGGPLANT_PLANT);
    public static final DeferredItem<BlockItem> BELL_PEPPER_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.BELL_PEPPER_PLANT);
    public static final DeferredItem<BlockItem> CHILI_PEPPER_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.CHILI_PEPPER_PLANT);
    public static final DeferredItem<BlockItem> BROCCOLI_PLANT_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.BROCCOLI_PLANT);

    private ModItems() {
    }

    private static FoodProperties createFoodProperties(String plantId) {
        PlantDefinition definition = PlantRegistry.get(plantId);
        if (definition == null) {
            throw new IllegalStateException("Unknown plant definition: " + plantId);
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
