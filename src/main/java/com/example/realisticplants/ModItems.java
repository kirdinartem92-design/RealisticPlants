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

    public static final DeferredItem<Item> TOMATO = ITEMS.registerSimpleItem("tomato", p -> p.food(createFoodProperties("tomato")));
    public static final DeferredItem<Item> CUCUMBER = ITEMS.registerSimpleItem("cucumber", p -> p.food(createFoodProperties("cucumber")));
    public static final DeferredItem<Item> POTATO = ITEMS.registerSimpleItem("potato", p -> p.food(createFoodProperties("potato")));
    public static final DeferredItem<Item> CARROT = ITEMS.registerSimpleItem("carrot", p -> p.food(createFoodProperties("carrot")));
    public static final DeferredItem<Item> BEETROOT = ITEMS.registerSimpleItem("beetroot", p -> p.food(createFoodProperties("beetroot")));
    public static final DeferredItem<Item> ZUCCHINI = ITEMS.registerSimpleItem("zucchini", p -> p.food(createFoodProperties("zucchini")));
    public static final DeferredItem<Item> EGGPLANT = ITEMS.registerSimpleItem("eggplant", p -> p.food(createFoodProperties("eggplant")));
    public static final DeferredItem<Item> BELL_PEPPER = ITEMS.registerSimpleItem("bell_pepper", p -> p.food(createFoodProperties("bell_pepper")));
    public static final DeferredItem<Item> CHILI_PEPPER = ITEMS.registerSimpleItem("chili_pepper", p -> p.food(createFoodProperties("chili_pepper")));
    public static final DeferredItem<Item> BROCCOLI = ITEMS.registerSimpleItem("broccoli", p -> p.food(createFoodProperties("broccoli")));

    public static final DeferredItem<Item> VEGETABLE_SALAD = ITEMS.registerSimpleItem("vegetable_salad",
            p -> p.food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.6f).build()));
    public static final DeferredItem<Item> RATATOUILLE = ITEMS.registerSimpleItem("ratatouille",
            p -> p.food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8f).build()));
    public static final DeferredItem<Item> STUFFED_PEPPER = ITEMS.registerSimpleItem("stuffed_pepper",
            p -> p.food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.7f).build()));

    public static final DeferredItem<RareVegetableItem> GOLDEN_VEGETABLE =
            ITEMS.registerItem("golden_vegetable", p -> new RareVegetableItem(
                    p.food(new FoodProperties.Builder().nutrition(8).saturationModifier(1.2f).build()),
                    RareVegetableItem.Variant.GOLDEN));
    public static final DeferredItem<RareVegetableItem> MEGA_VEGETABLE =
            ITEMS.registerItem("mega_vegetable", p -> new RareVegetableItem(
                    p.food(new FoodProperties.Builder().nutrition(20).saturationModifier(2.4f).build()),
                    RareVegetableItem.Variant.MEGA));

    public static final DeferredItem<TomatoSeedsItem> TOMATO_SEEDS = ITEMS.registerItem("tomato_seeds", TomatoSeedsItem::new);
    public static final DeferredItem<CucumberSeedsItem> CUCUMBER_SEEDS = ITEMS.registerItem("cucumber_seeds", CucumberSeedsItem::new);
    public static final DeferredItem<CropSeedsItem> ZUCCHINI_SEEDS = ITEMS.registerItem("zucchini_seeds", p -> new CropSeedsItem(p, ModBlocks.ZUCCHINI_PLANT));
    public static final DeferredItem<CropSeedsItem> EGGPLANT_SEEDS = ITEMS.registerItem("eggplant_seeds", p -> new CropSeedsItem(p, ModBlocks.EGGPLANT_PLANT));
    public static final DeferredItem<CropSeedsItem> BELL_PEPPER_SEEDS = ITEMS.registerItem("bell_pepper_seeds", p -> new CropSeedsItem(p, ModBlocks.BELL_PEPPER_PLANT));
    public static final DeferredItem<CropSeedsItem> CHILI_PEPPER_SEEDS = ITEMS.registerItem("chili_pepper_seeds", p -> new CropSeedsItem(p, ModBlocks.CHILI_PEPPER_PLANT));
    public static final DeferredItem<CropSeedsItem> BROCCOLI_SEEDS = ITEMS.registerItem("broccoli_seeds", p -> new CropSeedsItem(p, ModBlocks.BROCCOLI_PLANT));
    public static final DeferredItem<CropSeedsItem> POTATO_SEEDS = ITEMS.registerItem("potato_seeds", p -> new CropSeedsItem(p, ModBlocks.POTATO_PLANT));
    public static final DeferredItem<CropSeedsItem> CARROT_SEEDS = ITEMS.registerItem("carrot_seeds", p -> new CropSeedsItem(p, ModBlocks.CARROT_PLANT));
    public static final DeferredItem<CropSeedsItem> BEETROOT_SEEDS = ITEMS.registerItem("beetroot_seeds", p -> new CropSeedsItem(p, ModBlocks.BEETROOT_PLANT));

    public static final DeferredItem<BlockItem> TOMATO_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.TOMATO_PLANT);
    public static final DeferredItem<BlockItem> CUCUMBER_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CUCUMBER_PLANT);
    public static final DeferredItem<BlockItem> ZUCCHINI_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.ZUCCHINI_PLANT);
    public static final DeferredItem<BlockItem> EGGPLANT_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.EGGPLANT_PLANT);
    public static final DeferredItem<BlockItem> BELL_PEPPER_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.BELL_PEPPER_PLANT);
    public static final DeferredItem<BlockItem> CHILI_PEPPER_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CHILI_PEPPER_PLANT);
    public static final DeferredItem<BlockItem> BROCCOLI_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.BROCCOLI_PLANT);
    public static final DeferredItem<BlockItem> POTATO_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.POTATO_PLANT);
    public static final DeferredItem<BlockItem> CARROT_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CARROT_PLANT);
    public static final DeferredItem<BlockItem> BEETROOT_PLANT_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.BEETROOT_PLANT);

    private ModItems() {}
    private static FoodProperties createFoodProperties(String plantId) {
        PlantDefinition definition = PlantRegistry.get(plantId);
        if (definition == null) throw new IllegalStateException("Unknown plant definition: " + plantId);
        return new FoodProperties.Builder().nutrition(definition.getNutrition())
                .saturationModifier(definition.getSaturationModifier()).build();
    }
    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }
}
