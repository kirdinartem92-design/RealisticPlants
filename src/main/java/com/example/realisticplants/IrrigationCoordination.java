package com.realisticplants;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Prevents multiple irrigation devices from watering the same crop
 * during one world tick. State is scoped to each Level and discarded
 * automatically when the level is no longer referenced.
 */
public final class IrrigationCoordination {

    private static final Map<Level, TickClaims> CLAIMS = new WeakHashMap<>();

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato",
            "cucumber",
            "zucchini",
            "eggplant",
            "bell_pepper",
            "chili_pepper",
            "broccoli"
    );

    private IrrigationCoordination() {
    }

    /**
     * Returns a crop-specific irrigation target. Each crop receives a small
     * safety margin of 20 points above its configured minimum moisture.
     * The shared upper limit of 80 prevents overwatering without forcing
     * every crop to use the same target.
     */
    /**
     * Keeps the irrigation devices limited to the seven new crop species.
     * Centralizing this list prevents the drip unit and sprinkler from
     * drifting apart when support for another crop is added.
     */
    public static boolean supportsIrrigation(PlantData plant) {
        return plant != null
                && plant.getDefinition() != null
                && SUPPORTED_CROPS.contains(plant.getDefinition().getId());
    }

    public static int getTargetMoisture(PlantData plant) {
        if (plant == null || plant.getDefinition() == null) {
            return 40;
        }

        int minimum = plant.getDefinition().getMinimumMoisture();
        return Math.max(0, Math.min(80, minimum + 20));
    }

    public static synchronized boolean tryClaim(
            Level level,
            long gameTime,
            BlockPos cropPos
    ) {
        if (level == null || cropPos == null) {
            return false;
        }

        TickClaims claims = CLAIMS.get(level);
        if (claims == null || claims.gameTime != gameTime) {
            claims = new TickClaims(gameTime);
            CLAIMS.put(level, claims);
        }

        return claims.positions.add(cropPos.asLong());
    }

    private static final class TickClaims {
        private final long gameTime;
        private final Set<Long> positions = new HashSet<>();

        private TickClaims(long gameTime) {
            this.gameTime = gameTime;
        }
    }
}
