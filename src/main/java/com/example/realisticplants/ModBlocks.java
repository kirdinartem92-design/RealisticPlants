package com.realisticplants;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(RealisticPlants.MOD_ID);

    public static final DeferredBlock<Block> TEST_PLANT =
            BLOCKS.registerSimpleBlock(
                    "test_plant",
                    properties -> properties.mapColor(MapColor.PLANT)
            );

    public static final DeferredBlock<TomatoPlantBlock> TOMATO_PLANT =
            BLOCKS.registerBlock(
                    "tomato_plant",
                    properties -> new TomatoPlantBlock(
                            properties.mapColor(MapColor.PLANT)
                    )
            );

    public static final DeferredBlock<CucumberPlantBlock> CUCUMBER_PLANT =
            BLOCKS.registerBlock(
                    "cucumber_plant",
                    properties -> new CucumberPlantBlock(
                            properties.mapColor(MapColor.PLANT)
                    )
            );

    public static final DeferredBlock<NewCropPlantBlock> ZUCCHINI_PLANT =
            BLOCKS.registerBlock("zucchini_plant",
                    properties -> new NewCropPlantBlock(properties.mapColor(MapColor.PLANT),
                            "zucchini", ModItems.ZUCCHINI, ModItems.ZUCCHINI_SEEDS));

    public static final DeferredBlock<NewCropPlantBlock> EGGPLANT_PLANT =
            BLOCKS.registerBlock("eggplant_plant",
                    properties -> new NewCropPlantBlock(properties.mapColor(MapColor.PLANT),
                            "eggplant", ModItems.EGGPLANT, ModItems.EGGPLANT_SEEDS));

    public static final DeferredBlock<NewCropPlantBlock> BELL_PEPPER_PLANT =
            BLOCKS.registerBlock("bell_pepper_plant",
                    properties -> new NewCropPlantBlock(properties.mapColor(MapColor.PLANT),
                            "bell_pepper", ModItems.BELL_PEPPER, ModItems.BELL_PEPPER_SEEDS));

    public static final DeferredBlock<NewCropPlantBlock> CHILI_PEPPER_PLANT =
            BLOCKS.registerBlock("chili_pepper_plant",
                    properties -> new NewCropPlantBlock(properties.mapColor(MapColor.PLANT),
                            "chili_pepper", ModItems.CHILI_PEPPER, ModItems.CHILI_PEPPER_SEEDS));

    public static final DeferredBlock<NewCropPlantBlock> BROCCOLI_PLANT =
            BLOCKS.registerBlock("broccoli_plant",
                    properties -> new NewCropPlantBlock(properties.mapColor(MapColor.PLANT),
                            "broccoli", ModItems.BROCCOLI, ModItems.BROCCOLI_SEEDS));

    public static final DeferredBlock<NewCropPlantBlock> POTATO_PLANT =
            BLOCKS.registerBlock("potato_plant", p -> new NewCropPlantBlock(p.mapColor(MapColor.PLANT),
                    "potato", ModItems.POTATO, ModItems.POTATO_SEEDS));
    public static final DeferredBlock<NewCropPlantBlock> CARROT_PLANT =
            BLOCKS.registerBlock("carrot_plant", p -> new NewCropPlantBlock(p.mapColor(MapColor.PLANT),
                    "carrot", ModItems.CARROT, ModItems.CARROT_SEEDS));
    public static final DeferredBlock<NewCropPlantBlock> BEETROOT_PLANT =
            BLOCKS.registerBlock("beetroot_plant", p -> new NewCropPlantBlock(p.mapColor(MapColor.PLANT),
                    "beetroot", ModItems.BEETROOT, ModItems.BEETROOT_SEEDS));

    public static final DeferredBlock<IrrigationSprinklerBlock> IRRIGATION_SPRINKLER =
            BLOCKS.registerBlock(
                    "irrigation_sprinkler",
                    properties -> new IrrigationSprinklerBlock(
                            properties.mapColor(MapColor.METAL)
                                    .strength(2.0f)
                    )
            );

    public static final DeferredBlock<PurpleGrowLightBlock> PURPLE_GROW_LIGHT =
            BLOCKS.registerBlock(
                    "purple_grow_light",
                    properties -> new PurpleGrowLightBlock(
                            properties.mapColor(MapColor.COLOR_PURPLE)
                                    .strength(1.5f)
                                    .lightLevel(state ->
                                            state.getValue(PurpleGrowLightBlock.ACTIVE) ? 12 : 0)
                    )
            );

    public static final DeferredBlock<DripIrrigationBlock> DRIP_IRRIGATION =
            BLOCKS.registerBlock(
                    "drip_irrigation",
                    properties -> new DripIrrigationBlock(
                            properties.mapColor(MapColor.METAL)
                                    .strength(1.5f)
                    )
            );

    public static final DeferredBlock<GreenhouseFanBlock> GREENHOUSE_FAN =
            BLOCKS.registerBlock(
                    "greenhouse_fan",
                    properties -> new GreenhouseFanBlock(
                            properties.mapColor(MapColor.METAL)
                                    .strength(1.5f)
                    )
            );

    public static final DeferredBlock<GreenhouseControlPanelBlock> GREENHOUSE_CONTROL_PANEL =
            BLOCKS.registerBlock(
                    "greenhouse_control_panel",
                    properties -> new GreenhouseControlPanelBlock(
                            properties.mapColor(MapColor.METAL).strength(2.0f)
                    )
            );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
