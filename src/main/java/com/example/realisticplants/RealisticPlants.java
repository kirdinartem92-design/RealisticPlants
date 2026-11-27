package com.realisticplants;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.rpg_foundation.structure_pool.api.StructurePoolAPI;
import net.rpg_foundation.structure_pool.api.StructurePoolConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RealisticPlants.MOD_ID)
public class RealisticPlants {

    public static final String MOD_ID = "realisticplants";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealisticPlants(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(PlantPlacementHandler.class);
        NeoForge.EVENT_BUS.addListener(RPShowcaseCommand::register);
        registerVillageGardens();

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

    /**
     * Adds the seven garden variants and the rare greenhouse to the five
     * vanilla village house pools. The injected structures remain optional
     * additions; vanilla village pieces are not replaced.
     */
    private static void registerVillageGardens() {
        StructurePoolConfig config = new StructurePoolConfig();
        String[] villageHousePools = {
                "minecraft:village/plains/houses",
                "minecraft:village/taiga/houses",
                "minecraft:village/snowy/houses",
                "minecraft:village/savanna/houses",
                "minecraft:village/desert/houses"
        };

        for (String pool : villageHousePools) {
            for (int variant = 1; variant <= 7; variant++) {
                config.entries.add(new StructurePoolConfig.Entry(
                        pool,
                        "realisticplants:village/garden_" + variant,
                        1
                ));
            }
            config.entries.add(new StructurePoolConfig.Entry(
                    pool,
                    "realisticplants:village/super_greenhouse",
                    1
            ));
        }

        StructurePoolAPI.injectAll(config);
    }
}
