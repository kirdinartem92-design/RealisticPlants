
package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TomatoPlantBlockEntity extends CropPlantBlockEntity {

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

    public TomatoPlantBlockEntity(BlockPos pos, BlockState state) {

        super(
                TYPE.get(),
                pos,
                state,
                PlantRegistry.get("tomato")
        );
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return TomatoPlantBlock.AGE;
    }

    @Override
    protected EnumProperty<PlantCondition> getConditionProperty() {
        return TomatoPlantBlock.CONDITION;
    }

    /**
     * Preserves the existing tomato tick entry point.
     */
    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            TomatoPlantBlockEntity plant
    ) {

        if (plant == null) {
            return;
        }

        plant.tickPlant(level, pos, state);
    }
}
