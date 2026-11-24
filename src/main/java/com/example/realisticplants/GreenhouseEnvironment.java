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
                    if (level.getBlockState(plantPos.offset(dx, dy, dz))
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
        BlockPos roofPos = plantPos.above(3);
        if (!isGreenhouseGlass(level.getBlockState(roofPos))) {
            return false;
        }

        // Require glass walls on all four sides at the plant's upper body.
        // The test is intentionally local and bounded; no world flood-fill.
        int[][] directions = {
                {2, 0}, {-2, 0}, {0, 2}, {0, -2}
        };

        for (int[] direction : directions) {
            boolean wallFound = false;
            for (int y = 1; y <= 2; y++) {
                BlockPos wallPos = plantPos.offset(
                        direction[0], y, direction[1]
                );
                if (isGreenhouseGlass(level.getBlockState(wallPos))) {
                    wallFound = true;
                    break;
                }
            }

            if (!wallFound) {
                return false;
            }
        }

        return true;
    }

    private static boolean isGreenhouseGlass(BlockState state) {
        // Clear vanilla glass and panes are accepted. Tinted glass and
        // opaque blocks are excluded automatically.
        return state.is(Blocks.GLASS) || state.is(Blocks.GLASS_PANE);
    }
}
