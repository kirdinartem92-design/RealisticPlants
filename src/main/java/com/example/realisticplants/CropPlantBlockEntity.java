package com.realisticplants;

import net.minecraft.core.BlockPos;
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

        float temperature =
                level.getBiome(pos).value().getBaseTemperature();

        // Environmental simulation runs on its own cadence. Keeping its
        // interval equal to the old growth interval preserves current balance.
        if (moistureUpdate) {
            moistureTickCounter = 0;

            if (level.isRainingAt(pos)) {
                PlantEnvironment.applyRain(plantData, 5);
            } else {
                PlantEnvironment.applyHeat(
                        plantData,
                        temperature,
                        definition.getMoistureConsumption()
                );
            }

            PlantEnvironment.applyNearbyWater(level, pos, plantData);
            PlantGrowthManager.updateHealth(plantData);
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
