package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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

    private final PlantData plantData = new PlantData();

    private int tickCounter = 0;
    private int growthCounter = 0;

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

        plant.tickCounter++;

        if (plant.tickCounter >= 1200) {
            plant.tickCounter = 0;

            PlantGrowthManager.drySoil(
                    plant.plantData,
                    1
            );

            PlantGrowthManager.updateHealth(
                    plant.plantData
            );

            plant.updateGrowth(level, pos, state);
            plant.setChanged();
        }
    }

    private void updateGrowth(
            Level level,
            BlockPos pos,
            BlockState state
    ) {
        int moisture = plantData.getMoisture();
        int health = plantData.getHealth();
        int age = state.getValue(TomatoPlantBlock.AGE);

        if (moisture < 20 || health < 30) {
            return;
        }

        if (age < 4) {
            growthCounter++;

            if (growthCounter >= 5) {
                growthCounter = 0;

                level.setBlock(
                        pos,
                        state.setValue(TomatoPlantBlock.AGE, age + 1),
                        3
                );
            }

            return;
        }

        if (age == 4 && !plantData.isFlowering()) {
            PlantHarvest.startFlowering(plantData);
        }

        if (age < 6) {
            growthCounter++;

            if (growthCounter >= 5) {
                growthCounter = 0;

                int nextAge = age + 1;

                level.setBlock(
                        pos,
                        state.setValue(TomatoPlantBlock.AGE, nextAge),
                        3
                );

                if (nextAge == 6) {
                    PlantHarvest.produceHarvest(plantData);
                }
            }
        }
    }

    /**
     * Called after harvesting to allow another crop cycle.
     */
    public void resetHarvestCycle() {
        plantData.setHarvestReady(false);
        plantData.setFlowering(false);
        growthCounter = 0;

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
        output.putBoolean("flowering", plantData.isFlowering());
        output.putBoolean("harvest_ready", plantData.isHarvestReady());
        output.putInt("tick_counter", tickCounter);
        output.putInt("growth_counter", growthCounter);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        plantData.setMoisture(input.getIntOr("moisture", 100));
        plantData.setHealth(input.getIntOr("health", 100));
        plantData.setFlowering(input.getBooleanOr("flowering", false));
        plantData.setHarvestReady(input.getBooleanOr("harvest_ready", false));

        tickCounter = input.getIntOr("tick_counter", 0);
        growthCounter = input.getIntOr("growth_counter", 0);
    }
}
