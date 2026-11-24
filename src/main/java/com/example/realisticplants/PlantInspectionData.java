package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * Read-only snapshot of the information an agronomic analyzer can display.
 * Values are calculated on demand and do not alter plant simulation.
 */
public record PlantInspectionData(
        int moisture,
        int health,
        int growthStage,
        int growthProgressPercent,
        int skyLight,
        int blockLight,
        float temperature,
        float minimumTemperature,
        float maximumTemperature,
        boolean greenhouse,
        boolean flowering,
        boolean harvestReady,
        PlantCondition condition,
        double environmentalGrowthMultiplier
) {

    /**
     * Creates an inspection snapshot for a loaded crop block entity.
     * Temperature uses the mod's internal biome-based game scale,
     * not Celsius or Fahrenheit.
     */
    public static PlantInspectionData capture(
            Level level,
            BlockPos pos,
            CropPlantBlockEntity crop
    ) {
        if (level == null || pos == null || crop == null) {
            throw new IllegalArgumentException(
                    "Level, position and crop are required"
            );
        }

        PlantData plant = crop.getPlantData();
        PlantDefinition definition = plant.getDefinition();

        boolean inGreenhouse =
                GreenhouseEnvironment.isInGreenhouse(level, pos, definition);

        float ambientTemperature =
                level.getBiome(pos).value().getBaseTemperature();

        float effectiveTemperature = GreenhouseEnvironment.getTemperature(
                level,
                pos,
                ambientTemperature,
                inGreenhouse,
                definition
        );

        return new PlantInspectionData(
                plant.getMoisture(),
                plant.getHealth(),
                crop.getGrowthStage(),
                crop.getGrowthProgressPercent(),
                level.getBrightness(LightLayer.SKY, pos),
                level.getBrightness(LightLayer.BLOCK, pos),
                effectiveTemperature,
                definition.getMinTemperature(),
                definition.getMaxTemperature(),
                inGreenhouse,
                plant.isFlowering(),
                plant.isHarvestReady(),
                plant.getCondition(),
                PurpleGrowLightEnvironment.getGrowthMultiplier(
                        level,
                        pos,
                        definition
                )
        );
    }

    /**
     * Human-readable temperature category for a future analyzer display.
     */
    public String getTemperatureStatus() {
        if (temperature < minimumTemperature) {
            return "cold";
        }
        if (temperature > maximumTemperature) {
            return "hot";
        }
        return "comfortable";
    }
}
