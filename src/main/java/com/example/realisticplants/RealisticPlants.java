
package com.realisticplants;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RealisticPlants.MOD_ID)
public class RealisticPlants {

    public static final String MOD_ID = "realisticplants";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealisticPlants(IEventBus modEventBus, ModContainer modContainer) {

        NeoForge.EVENT_BUS.register(PlantPlacementHandler.class);

        ModSounds.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModMenus.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        TomatoPlantBlockEntity.register(modEventBus);
        CucumberPlantBlockEntity.register(modEventBus);
        NewCropPlantBlockEntity.register(modEventBus);
        IrrigationSprinklerBlockEntity.register(modEventBus);
        DripIrrigationBlockEntity.register(modEventBus);
        GreenhouseControlPanelBlockEntity.register(modEventBus);
        PurpleGrowLightBlockEntity.register(modEventBus);
        GreenhouseFanBlockEntity.register(modEventBus);

        LOGGER.info("Realistic Plants is loading!");
        LOGGER.info("Plant items registered!");
        LOGGER.info("Creative tabs registered!");
    }
}
