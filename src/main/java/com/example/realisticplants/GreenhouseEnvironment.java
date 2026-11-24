package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Lightweight greenhouse detection for the seven crops introduced by this
 * mod. A greenhouse is recognized by a glass roof and glass on all four sides.
 * Detection is performed only during the plant's existing moisture/growth
 * update, not every game tick.
 */
public final class GreenhouseEnvironment {

    public static final float TEMPERATURE_BONUS = 0.15f;
    public static final int MOISTURE_SAVING = 1;

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato",
            "cucumber",
            "zucchini",
            "eggplant",
            "bell_pepper",
            "chili_pepper",
            "broccoli"
    );

    private GreenhouseEnvironment() {
    }

    /**
     * Calculates the crop's greenhouse temperature after nearby fans run.
     * Each fan removes a small part of the greenhouse heat bonus; cooling
     * never pushes the crop below the biome's ambient temperature.
     */
    public static float getTemperature(
            Level level,
            BlockPos plantPos,
            float ambientTemperature,
            boolean inGreenhouse,
            PlantDefinition definition
    ) {
        if (!inGreenhouse || level == null || plantPos == null
                || definition == null) {
            return ambientTemperature;
        }

        int fans = 0;
        int radius = 6;
        for (int dx = -radius; dx <= radius && fans < 3; dx++) {
            for (int dy = -2; dy <= 2 && fans < 3; dy++) {
                for (int dz = -radius; dz <= radius && fans < 3; dz++) {
                    BlockPos fanPos = plantPos.offset(dx, dy, dz);
                    // Never load neighboring chunks just to locate a fan.
                    // Only already-loaded greenhouse sections can contribute
                    // to cooling during this crop update.
                    if (level.hasChunkAt(fanPos)
                            && level.getBlockState(fanPos)
                                    .is(ModBlocks.GREENHOUSE_FAN.get())) {
                        fans++;
                    }
                }
            }
        }

        float greenhouseTemperature =
                ambientTemperature + TEMPERATURE_BONUS;

        // Fans are automatic: they remain idle unless the greenhouse would
        // exceed this crop's own maximum temperature.
        if (fans == 0 || greenhouseTemperature <= definition.getMaxTemperature()) {
            return greenhouseTemperature;
        }

        float cooling = Math.min(TEMPERATURE_BONUS, fans * 0.05f);
        return Math.max(
                ambientTemperature,
                greenhouseTemperature - cooling
        );
    }

    public static boolean isInGreenhouse(
            Level level,
            BlockPos plantPos,
            PlantDefinition definition
    ) {
        if (level == null || plantPos == null || definition == null
                || !SUPPORTED_CROPS.contains(definition.getId())) {
            return false;
        }

        // A roof three blocks above the plant leaves room for mature crops.
        // Require a small connected-looking roof patch, not a lone glass block.
        if (!hasGlassRoof(level, plantPos)) {
            return false;
        }

        // Accept compact and larger greenhouse footprints. The bounded
        // directional scan avoids an unbounded flood-fill and never loads chunks.
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] direction : directions) {
            if (findWallDistance(level, plantPos, direction[0], direction[1]) < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks a 3x3 roof patch above the crop. At least five of its nine
     * positions must be glass, including the position directly above it.
     * Unloaded chunks are ignored rather than force-loaded.
     */
    private static boolean hasGlassRoof(Level level, BlockPos plantPos) {
        BlockPos center = plantPos.above(3);
        if (!level.hasChunkAt(center)
                || !isGreenhouseGlass(level.getBlockState(center))) {
            return false;
        }

        int glassCount = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos roofPos = center.offset(dx, 0, dz);
                if (level.hasChunkAt(roofPos)
                        && isGreenhouseGlass(level.getBlockState(roofPos))) {
                    glassCount++;
                }
            }
        }
        return glassCount >= 5;
    }

    /**
     * Finds a glass wall along one cardinal direction. A wall may be between
     * 2 and 8 blocks from the crop, allowing more than one greenhouse size.
     */
    private static int findWallDistance(
            Level level,
            BlockPos plantPos,
            int dx,
            int dz
    ) {
        for (int distance = 2; distance <= 8; distance++) {
            for (int y = 1; y <= 2; y++) {
                BlockPos wallPos = plantPos.offset(
                        dx * distance, y, dz * distance
                );
                if (level.hasChunkAt(wallPos)
                        && isGreenhouseGlass(level.getBlockState(wallPos))) {
                    return distance;
                }
            }
        }
        return -1;
    }

    private static boolean isGreenhouseGlass(BlockState state) {
        // Clear vanilla glass and panes are accepted. Tinted glass and
        // opaque blocks are excluded automatically.
        return state.is(Blocks.GLASS) || state.is(Blocks.GLASS_PANE);
    }
}
