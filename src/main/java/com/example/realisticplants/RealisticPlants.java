
package com.realisticplants;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(RealisticPlants.MOD_ID)
public class RealisticPlants {

    public static final String MOD_ID = "realisticplants";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealisticPlants(IEventBus modEventBus, ModContainer modContainer) {

        modContainer.registerConfig(
                ModConfig.Type.LOCAL,
                Config.SPEC
        );

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        TomatoPlantBlockEntity.register(modEventBus);
        CucumberPlantBlockEntity.register(modEventBus);
        NewCropPlantBlockEntity.register(modEventBus);

        LOGGER.info("Realistic Plants is loading!");
        LOGGER.info("Plant configuration registered!");
        LOGGER.info("Plant items registered!");
        LOGGER.info("Creative tabs registered!");
    }
}
