package com.realisticplants;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Independently updates a grow light every second. The lamp works around
 * supported crops in open fields as well as inside greenhouses.
 */
public class PurpleGrowLightBlockEntity extends BlockEntity {

    private static final long UPDATE_INTERVAL = 20L;
    private static final int RADIUS = 4;
    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato", "cucumber", "zucchini", "eggplant",
            "bell_pepper", "chili_pepper", "broccoli"
    );

    private static final DeferredRegister<BlockEntityType<?>> TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RealisticPlants.MOD_ID);

    public static final Supplier<BlockEntityType<PurpleGrowLightBlockEntity>> TYPE =
            TYPES.register("purple_grow_light", () ->
                    new BlockEntityType<>(
                            PurpleGrowLightBlockEntity::new,
                            ModBlocks.PURPLE_GROW_LIGHT.get()
                    ));

    public PurpleGrowLightBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    public static void tick(
            Level level, BlockPos pos, BlockState state,
            PurpleGrowLightBlockEntity light
    ) {
        if (level.isClientSide() || light == null
                || Math.floorMod(level.getGameTime(), UPDATE_INTERVAL) != 0) {
            return;
        }

        boolean foundCrop = false;
        int radiusSquared = RADIUS * RADIUS;
        for (int dx = -RADIUS; dx <= RADIUS && !foundCrop; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS && !foundCrop; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }

                    BlockPos cropPos = pos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(cropPos)
                            || !(level.getBlockEntity(cropPos)
                                    instanceof CropPlantBlockEntity crop)) {
                        continue;
                    }

                    if (!SUPPORTED_CROPS.contains(
                            crop.getPlantData().getDefinition().getId())) {
                        continue;
                    }

                    foundCrop = true;
                    PurpleGrowLightEnvironment.updateGrowLights(
                            level, cropPos, false
                    );
                    break;
                }
            }
        }

        if (!foundCrop && state.getValue(PurpleGrowLightBlock.ACTIVE)) {
            level.setBlock(
                    pos,
                    state.setValue(PurpleGrowLightBlock.ACTIVE, false),
                    3
            );
        }
    }
}