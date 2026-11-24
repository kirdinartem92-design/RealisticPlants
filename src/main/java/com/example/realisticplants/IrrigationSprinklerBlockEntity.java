package com.realisticplants;

import java.util.Set;
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
 * Water tank and periodic sprinkler for the seven crops added by this mod.
 * Vanilla Minecraft crops are deliberately excluded.
 */
public class IrrigationSprinklerBlockEntity extends BlockEntity {

    private static final int CAPACITY = 4000;
    private static final int WATER_PER_MOISTURE_POINT = 10;
    private static final int MOISTURE_PER_CYCLE = 2;
    private static final int RADIUS = 3;
    private static final long CYCLE_TICKS = 100L;

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato",
            "cucumber",
            "zucchini",
            "eggplant",
            "bell_pepper",
            "chili_pepper",
            "broccoli"
    );

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    RealisticPlants.MOD_ID
            );

    public static final Supplier<BlockEntityType<IrrigationSprinklerBlockEntity>> TYPE =
            BLOCK_ENTITY_TYPES.register(
                    "irrigation_sprinkler",
                    () -> new BlockEntityType<>(
                            IrrigationSprinklerBlockEntity::new,
                            ModBlocks.IRRIGATION_SPRINKLER.get()
                    )
            );

    private int storedWater;

    public IrrigationSprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public int getStoredWater() {
        return storedWater;
    }

    public int getCapacity() {
        return CAPACITY;
    }

    public int addWater(int amount) {
        if (amount <= 0) {
            return 0;
        }

        int accepted = Math.min(amount, CAPACITY - storedWater);
        if (accepted > 0) {
            storedWater += accepted;
            setChanged();
        }
        return accepted;
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            IrrigationSprinklerBlockEntity sprinkler
    ) {
        if (level.isClientSide()
                || sprinkler == null
                || sprinkler.storedWater <= 0
                || level.getGameTime() % CYCLE_TICKS != 0) {
            return;
        }

        int waterUsed = 0;

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx * dx + dz * dz > RADIUS * RADIUS) {
                    continue;
                }

                BlockPos cropPos = pos.offset(dx, 1, dz);
                BlockEntity candidate = level.getBlockEntity(cropPos);

                if (!(candidate instanceof CropPlantBlockEntity crop)) {
                    continue;
                }

                PlantData plant = crop.getPlantData();
                if (plant.isDead()
                        || !SUPPORTED_CROPS.contains(
                                plant.getDefinition().getId())) {
                    continue;
                }

                int moistureNeeded = Math.min(
                        MOISTURE_PER_CYCLE,
                        100 - plant.getMoisture()
                );
                int cost = moistureNeeded * WATER_PER_MOISTURE_POINT;

                if (moistureNeeded <= 0 || sprinkler.storedWater < cost) {
                    continue;
                }

                PlantGrowthManager.water(plant, moistureNeeded);
                sprinkler.storedWater -= cost;
                waterUsed += cost;
                crop.setChanged();
            }
        }

        if (waterUsed > 0) {
            sprinkler.setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("stored_water", storedWater);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedWater = Math.max(
                0,
                Math.min(CAPACITY, input.getIntOr("stored_water", 0))
        );
    }
}
