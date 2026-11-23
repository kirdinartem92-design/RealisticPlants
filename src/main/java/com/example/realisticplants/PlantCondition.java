package com.realisticplants;

import net.minecraft.util.StringRepresentable;

/**
 * Represents the current condition of a plant.
 */
public enum PlantCondition implements StringRepresentable {

    HEALTHY,
    WILTING,
    DRY,
    DEAD;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
