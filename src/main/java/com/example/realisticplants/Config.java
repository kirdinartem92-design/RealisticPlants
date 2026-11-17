
package com.realisticplants;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue GROWTH_SPEED =
            BUILDER
                    .comment("Plant growth speed in ticks")
                    .defineInRange("growthSpeed", 1200, 100, 24000);

    public static final ModConfigSpec.IntValue WATERING_INTERVAL =
            BUILDER
                    .comment("Watering interval in ticks")
                    .defineInRange("wateringInterval", 600, 100, 24000);

    public static final ModConfigSpec.IntValue SOIL_MOISTURE =
            BUILDER
                    .comment("Default soil moisture level")
                    .defineInRange("soilMoisture", 100, 0, 100);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
