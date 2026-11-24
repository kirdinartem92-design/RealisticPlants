package com.realisticplants;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Independently updates a grow light, so night lighting does not depend on
 * whether a nearby crop happens to run its growth simulation.
 */
public class PurpleGrowLightBlockEntity extends BlockEntity {

    private static final long UPDATE_INTERVAL = 20L;
    private static final int RADIUS = 4;
    private static final int NATURAL_LIGHT_THRESHOLD = 12;
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

        boolean night = level.getSkyDarken() >= 4;
        boolean insufficientDaylight = false;

        if (!night) {
            for (int dx = -RADIUS; dx <= RADIUS && !insufficientDaylight; dx++) {
                for (int dy = -RADIUS; dy <= RADIUS && !insufficientDaylight; dy++) {
                    for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                        if (dx * dx + dy * dy + dz * dz > RADIUS * RADIUS) {
                            continue;
                        }
                        BlockPos cropPos = pos.offset(dx, dy, dz);
                        if (!level.hasChunkAt(cropPos)
                                || !(level.getBlockEntity(cropPos)
                                        instanceof CropPlantBlockEntity crop)) {
                            continue;
                        }
                        if (SUPPORTED_CROPS.contains(crop.getPlantData()
                                .getDefinition().getId())
                                && level.getBrightness(LightLayer.SKY, cropPos)
                                        < NATURAL_LIGHT_THRESHOLD) {
                            insufficientDaylight = true;
                            break;
                        }
                    }
                }
            }
        }

        boolean shouldBeActive = night || insufficientDaylight;
        if (state.getValue(PurpleGrowLightBlock.ACTIVE) != shouldBeActive) {
            level.setBlock(pos,
                    state.setValue(PurpleGrowLightBlock.ACTIVE, shouldBeActive), 3);
        }
    }
}
