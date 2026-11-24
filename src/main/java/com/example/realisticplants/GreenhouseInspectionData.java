package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Read-only summary of a greenhouse-sized area around a selected position.
 * This is the data layer for the future central greenhouse control panel.
 */
public record GreenhouseInspectionData(
        int plantCount,
        int averageMoisture,
        int averageHealth,
        int averageSkyLight,
        int averageBlockLight,
        int sprinklerCount,
        int dripIrrigationCount,
        int purpleGrowLightCount,
        int fanCount,
        int storedWater,
        int totalWaterCapacity,
        int plantsNeedingCare,
        int deadPlants,
        int wiltingPlants,
        int dryPlants,
        int lowLightPlants,
        int weakPlants,
        int overheatedPlants,
        int coldPlants,
        int droughtPlants,
        float averageTemperature,
        double averageGrowthMultiplier,
        int tomatoCount,
        int cucumberCount,
        int zucchiniCount,
        int eggplantCount,
        int bellPepperCount,
        int chiliPepperCount,
        int broccoliCount,
        int healthyPlants,
        int floweringPlants,
        int harvestReadyPlants
) {
    private static final int HORIZONTAL_RADIUS = 8;
    private static final int MIN_Y = -2;
    private static final int MAX_Y = 4;

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato", "cucumber", "zucchini", "eggplant",
            "bell_pepper", "chili_pepper", "broccoli"
    );

    /**
     * Scans a bounded 17x17x7 area around the selected position.
     * Only the seven crops introduced by this mod are included.
     */
    public static GreenhouseInspectionData capture(Level level, BlockPos center) {
        if (level == null || center == null) {
            throw new IllegalArgumentException("Level and center are required");
        }

        int plants = 0;
        int moistureTotal = 0;
        int healthTotal = 0;
        int skyLightTotal = 0;
        int blockLightTotal = 0;
        int sprinklers = 0;
        int drippers = 0;
        int growLights = 0;
        int fans = 0;
        int water = 0;
        int capacity = 0;
        int needsCare = 0;
        int dead = 0;
        int wilting = 0;
        int dry = 0;
        int lowLight = 0;
        int weak = 0;
        int overheated = 0;
        int cold = 0;
        int drought = 0;
        double growthMultiplierTotal = 0.0;
        double temperatureTotal = 0.0;
        int tomatoes = 0, cucumbers = 0, zucchinis = 0, eggplants = 0;
        int bellPeppers = 0, chiliPeppers = 0, broccoli = 0, healthy = 0;
        int floweringCount = 0, harvestReadyCount = 0;

        for (int dx = -HORIZONTAL_RADIUS; dx <= HORIZONTAL_RADIUS; dx++) {
            for (int dy = MIN_Y; dy <= MAX_Y; dy++) {
                for (int dz = -HORIZONTAL_RADIUS; dz <= HORIZONTAL_RADIUS; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    // Do not force-load chunks just to inspect a greenhouse.
                    if (!level.hasChunkAt(pos)) {
                        continue;
                    }
                    var state = level.getBlockState(pos);

                    if (state.is(ModBlocks.IRRIGATION_SPRINKLER.get())) {
                        sprinklers++;
                    } else if (state.is(ModBlocks.DRIP_IRRIGATION.get())) {
                        drippers++;
                    } else if (state.is(ModBlocks.PURPLE_GROW_LIGHT.get())) {
                        growLights++;
                    } else if (state.is(ModBlocks.GREENHOUSE_FAN.get())) {
                        fans++;
                    }

                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof IrrigationSprinklerBlockEntity sprinkler) {
                        water += sprinkler.getStoredWater();
                        capacity += sprinkler.getCapacity();
                    } else if (entity instanceof DripIrrigationBlockEntity drip) {
                        water += drip.getStoredWater();
                        capacity += drip.getCapacity();
                    }

                    if (!(entity instanceof CropPlantBlockEntity crop)) {
                        continue;
                    }

                    PlantData plant = crop.getPlantData();
                    if (!SUPPORTED_CROPS.contains(plant.getDefinition().getId())) {
                        continue;
                    }

                    plants++;
                    if (plant.isFlowering()) floweringCount++;
                    if (plant.isHarvestReady()) harvestReadyCount++;
                    switch (plant.getDefinition().getId()) {
                        case "tomato" -> tomatoes++;
                        case "cucumber" -> cucumbers++;
                        case "zucchini" -> zucchinis++;
                        case "eggplant" -> eggplants++;
                        case "bell_pepper" -> bellPeppers++;
                        case "chili_pepper" -> chiliPeppers++;
                        case "broccoli" -> broccoli++;
                        default -> { }
                    }
                    moistureTotal += plant.getMoisture();
                    healthTotal += plant.getHealth();
                    skyLightTotal += level.getBrightness(LightLayer.SKY, pos);
                    blockLightTotal += level.getBrightness(LightLayer.BLOCK, pos);
                    boolean inGreenhouse = GreenhouseEnvironment.isInGreenhouse(
                            level, pos, plant.getDefinition());
                    growthMultiplierTotal += PurpleGrowLightEnvironment
                            .getGrowthMultiplier(
                                    level, pos, plant.getDefinition(), inGreenhouse
                            );
                    float ambientTemperature =
                            level.getBiome(pos).value().getBaseTemperature();
                    // Calculate the effective temperature once per plant.
                    // This also performs the nearby-fan scan, so reusing the
                    // result avoids repeating an expensive world scan.
                    float effectiveTemperature = GreenhouseEnvironment.getTemperature(
                            level, pos, ambientTemperature, inGreenhouse,
                            plant.getDefinition()
                    );
                    temperatureTotal += effectiveTemperature;

                    if (plant.isDead() || plant.getHealth() <= 50
                            || plant.getMoisture() <= 30) {
                        needsCare++;
                    }
                    if (plant.getMoisture() <= 30) {
                        drought++;
                    }
                    // Count thermal stress using the same effective temperature
                    // and crop-specific limits as the growth simulation.
                    if (effectiveTemperature > plant.getDefinition().getMaxTemperature()) {
                        overheated++;
                    } else if (effectiveTemperature < plant.getDefinition().getMinTemperature()) {
                        cold++;
                    }
                    if (plant.getCondition() == PlantCondition.HEALTHY
                            && plant.getHealth() <= 50) {
                        weak++;
                    }
                    if (plant.getCondition() == PlantCondition.HEALTHY && plant.getHealth() > 50) {
                        healthy++;
                    }
                    if (plant.getCondition() == PlantCondition.DEAD) {
                        dead++;
                    } else if (plant.getCondition() == PlantCondition.WILTING) {
                        wilting++;
                    } else if (plant.getCondition() == PlantCondition.DRY) {
                        dry++;
                    }
                    if (level.getBrightness(LightLayer.SKY, pos) < 8
                            && level.getBrightness(LightLayer.BLOCK, pos) < 8) {
                        lowLight++;
                    }
                }
            }
        }

        if (plants == 0) {
            return new GreenhouseInspectionData(
                    0, 0, 0, 0, 0, sprinklers, drippers, growLights,
                    fans, water, capacity, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0f, 1.0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
            );
        }

        return new GreenhouseInspectionData(
                plants,
                moistureTotal / plants,
                healthTotal / plants,
                skyLightTotal / plants,
                blockLightTotal / plants,
                sprinklers,
                drippers,
                growLights,
                fans,
                water,
                capacity,
                needsCare,
                dead,
                wilting,
                dry,
                lowLight,
                weak,
                overheated,
                cold,
                drought,
                (float) (temperatureTotal / plants),
                growthMultiplierTotal / plants,
                tomatoes, cucumbers, zucchinis, eggplants,
                bellPeppers, chiliPeppers, broccoli, healthy,
                floweringCount, harvestReadyCount
        );
    }

    /**
     * Returns the highest-priority greenhouse issue for the player.
     * Dead crops are reported first, followed by water and temperature stress.
     */
    public String getPriorityAlertKey() {
        if (deadPlants > 0) return "dead";
        if (droughtPlants > 0) return "drought";
        if (overheatedPlants > 0) return "overheated";
        if (coldPlants > 0) return "cold";
        if (lowLightPlants > 0) return "low_light";
        if (weakPlants > 0) return "weak_plants";
        if (wiltingPlants > 0 || dryPlants > 0) return "general_care";
        return "good";
    }

    public int getPriorityAlertCount() {
        return switch (getPriorityAlertKey()) {
            case "dead" -> deadPlants;
            case "drought" -> droughtPlants;
            case "overheated" -> overheatedPlants;
            case "cold" -> coldPlants;
            case "low_light" -> lowLightPlants;
            case "weak_plants" -> weakPlants;
            case "general_care" -> wiltingPlants + dryPlants;
            default -> 0;
        };
    }

    public int getWaterPercent() {
        return totalWaterCapacity <= 0
                ? 0
                : (int) Math.round(storedWater * 100.0 / totalWaterCapacity);
    }
}
