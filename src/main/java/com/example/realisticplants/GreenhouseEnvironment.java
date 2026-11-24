package com.realisticplants;

import java.util.Set;

import net.minecraft.sounds.SoundSource;

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

    private static final float MAX_SOLAR_GAIN = 0.25f;
    private static final float MAX_FAN_COOLING = 0.20f;
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
     * Updates every fan inside the detected greenhouse. Fans start when at
     * least one supported crop would exceed its own maximum temperature
     * without cooling, and stop when all inspected crops are within range.
     */
    public static void updateFans(
            Level level,
            BlockPos plantPos,
            boolean inGreenhouse,
            float ambientTemperature
    ) {
        if (level == null || plantPos == null || !inGreenhouse) {
            return;
        }

        int north = findWallDistance(level, plantPos, 0, -1);
        int south = findWallDistance(level, plantPos, 0, 1);
        int west = findWallDistance(level, plantPos, -1, 0);
        int east = findWallDistance(level, plantPos, 1, 0);
        if (north < 0 || south < 0 || west < 0 || east < 0) {
            return;
        }

        boolean coolingNeeded = false;
        for (int dx = -west + 1; dx < east && !coolingNeeded; dx++) {
            for (int dy = -2; dy <= 2 && !coolingNeeded; dy++) {
                for (int dz = -north + 1; dz < south; dz++) {
                    BlockPos cropPos = plantPos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(cropPos)) {
                        continue;
                    }
                    if (!(level.getBlockEntity(cropPos)
                            instanceof CropPlantBlockEntity crop)) {
                        continue;
                    }

                    PlantDefinition cropDefinition =
                            crop.getPlantData().getDefinition();
                    float cropAmbientTemperature = level.getBiome(cropPos)
                            .value().getBaseTemperature();
                    if (SUPPORTED_CROPS.contains(cropDefinition.getId())
                            && getUncooledGreenhouseTemperature(
                                    level, cropPos, cropAmbientTemperature
                            ) > cropDefinition.getMaxTemperature()) {
                        coolingNeeded = true;
                        break;
                    }
                }
            }
        }

        for (int dx = -west + 1; dx < east; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -north + 1; dz < south; dz++) {
                    BlockPos fanPos = plantPos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(fanPos)) {
                        continue;
                    }

                    BlockState fanState = level.getBlockState(fanPos);
                    if (!fanState.is(ModBlocks.GREENHOUSE_FAN.get())) {
                        continue;
                    }

                    boolean active = fanState.getValue(GreenhouseFanBlock.ACTIVE);
                    if (active != coolingNeeded) {
                        level.setBlock(
                                fanPos,
                                fanState.setValue(
                                        GreenhouseFanBlock.ACTIVE, coolingNeeded
                                ),
                                3
                        );
                        level.playSound(
                                null,
                                fanPos,
                                coolingNeeded
                                        ? ModSounds.GREENHOUSE_FAN_START.get()
                                        : ModSounds.GREENHOUSE_FAN_STOP.get(),
                                SoundSource.BLOCKS,
                                0.35f,
                                coolingNeeded ? 0.9f : 0.75f
                        );
                    }
                }
            }
        }
    }

    /**
     * Calculates greenhouse temperature from ambient biome temperature,
     * available daylight, weather and active ventilation.
     *
     * This is a gameplay-oriented microclimate model, not a fluid simulation.
     * Solar gain is reduced by clouds/rain and fans remove accumulated heat
     * without cooling below the outdoor ambient temperature.
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

        float uncooledTemperature = getUncooledGreenhouseTemperature(
                level, plantPos, ambientTemperature
        );
        if (uncooledTemperature <= ambientTemperature) {
            return ambientTemperature;
        }

        int fans = countActiveGreenhouseFans(level, plantPos);
        float cooling = Math.min(MAX_FAN_COOLING, fans * 0.07f);
        return Math.max(
                ambientTemperature,
                uncooledTemperature - cooling
        );
    }

    /**
     * Estimates greenhouse solar heating from the actual sky brightness at
     * crop height. Rain and a dark sky reduce incoming solar energy.
     */
    private static float getUncooledGreenhouseTemperature(
            Level level,
            BlockPos plantPos,
            float ambientTemperature
    ) {
        if (level == null || plantPos == null) {
            return ambientTemperature;
        }

        int skyBrightness = level.getBrightness(
                net.minecraft.world.level.LightLayer.SKY,
                plantPos
        );
        float daylightFactor = Math.max(0.0f, Math.min(1.0f,
                skyBrightness / 15.0f));
        float sunFactor = Math.max(0.0f, Math.min(1.0f,
                (15.0f - level.getSkyDarken()) / 15.0f));
        float solarGain = MAX_SOLAR_GAIN * daylightFactor * sunFactor;

        if (level.isRainingAt(plantPos)) {
            solarGain *= 0.25f;
        }

        return ambientTemperature + solarGain;
    }

    private static int countActiveGreenhouseFans(Level level, BlockPos plantPos) {
        int north = findWallDistance(level, plantPos, 0, -1);
        int south = findWallDistance(level, plantPos, 0, 1);
        int west = findWallDistance(level, plantPos, -1, 0);
        int east = findWallDistance(level, plantPos, 1, 0);
        if (north < 0 || south < 0 || west < 0 || east < 0) {
            return 0;
        }

        int fans = 0;
        for (int dx = -west + 1; dx < east && fans < 3; dx++) {
            for (int dy = -2; dy <= 2 && fans < 3; dy++) {
                for (int dz = -north + 1; dz < south && fans < 3; dz++) {
                    BlockPos fanPos = plantPos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(fanPos)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(fanPos);
                    if (state.is(ModBlocks.GREENHOUSE_FAN.get())
                            && state.getValue(GreenhouseFanBlock.ACTIVE)) {
                        fans++;
                    }
                }
            }
        }
        return fans;
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

        // Find all four walls first so the roof check can use the actual
        // greenhouse footprint instead of sampling only above the crop.
        int northDistance = findWallDistance(level, plantPos, 0, -1);
        int southDistance = findWallDistance(level, plantPos, 0, 1);
        int westDistance = findWallDistance(level, plantPos, -1, 0);
        int eastDistance = findWallDistance(level, plantPos, 1, 0);

        if (northDistance < 0 || southDistance < 0
                || westDistance < 0 || eastDistance < 0) {
            return false;
        }

        // Every side must contain a substantial glass section between the
        // opposite walls, and the roof must cover the detected footprint.
        return isMostlyGlassWall(
                    level, plantPos, 0, -1, northDistance, westDistance, eastDistance)
                && isMostlyGlassWall(
                    level, plantPos, 0, 1, southDistance, westDistance, eastDistance)
                && isMostlyGlassWall(
                    level, plantPos, -1, 0, westDistance, northDistance, southDistance)
                && isMostlyGlassWall(
                    level, plantPos, 1, 0, eastDistance, northDistance, southDistance)
                && hasGlassRoof(
                    level, plantPos, northDistance, southDistance,
                    westDistance, eastDistance);
    }

    /**
     * Checks the full detected roof footprint three blocks above the crop.
     * The center and all four corners must be glass, and at least two thirds
     * of the roof area must be glass. The maximum scan is bounded to 17x17.
     * Unloaded chunks are treated as missing roof blocks, never force-loaded.
     */
    private static boolean hasGlassRoof(
            Level level,
            BlockPos plantPos,
            int northDistance,
            int southDistance,
            int westDistance,
            int eastDistance
    ) {
        BlockPos roofCenter = plantPos.above(3);
        if (!isGlassAt(level, roofCenter)
                || !isGlassAt(level, plantPos.offset(-westDistance, 3, -northDistance))
                || !isGlassAt(level, plantPos.offset(eastDistance, 3, -northDistance))
                || !isGlassAt(level, plantPos.offset(-westDistance, 3, southDistance))
                || !isGlassAt(level, plantPos.offset(eastDistance, 3, southDistance))) {
            return false;
        }

        int total = (westDistance + eastDistance + 1)
                * (northDistance + southDistance + 1);
        int glassCount = 0;

        for (int x = -westDistance; x <= eastDistance; x++) {
            for (int z = -northDistance; z <= southDistance; z++) {
                if (isGlassAt(level, plantPos.offset(x, 3, z))) {
                    glassCount++;
                }
            }
        }

        return glassCount * 3 >= total * 2;
    }

    private static boolean isGlassAt(Level level, BlockPos pos) {
        return level.hasChunkAt(pos)
                && isGreenhouseGlass(level.getBlockState(pos));
    }

    /**
     * Finds a glass wall along one cardinal direction. A wall may be between
     * 2 and 8 blocks from the crop, allowing more than one greenhouse size.
     */
    static int findWallDistance(
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

    /**
     * Verifies that a detected side is mostly glass across the greenhouse
     * width. Both ends must be glass; at least two thirds of positions must
     * contain glass at either of the two wall heights.
     */
    private static boolean isMostlyGlassWall(
            Level level,
            BlockPos plantPos,
            int dx,
            int dz,
            int wallDistance,
            int negativeExtent,
            int positiveExtent
    ) {
        int total = negativeExtent + positiveExtent + 1;
        int glassCount = 0;

        for (int offset = -negativeExtent; offset <= positiveExtent; offset++) {
            // For north/south walls, vary X; for west/east walls, vary Z.
            BlockPos base = dx != 0
                    ? plantPos.offset(dx * wallDistance, 0, offset)
                    : plantPos.offset(offset, 0, dz * wallDistance);

            boolean glassAtPosition = false;
            for (int y = 1; y <= 2; y++) {
                BlockPos wallPos = base.above(y);
                if (level.hasChunkAt(wallPos)
                        && isGreenhouseGlass(level.getBlockState(wallPos))) {
                    glassAtPosition = true;
                    break;
                }
            }

            if (glassAtPosition) {
                glassCount++;
            } else if (offset == -negativeExtent || offset == positiveExtent) {
                return false;
            }
        }

        return glassCount * 3 >= total * 2;
    }

    private static boolean isGreenhouseGlass(BlockState state) {
        // Clear vanilla glass and panes are accepted. Tinted glass and
        // opaque blocks are excluded automatically.
        return state.is(Blocks.GLASS) || state.is(Blocks.GLASS_PANE);
    }
}
