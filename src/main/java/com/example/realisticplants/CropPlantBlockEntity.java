
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

    protected CropPlantBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            PlantDefinition definition
    ) {
        super(type, pos, state);

        this.plantData = new PlantData(definition);
    }

    /**
     * Returns the age property used by this plant block.
     */
    protected abstract IntegerProperty getAgeProperty();

    /**
     * Returns the condition property used by this plant block.
     */
    protected abstract EnumProperty<PlantCondition> getConditionProperty();

    /**
     * Returns the stage at which flowering begins.
     */
    protected int getFloweringStage() {
        return Math.max(
                0,
                plantData.getDefinition().getMaxGrowthStage() - 2
        );
    }

    /**
     * Returns the stage used after harvesting.
     */
    protected int getRegrowthStage() {
        return getFloweringStage();
    }

    public PlantData getPlantData() {
        return plantData;
    }

    /**
     * Synchronizes plant condition with block state.
     */
    public void updateCondition() {

        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(worldPosition);

        PlantCondition condition = plantData.getCondition();

        EnumProperty<PlantCondition> property =
                getConditionProperty();

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

        PlantDefinition definition =
                plantData.getDefinition();

        tickCounter++;

        if (tickCounter >= definition.getGrowthInterval()) {

            tickCounter = 0;

            float temperature =
                    level.getBiome(pos).value().getBaseTemperature();

            if (level.isRainingAt(pos)) {

                PlantEnvironment.applyRain(
                        plantData,
                        5
                );

            } else {

                PlantEnvironment.applyHeat(
                        plantData,
                        temperature,
                        definition.getMoistureConsumption()
                );
            }

            PlantGrowthManager.updateHealth(plantData);

            updateCondition();

            BlockState currentState =
                    level.getBlockState(pos);

            updateGrowth(
                    level,
                    pos,
                    currentState,
                    temperature
            );

            setChanged();
        }
    }

    /**
     * Updates fractional growth progress and harvest readiness.
     */
    private void updateGrowth(
            Level level,
            BlockPos pos,
            BlockState state,
            float temperature
    ) {

        PlantDefinition definition =
                plantData.getDefinition();

        IntegerProperty ageProperty =
                getAgeProperty();

        int age = state.getValue(ageProperty);

        if (age >= definition.getMaxGrowthStage()) {

            if (plantData.isFlowering()
                    && !plantData.isHarvestReady()) {

                PlantHarvest.produceHarvest(plantData);
            }

            return;
        }

        double growthAmount =
                PlantGrowthController.getGrowthAmount(
                        plantData,
                        temperature
                );

        if (growthAmount <= 0.0) {
            return;
        }

        double progress =
                plantData.getGrowthProgress() + growthAmount;

        plantData.setGrowthProgress(progress);

        if (plantData.getGrowthProgress() >= GROWTH_REQUIRED) {

            plantData.setGrowthProgress(
                    plantData.getGrowthProgress() - GROWTH_REQUIRED
            );

            int nextAge = age + 1;

            level.setBlock(
                    pos,
                    state.setValue(ageProperty, nextAge),
                    3
            );

            if (nextAge == getFloweringStage()
                    && !plantData.isFlowering()) {

                PlantHarvest.startFlowering(plantData);
            }

            if (nextAge == definition.getMaxGrowthStage()) {

                PlantHarvest.produceHarvest(plantData);
            }
        }
    }

    /**
     * Starts a new harvest cycle.
     */
    public void resetHarvestCycle() {

        plantData.setHarvestReady(false);
        plantData.setGrowthProgress(0.0);

        if (!plantData.getDefinition().regrowsAfterHarvest()) {

            plantData.setFlowering(false);
            setChanged();
            return;
        }

        PlantHarvest.startFlowering(plantData);

        if (level != null) {

            BlockState state = getBlockState();

            IntegerProperty ageProperty =
                    getAgeProperty();

            int maxAge =
                    plantData.getDefinition().getMaxGrowthStage();

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

        output.putBoolean(
                "flowering",
                plantData.isFlowering()
        );

        output.putBoolean(
                "harvest_ready",
                plantData.isHarvestReady()
        );

        output.putInt("tick_counter", tickCounter);

        output.putDouble(
                "growth_progress",
                plantData.getGrowthProgress()
        );

        output.putInt(
                "growth_counter",
                (int) Math.floor(plantData.getGrowthProgress())
        );
    }

    @Override
    protected void loadAdditional(ValueInput input) {

        super.loadAdditional(input);

        plantData.setMoisture(
                input.getIntOr("moisture", 100)
        );

        plantData.setHealth(
                input.getIntOr("health", 100)
        );

        plantData.setFlowering(
                input.getBooleanOr("flowering", false)
        );

        plantData.setHarvestReady(
                input.getBooleanOr("harvest_ready", false)
        );

        tickCounter = input.getIntOr("tick_counter", 0);

        double savedProgress = input.getDoubleOr(
                "growth_progress",
                input.getIntOr("growth_counter", 0)
        );

        plantData.setGrowthProgress(savedProgress);
    }
}
