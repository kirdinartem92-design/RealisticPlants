package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Universal block entity for all plant species.
 *
 * Handles shared plant growth, moisture, health,
 * flowering, harvesting and persistent data.
 */
public abstract class CropPlantBlockEntity extends BlockEntity {

    private static final double GROWTH_REQUIRED = 10.0;

    private final PlantData plantData;
    private int tickCounter = 0;
    private int moistureTickCounter = 0;
    private double greenhouseTemperature;
    private boolean greenhouseTemperatureInitialized;

    protected CropPlantBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            PlantDefinition definition
    ) {
        super(type, pos, state);
        this.plantData = new PlantData(definition);
    }

    protected abstract IntegerProperty getAgeProperty();

    protected abstract EnumProperty<PlantCondition> getConditionProperty();

    protected int getFloweringStage() {
        return plantData.getDefinition().getFloweringStage();
    }

    protected int getRegrowthStage() {
        return plantData.getDefinition().getRegrowthStage();
    }

    public PlantData getPlantData() {
        return plantData;
    }

    /**
     * Returns the temperature currently used by this crop after greenhouse
     * thermal inertia has been applied. Before the first greenhouse update,
     * the caller-provided target is used as a safe fallback.
     */
    public float getSimulatedTemperature(float fallbackTemperature) {
        return greenhouseTemperatureInitialized
                ? (float) greenhouseTemperature
                : fallbackTemperature;
    }


    /**
     * Current visual growth stage, exposed for inspection tools.
     */
    public int getGrowthStage() {
        BlockState state = getBlockState();
        IntegerProperty ageProperty = getAgeProperty();
        return state.hasProperty(ageProperty)
                ? state.getValue(ageProperty)
                : 0;
    }

    /**
     * Progress toward the next growth stage, expressed as 0-100 percent.
     */
    public int getGrowthProgressPercent() {
        return (int) Math.max(
                0,
                Math.min(100, Math.round(plantData.getGrowthProgress() * 10.0))
        );
    }

    public void updateCondition() {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(worldPosition);
        PlantCondition condition = plantData.getCondition();
        EnumProperty<PlantCondition> property = getConditionProperty();

        if (state.getValue(property) != condition) {
            level.setBlock(
                    worldPosition,
                    state.setValue(property, condition),
                    3
            );
        }
    }

    /**
     * Performs one server-side plant tick.
     */
    public final void tickPlant(
            Level level,
            BlockPos pos,
            BlockState state
    ) {
        if (level.isClientSide()) {
            return;
        }

        // A crop cannot survive being submerged or having flowing/source water
        // occupying the plant block or the block directly above it.
        if (level.getFluidState(pos).is(FluidTags.WATER)
                || level.getFluidState(pos.above()).is(FluidTags.WATER)) {
            level.destroyBlock(pos, false);
            return;
        }

        updateCondition();
        if (plantData.isDead()) {
            return;
        }

        PlantDefinition definition = plantData.getDefinition();
        tickCounter++;
        moistureTickCounter++;

        boolean moistureUpdate =
                moistureTickCounter >= definition.getMoistureInterval();
        boolean growthUpdate =
                tickCounter >= definition.getGrowthInterval();

        if (!moistureUpdate && !growthUpdate) {
            return;
        }

        float ambientTemperature =
                level.getBiome(pos).value().getBaseTemperature();

        // Greenhouse benefits and fan cooling are restricted to the seven
        // crops added by this mod; vanilla Minecraft crops remain untouched.
        boolean inGreenhouse =
                GreenhouseEnvironment.isInGreenhouse(level, pos, definition);
        GreenhouseEnvironment.updateFans(
                level, pos, inGreenhouse, ambientTemperature
        );
        PurpleGrowLightEnvironment.updateGrowLights(
                level, pos, inGreenhouse
        );
        float targetTemperature = GreenhouseEnvironment.getTemperature(
                level,
                pos,
                ambientTemperature,
                inGreenhouse,
                definition
        );

        // Outside a greenhouse, use the biome temperature directly. This
        // prevents a plant moved out of a greenhouse from retaining its old
        // greenhouse microclimate.
        if (!inGreenhouse) {
            greenhouseTemperature = ambientTemperature;
            greenhouseTemperatureInitialized = false;
        } else if (!greenhouseTemperatureInitialized) {
            greenhouseTemperature = targetTemperature;
            greenhouseTemperatureInitialized = true;
        } else {
            // Thermal inertia is asymmetric: enclosed spaces warm gradually
            // and lose stored heat more slowly when the sun goes away. Active
            // ventilation increases the cooling response through the target
            // temperature calculated by GreenhouseEnvironment.
            double thermalInertia = GreenhouseEnvironment.getThermalInertiaFactor(
                    level, pos
            );
            double response = (targetTemperature > greenhouseTemperature
                    ? 0.16
                    : 0.10) * thermalInertia;
            greenhouseTemperature +=
                    (targetTemperature - greenhouseTemperature) * response;
        }
        float temperature = inGreenhouse
                ? (float) greenhouseTemperature
                : ambientTemperature;

        // Environmental simulation runs on its own cadence. Keeping its
        // interval equal to the old growth interval preserves current balance.
        if (moistureUpdate) {
            moistureTickCounter = 0;

            int moistureConsumption =
                    definition.getMoistureConsumption();
            if (inGreenhouse) {
                moistureConsumption = Math.max(
                        0,
                        moistureConsumption
                                - GreenhouseEnvironment.MOISTURE_SAVING
                );
            }

            // Heat and the crop's normal moisture consumption continue during
            // rain. Rain replenishes the soil afterward, so hot weather can
            // still cause evaporation without making rainfall ineffective.
            // Use the effective greenhouse temperature: fan cooling therefore
            // reduces evaporation, while greenhouse warmth can increase it.
            PlantEnvironment.applyHeat(
                    plantData,
                    temperature,
                    moistureConsumption
            );

            if (level.isRainingAt(pos)) {
                PlantEnvironment.applyRain(plantData, 5);
            }

            PlantEnvironment.applyNearbyWater(level, pos, plantData);
            PlantGrowthManager.updateHealth(plantData, temperature);
            updateCondition();
        }

        if (growthUpdate) {
            tickCounter = 0;
            BlockState currentState = level.getBlockState(pos);
            updateGrowth(level, pos, currentState, temperature);
        }

        setChanged();
    }

    private void updateGrowth(
            Level level,
            BlockPos pos,
            BlockState state,
            float temperature
    ) {
        PlantDefinition definition = plantData.getDefinition();
        IntegerProperty ageProperty = getAgeProperty();
        int age = state.getValue(ageProperty);

        if (age >= definition.getMaxGrowthStage()) {
            if (plantData.isFlowering() && !plantData.isHarvestReady()) {
                PlantHarvest.produceHarvest(plantData);
            }
            return;
        }

        double growthAmount = PlantGrowthController.getGrowthAmount(
                plantData,
                temperature
        );

        growthAmount *= PurpleGrowLightEnvironment.getGrowthMultiplier(
                level,
                pos,
                definition,
                GreenhouseEnvironment.isInGreenhouse(level, pos, definition)
        );

        if (growthAmount <= 0.0) {
            return;
        }

        double progress = plantData.getGrowthProgress() + growthAmount;
        plantData.setGrowthProgress(progress);

        if (plantData.getGrowthProgress() >= GROWTH_REQUIRED) {
            plantData.setGrowthProgress(
                    plantData.getGrowthProgress() - GROWTH_REQUIRED
            );

            int nextAge = age + 1;
            level.setBlock(pos, state.setValue(ageProperty, nextAge), 3);

            if (PlantLifecycleRules.shouldStartFlowering(
                    nextAge,
                    getFloweringStage(),
                    plantData.isFlowering()
            )) {
                PlantHarvest.startFlowering(plantData);
            }

            if (nextAge == definition.getMaxGrowthStage()) {
                PlantHarvest.produceHarvest(plantData);
            }
        }
    }

    public void resetHarvestCycle() {
        plantData.setHarvestReady(false);
        plantData.setGrowthProgress(0.0);

        // A new regrowth cycle must earn its next stage from a fresh interval.
        // Do not let time accumulated before harvesting cause near-instant growth.
        tickCounter = 0;

        if (!plantData.getDefinition().regrowsAfterHarvest()) {
            plantData.setFlowering(false);
            setChanged();
            return;
        }

        // Regrowth starts from its configured stage, but flowering must
        // happen again only after a subsequent growth-stage transition.
        plantData.setFlowering(false);

        if (level != null) {
            BlockState state = getBlockState();
            IntegerProperty ageProperty = getAgeProperty();
            int maxAge = plantData.getDefinition().getMaxGrowthStage();

            if (state.getValue(ageProperty) >= maxAge) {
                int regrowthAge = Math.max(
                        0,
                        Math.min(maxAge, getRegrowthStage())
                );
                level.setBlock(
                        worldPosition,
                        state.setValue(ageProperty, regrowthAge),
                        3
                );
            }
        }

        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("moisture", plantData.getMoisture());
        output.putInt("health", plantData.getHealth());
        output.putBoolean("flowering", plantData.isFlowering());
        output.putBoolean("harvest_ready", plantData.isHarvestReady());
        output.putInt("tick_counter", tickCounter);
        output.putInt("moisture_tick_counter", moistureTickCounter);
        output.putDouble("greenhouse_temperature", greenhouseTemperature);
        output.putBoolean("greenhouse_temperature_initialized", greenhouseTemperatureInitialized);
        output.putDouble("growth_progress", plantData.getGrowthProgress());
        output.putInt(
                "growth_counter",
                (int) Math.floor(plantData.getGrowthProgress())
        );
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        plantData.setMoisture(input.getIntOr("moisture", 100));
        plantData.setHealth(input.getIntOr("health", 100));
        plantData.setFlowering(input.getBooleanOr("flowering", false));
        plantData.setHarvestReady(input.getBooleanOr("harvest_ready", false));
        plantData.normalizeLifecycleState();

        PlantDefinition definition = plantData.getDefinition();
        tickCounter = clampCounter(
                input.getIntOr("tick_counter", 0),
                definition.getGrowthInterval()
        );

        // Old saves used one shared counter for growth and moisture.
        moistureTickCounter = clampCounter(
                input.getIntOr("moisture_tick_counter", tickCounter),
                definition.getMoistureInterval()
        );
        greenhouseTemperature = input.getDoubleOr("greenhouse_temperature", 0.0);
        greenhouseTemperatureInitialized = input.getBooleanOr(
                "greenhouse_temperature_initialized", false
        );

        double savedProgress = input.getDoubleOr(
                "growth_progress",
                input.getIntOr("growth_counter", 0)
        );
        plantData.setGrowthProgress(savedProgress);
    }

    private static int clampCounter(int counter, int interval) {
        return Math.max(0, Math.min(interval - 1, counter));
    }
}
