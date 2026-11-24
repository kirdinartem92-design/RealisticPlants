
package com.realisticplants;

/**
 * Defines the biological, growth and food properties
 * of a plant species.
 */
public class PlantDefinition {

    private final String id;

    private final int maxGrowthStage;
    private final int floweringStage;
    private final int regrowthStage;

    private final int growthInterval;
    private final int moistureInterval;
    private final int moistureConsumption;

    private final float minTemperature;
    private final float maxTemperature;

    private final int minimumMoisture;
    private final int droughtResistance;

    private final boolean regrowsAfterHarvest;

    private final int minHarvestAmount;
    private final int maxHarvestAmount;

    private final int nutrition;
    private final float saturationModifier;

    /**
     * Backward-compatible constructor.
     */
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
        this(
                id,
                maxGrowthStage,
                Math.max(0, maxGrowthStage - 2),
                Math.max(0, maxGrowthStage - 2),
                growthInterval,
                growthInterval,
                moistureConsumption,
                minTemperature,
                maxTemperature,
                minimumMoisture,
                droughtResistance,
                regrowsAfterHarvest,
                1,
                4,
                2,
                0.3f
        );
    }

    /**
     * Constructor with individual flowering and regrowth stages.
     */
    public PlantDefinition(
            String id,
            int maxGrowthStage,
            int floweringStage,
            int regrowthStage,
            int growthInterval,
            int moistureConsumption,
            float minTemperature,
            float maxTemperature,
            int minimumMoisture,
            int droughtResistance,
            boolean regrowsAfterHarvest
    ) {
        this(
                id,
                maxGrowthStage,
                floweringStage,
                regrowthStage,
                growthInterval,
                moistureConsumption,
                minTemperature,
                maxTemperature,
                minimumMoisture,
                droughtResistance,
                regrowsAfterHarvest,
                1,
                4,
                2,
                0.3f
        );
    }

    /**
     * Constructor with individual harvest settings.
     */
    public PlantDefinition(
            String id,
            int maxGrowthStage,
            int floweringStage,
            int regrowthStage,
            int growthInterval,
            int moistureConsumption,
            float minTemperature,
            float maxTemperature,
            int minimumMoisture,
            int droughtResistance,
            boolean regrowsAfterHarvest,
            int minHarvestAmount,
            int maxHarvestAmount
    ) {
        this(
                id,
                maxGrowthStage,
                floweringStage,
                regrowthStage,
                growthInterval,
                moistureConsumption,
                minTemperature,
                maxTemperature,
                minimumMoisture,
                droughtResistance,
                regrowsAfterHarvest,
                minHarvestAmount,
                maxHarvestAmount,
                2,
                0.3f
        );
    }

    /**
     * Complete constructor with individual food properties.
     */
    public PlantDefinition(
            String id,
            int maxGrowthStage,
            int floweringStage,
            int regrowthStage,
            int growthInterval,
            int moistureConsumption,
            float minTemperature,
            float maxTemperature,
            int minimumMoisture,
            int droughtResistance,
            boolean regrowsAfterHarvest,
            int minHarvestAmount,
            int maxHarvestAmount,
            int nutrition,
            float saturationModifier
    ) {
        this(
                id, maxGrowthStage, floweringStage, regrowthStage,
                growthInterval, growthInterval, moistureConsumption,
                minTemperature, maxTemperature, minimumMoisture,
                droughtResistance, regrowsAfterHarvest, minHarvestAmount,
                maxHarvestAmount, nutrition, saturationModifier
        );
    }

    /**
     * Complete constructor with independently configurable growth and
     * environmental moisture intervals.
     */
    public PlantDefinition(
            String id,
            int maxGrowthStage,
            int floweringStage,
            int regrowthStage,
            int growthInterval,
            int moistureInterval,
            int moistureConsumption,
            float minTemperature,
            float maxTemperature,
            int minimumMoisture,
            int droughtResistance,
            boolean regrowsAfterHarvest,
            int minHarvestAmount,
            int maxHarvestAmount,
            int nutrition,
            float saturationModifier
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Plant ID cannot be empty"
            );
        }

        if (maxGrowthStage < 1 || maxGrowthStage > 6) {
            throw new IllegalArgumentException(
                    "Maximum growth stage must be between 1 and 6"
            );
        }

        if (floweringStage < 0 || floweringStage > maxGrowthStage) {
            throw new IllegalArgumentException(
                    "Invalid flowering stage"
            );
        }

        if (regrowthStage < 0 || regrowthStage > maxGrowthStage) {
            throw new IllegalArgumentException(
                    "Invalid regrowth stage"
            );
        }

        if (growthInterval < 1) {
            throw new IllegalArgumentException(
                    "Growth interval must be positive"
            );
        }

        if (moistureInterval < 1) {
            throw new IllegalArgumentException(
                    "Moisture interval must be positive"
            );
        }

        if (moistureConsumption < 0) {
            throw new IllegalArgumentException(
                    "Moisture consumption cannot be negative"
            );
        }

        if (minimumMoisture < 0 || minimumMoisture > 100) {
            throw new IllegalArgumentException(
                    "Minimum moisture must be between 0 and 100"
            );
        }

        if (droughtResistance < 0 || droughtResistance > 100) {
            throw new IllegalArgumentException(
                    "Drought resistance must be between 0 and 100"
            );
        }

        if (!Float.isFinite(minTemperature)
                || !Float.isFinite(maxTemperature)
                || minTemperature > maxTemperature) {
            throw new IllegalArgumentException(
                    "Invalid plant temperature range"
            );
        }

        if (minHarvestAmount < 1
                || maxHarvestAmount < minHarvestAmount) {
            throw new IllegalArgumentException(
                    "Invalid harvest amount range"
            );
        }

        if (nutrition < 0 || !Float.isFinite(saturationModifier)
                || saturationModifier < 0) {
            throw new IllegalArgumentException(
                    "Food properties must be finite and cannot be negative"
            );
        }

        this.id = id;
        this.maxGrowthStage = maxGrowthStage;
        this.floweringStage = floweringStage;
        this.regrowthStage = regrowthStage;
        this.growthInterval = growthInterval;
        this.moistureInterval = moistureInterval;
        this.moistureConsumption = moistureConsumption;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minimumMoisture = minimumMoisture;
        this.droughtResistance = droughtResistance;
        this.regrowsAfterHarvest = regrowsAfterHarvest;
        this.minHarvestAmount = minHarvestAmount;
        this.maxHarvestAmount = maxHarvestAmount;
        this.nutrition = nutrition;
        this.saturationModifier = saturationModifier;
    }

    public String getId() {
        return id;
    }

    public int getMaxGrowthStage() {
        return maxGrowthStage;
    }

    public int getFloweringStage() {
        return floweringStage;
    }

    public int getRegrowthStage() {
        return regrowthStage;
    }

    public int getGrowthInterval() {
        return growthInterval;
    }

    public int getMoistureInterval() {
        return moistureInterval;
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

    public int getMinHarvestAmount() {
        return minHarvestAmount;
    }

    public int getMaxHarvestAmount() {
        return maxHarvestAmount;
    }

    public int getNutrition() {
        return nutrition;
    }

    public float getSaturationModifier() {
        return saturationModifier;
    }
}
