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
 * Small refillable drip unit. It waters one adjacent mod crop slowly,
 * delivering moisture directly beside the plant's root zone.
 */
public class DripIrrigationBlockEntity extends BlockEntity {

    private static final int CAPACITY = 2000;
    private static final int WATER_PER_MOISTURE_POINT = 10;
    private static final int MOISTURE_PER_CYCLE = 1;
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

    public static final Supplier<BlockEntityType<DripIrrigationBlockEntity>> TYPE =
            BLOCK_ENTITY_TYPES.register(
                    "drip_irrigation",
                    () -> new BlockEntityType<>(
                            DripIrrigationBlockEntity::new,
                            ModBlocks.DRIP_IRRIGATION.get()
                    )
            );

    private int storedWater;

    public DripIrrigationBlockEntity(BlockPos pos, BlockState state) {
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
            DripIrrigationBlockEntity emitter
    ) {
        if (level.isClientSide()
                || emitter == null
                || emitter.storedWater < WATER_PER_MOISTURE_POINT
                || level.getGameTime() % CYCLE_TICKS != 0) {
            return;
        }

        // The emitter is placed beside the crop's farmland. The plant block
        // itself is one block above that soil level.
        for (int[] direction : HORIZONTAL_DIRECTIONS) {
            BlockPos cropPos = pos.above().offset(direction[0], 0, direction[1]);
            BlockEntity candidate = level.getBlockEntity(cropPos);

            if (!(candidate instanceof CropPlantBlockEntity crop)) {
                continue;
            }

            PlantData plant = crop.getPlantData();
            if (plant.isDead()
                    || !SUPPORTED_CROPS.contains(
                            plant.getDefinition().getId())
                    || plant.getMoisture() >= 100) {
                continue;
            }

            PlantGrowthManager.water(plant, MOISTURE_PER_CYCLE);
            emitter.storedWater -= WATER_PER_MOISTURE_POINT;
            emitter.setChanged();
            crop.setChanged();
            break;
        }
    }

    private static final int[][] HORIZONTAL_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

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
