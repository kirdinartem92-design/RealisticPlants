
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

/**
 * Stores individual tomato plant data.
 */
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

    public TomatoPlantBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public PlantData getPlantData() {
        return plantData;
    }

    /**
     * Updates plant moisture and health over time.
     */
    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            TomatoPlantBlockEntity plant
    ) {
        if (level.isClientSide()) {
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

            plant.setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("moisture", plantData.getMoisture());
        output.putInt("health", plantData.getHealth());
        output.putBoolean("flowering", plantData.isFlowering());
        output.putBoolean("harvest_ready", plantData.isHarvestReady());
        output.putInt("tick_counter", tickCounter);
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
    }
}
