
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
        // Tomato
        register(new PlantDefinition(
                "tomato",
                6,      // Maximum growth stage
                4,      // Flowering stage
                4,      // Regrowth stage
                1200,   // Growth interval
                1,      // Moisture consumption
                0.15f,  // Minimum temperature
                1.6f,   // Maximum temperature
                20,     // Minimum moisture
                20,     // Drought resistance
                true    // Regrows after harvest
        ));

        // Cucumber
        register(new PlantDefinition(
                "cucumber",
                6,      // Maximum growth stage
                3,      // Flowering stage
                3,      // Regrowth stage
                800,    // Growth interval - faster than tomato
                1,      // Moisture consumption
                0.15f,  // Minimum temperature
                1.6f,   // Maximum temperature
                20,     // Minimum moisture
                20,     // Drought resistance
                true    // Regrows after harvest
        ));
    }

    private PlantRegistry() {
    }

    public static void register(PlantDefinition definition) {
        Objects.requireNonNull(
                definition,
                "Plant definition cannot be null"
        );

        if (PLANTS.containsKey(definition.getId())) {
            throw new IllegalArgumentException(
                    "Plant already registered: " + definition.getId()
            );
        }

        PLANTS.put(definition.getId(), definition);
    }

    public static PlantDefinition get(String id) {
        return PLANTS.get(id);
    }

    public static Collection<PlantDefinition> getAll() {
        return Collections.unmodifiableCollection(
                PLANTS.values()
        );
    }

    public static boolean contains(String id) {
        return PLANTS.containsKey(id);
    }
}
