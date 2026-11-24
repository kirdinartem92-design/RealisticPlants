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

/**
 * One shared block entity type for the five configurable vegetable crops.
 */
public class NewCropPlantBlockEntity extends CropPlantBlockEntity {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    RealisticPlants.MOD_ID
            );

    public static final Supplier<BlockEntityType<NewCropPlantBlockEntity>> TYPE =
            BLOCK_ENTITY_TYPES.register(
                    "new_crop_plant",
                    () -> new BlockEntityType<>(
                            NewCropPlantBlockEntity::new,
                            ModBlocks.ZUCCHINI_PLANT.get(),
                            ModBlocks.EGGPLANT_PLANT.get(),
                            ModBlocks.BELL_PEPPER_PLANT.get(),
                            ModBlocks.CHILI_PEPPER_PLANT.get(),
                            ModBlocks.BROCCOLI_PLANT.get(),
                            ModBlocks.POTATO_PLANT.get(),
                            ModBlocks.CARROT_PLANT.get(),
                            ModBlocks.BEETROOT_PLANT.get()
                    )
            );

    public NewCropPlantBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state, getDefinition(state));
    }

    private static PlantDefinition getDefinition(BlockState state) {
        if (state.is(ModBlocks.ZUCCHINI_PLANT.get())) {
            return PlantRegistry.get("zucchini");
        }
        if (state.is(ModBlocks.EGGPLANT_PLANT.get())) {
            return PlantRegistry.get("eggplant");
        }
        if (state.is(ModBlocks.BELL_PEPPER_PLANT.get())) {
            return PlantRegistry.get("bell_pepper");
        }
        if (state.is(ModBlocks.CHILI_PEPPER_PLANT.get())) {
            return PlantRegistry.get("chili_pepper");
        }
        if (state.is(ModBlocks.BROCCOLI_PLANT.get())) {
            return PlantRegistry.get("broccoli");
        }

        if (state.is(ModBlocks.POTATO_PLANT.get())) return PlantRegistry.get("potato");
        if (state.is(ModBlocks.CARROT_PLANT.get())) return PlantRegistry.get("carrot");
        if (state.is(ModBlocks.BEETROOT_PLANT.get())) return PlantRegistry.get("beetroot");

        throw new IllegalArgumentException(
                "Unknown configurable crop block: " + state.getBlock()
        );
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return CropPlantBlock.AGE;
    }

    @Override
    protected EnumProperty<PlantCondition> getConditionProperty() {
        return CropPlantBlock.CONDITION;
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            NewCropPlantBlockEntity plant
    ) {
        if (plant != null) {
            plant.tickPlant(level, pos, state);
        }
    }
}
