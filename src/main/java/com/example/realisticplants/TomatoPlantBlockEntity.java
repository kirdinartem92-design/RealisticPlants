
package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TomatoPlantBlockEntity extends BlockEntity {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    RealisticPlants.MOD_ID
            );

    public static final Supplier<BlockEntityType<TomatoPlantBlockEntity>> TYPE =
            BLOCK_ENTITY_TYPES.register(
                    "tomato_plant",
                    () -> new BlockEntityType<>(
                            TomatoPlantBlockEntity::new,
                            ModBlocks.TOMATO_PLANT.get()
                    )
            );

    private static final double GROWTH_REQUIRED = 10.0;

    private final PlantData plantData = new PlantData();

    private int tickCounter = 0;

    public TomatoPlantBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public PlantData getPlantData() {
        return plantData;
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            TomatoPlantBlockEntity plant
    ) {

        if (level.isClientSide() || plant.plantData.isDead()) {
            return;
        }

        PlantDefinition definition =
                plant.plantData.getDefinition();

        if (definition == null) {
            return;
        }

        plant.tickCounter++;

        if (plant.tickCounter >= definition.getGrowthInterval()) {

            plant.tickCounter = 0;

            float temperature =
                    level.getBiome(pos).value().getBaseTemperature();

            if (level.isRainingAt(pos)) {

                PlantEnvironment.applyRain(
                        plant.plantData,
                        5
                );

            } else {

                PlantEnvironment.applyHeat(
                        plant.plantData,
                        temperature,
                        definition.getMoistureConsumption()
                );
            }

            PlantGrowthManager.updateHealth(
                    plant.plantData
            );

            BlockState currentState = level.getBlockState(pos);

            plant.updateGrowth(
                    level,
                    pos,
                    currentState,
                    temperature,
                    definition
            );

            plant.setChanged();
        }
    }

    /**
     * Updates fractional growth progress and checks harvest readiness.
     */
    private void updateGrowth(
            Level level,
            BlockPos pos,
            BlockState state,
            float temperature,
            PlantDefinition definition
    ) {

        int age = state.getValue(TomatoPlantBlock.AGE);

        // A mature plant keeps checking whether it can produce fruit.
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
                    state.setValue(TomatoPlantBlock.AGE, nextAge),
                    3
            );

            if (nextAge == 4 && !plantData.isFlowering()) {

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

        PlantHarvest.startFlowering(plantData);

        if (level != null) {

            BlockState state = getBlockState();

            if (state.getValue(TomatoPlantBlock.AGE) == 6) {

                level.setBlock(
                        worldPosition,
                        state.setValue(TomatoPlantBlock.AGE, 4),
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

        // Compatibility with older saved data.
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
