
package com.realisticplants;

/**
 * Defines the biological and growth properties of a plant species.
 * Shared by all plant types in Realistic Plants.
 */
public class PlantDefinition {

    private final String id;
    private final int maxGrowthStage;

    private final int growthInterval;
    private final int moistureConsumption;

    private final float minTemperature;
    private final float maxTemperature;

    private final int minimumMoisture;
    private final int droughtResistance;

    private final boolean regrowsAfterHarvest;

    public PlantDefinition(
            String id,
            int maxGrowthStage,
            int growthInterval,
            int moistureConsumption,
            float minTemperature,
            float maxTemperature,
            int minimumMoisture,
            int droughtResistance,
            boolean regrowsAfterHarvest
    ) {
        this.id = id;
        this.maxGrowthStage = maxGrowthStage;
        this.growthInterval = growthInterval;
        this.moistureConsumption = moistureConsumption;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minimumMoisture = minimumMoisture;
        this.droughtResistance = droughtResistance;
        this.regrowsAfterHarvest = regrowsAfterHarvest;
    }

    public String getId() {
        return id;
    }

    public int getMaxGrowthStage() {
        return maxGrowthStage;
    }

    public int getGrowthInterval() {
        return growthInterval;
    }

    public int getMoistureConsumption() {
        return moistureConsumption;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public int getMinimumMoisture() {
        return minimumMoisture;
    }

    public int getDroughtResistance() {
        return droughtResistance;
    }

    public boolean regrowsAfterHarvest() {
        return regrowsAfterHarvest;
    }
}
