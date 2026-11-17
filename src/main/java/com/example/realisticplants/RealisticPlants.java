
package com.realisticplants;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RealisticPlants.MOD_ID)
public class RealisticPlants {

    public static final String MOD_ID = "realisticplants";

    public static final Logger LOGGER = LogUtils.getLogger();

    public RealisticPlants(IEventBus modEventBus, ModContainer modContainer) {

        LOGGER.info("Realistic Plants is loading!");

        LOGGER.info("Minecraft 26.3 + NeoForge");

    }
}
