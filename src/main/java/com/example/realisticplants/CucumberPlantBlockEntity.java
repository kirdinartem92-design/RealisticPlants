
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

public class CucumberPlantBlockEntity extends CropPlantBlockEntity {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    RealisticPlants.MOD_ID
            );

    public static final Supplier<BlockEntityType<CucumberPlantBlockEntity>> TYPE =
            BLOCK_ENTITY_TYPES.register(
                    "cucumber_plant",
                    () -> new BlockEntityType<>(
                            CucumberPlantBlockEntity::new,
                            ModBlocks.CUCUMBER_PLANT.get()
                    )
            );

    public CucumberPlantBlockEntity(BlockPos pos, BlockState state) {
        super(
                TYPE.get(),
                pos,
                state,
                PlantRegistry.get("cucumber")
        );
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return CucumberPlantBlock.AGE;
    }

    @Override
    protected EnumProperty<PlantCondition> getConditionProperty() {
        return CucumberPlantBlock.CONDITION;
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            CucumberPlantBlockEntity plant
    ) {
        if (plant == null) {
            return;
        }

        plant.tickPlant(level, pos, state);
    }
}
