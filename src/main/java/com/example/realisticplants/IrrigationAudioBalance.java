package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Softens overlapping irrigation sound effects in densely equipped areas.
 * Only nearby, stocked irrigation devices are counted; vanilla blocks and
 * crops are never modified.
 */
public final class IrrigationAudioBalance {

    private static final int HORIZONTAL_RADIUS = 6;
    private static final int VERTICAL_RADIUS = 2;

    private IrrigationAudioBalance() {
    }

    public static float getVolume(Level level, BlockPos source, float baseVolume) {
        if (level == null || source == null || baseVolume <= 0.0f) {
            return Math.max(0.0f, baseVolume);
        }

        int stockedDevices = 0;
        for (int dx = -HORIZONTAL_RADIUS; dx <= HORIZONTAL_RADIUS; dx++) {
            for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
                for (int dz = -HORIZONTAL_RADIUS; dz <= HORIZONTAL_RADIUS; dz++) {
                    if (dx * dx + dz * dz > HORIZONTAL_RADIUS * HORIZONTAL_RADIUS) {
                        continue;
                    }

                    BlockPos checkPos = source.offset(dx, dy, dz);
                    if (!level.hasChunkAt(checkPos)) {
                        continue;
                    }

                    BlockEntity candidate = level.getBlockEntity(checkPos);
                    if (candidate instanceof IrrigationSprinklerBlockEntity sprinkler
                            && sprinkler.getStoredWater() > 0) {
                        stockedDevices++;
                    } else if (candidate instanceof DripIrrigationBlockEntity drip
                            && drip.getStoredWater() > 0) {
                        stockedDevices++;
                    }
                }
            }
        }

        int deviceCount = Math.max(1, stockedDevices);
        return baseVolume / (float) Math.sqrt(deviceCount);
    }
}
