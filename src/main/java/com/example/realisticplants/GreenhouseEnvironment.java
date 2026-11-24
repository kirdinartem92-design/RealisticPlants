package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
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
        // Full glass blocks, stained glass and glass panes all work as
        // greenhouse glazing. Tinted glass is deliberately excluded because
        // it blocks light and should not qualify as a transparent roof/wall.
        return state.getBlock() instanceof AbstractGlassBlock
                && !state.is(Blocks.TINTED_GLASS);
    }
}
