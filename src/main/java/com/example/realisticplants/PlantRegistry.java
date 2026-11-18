
package com.realisticplants;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Central registry of all plant species.
 */
public final class PlantRegistry {

    private static final Map<String, PlantDefinition> PLANTS =
            new LinkedHashMap<>();

    static {
        register(new PlantDefinition(
                "tomato",
                6,
                1200,
                1,
                0.15f,
                1.6f,
                20,
                20,
                true
        ));
    }

    private PlantRegistry() {
    }

    /**
     * Registers a new plant species.
     */
    public static void register(PlantDefinition definition) {
        Objects.requireNonNull(definition, "Plant definition cannot be null");

        if (PLANTS.containsKey(definition.getId())) {
            throw new IllegalArgumentException(
                    "Plant already registered: " + definition.getId()
            );
        }

        PLANTS.put(definition.getId(), definition);
    }

    /**
     * Gets a plant definition by its identifier.
     */
    public static PlantDefinition get(String id) {
        return PLANTS.get(id);
    }

    /**
     * Returns all registered plant species.
     */
    public static Collection<PlantDefinition> getAll() {
        return Collections.unmodifiableCollection(
                PLANTS.values()
        );
    }

    /**
     * Checks whether a plant species is registered.
     */
    public static boolean contains(String id) {
        return PLANTS.containsKey(id);
    }
}
