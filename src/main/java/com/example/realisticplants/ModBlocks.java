
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

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
