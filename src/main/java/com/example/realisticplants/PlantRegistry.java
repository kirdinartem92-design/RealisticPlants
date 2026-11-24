package com.realisticplants;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Central registry of all plant species. */
public final class PlantRegistry {

    private static final Map<String, PlantDefinition> PLANTS =
            new LinkedHashMap<>();

    static {
        register(new PlantDefinition(
                "tomato", 6, 4, 4, 1200, 1200, 1,
                0.15f, 1.6f, 20, 20, true, 1, 4, 2, 0.3f));

        register(new PlantDefinition(
                "cucumber", 6, 3, 3, 800, 800, 1,
                0.15f, 1.6f, 20, 20, true, 1, 5, 1, 0.2f));

        // Root crops mature underground and do not regrow after harvesting.
        register(new PlantDefinition(
                "potato", 6, 5, 5, 1400, 1400, 1,
                0.10f, 1.5f, 25, 25, false, 2, 5, 2, 0.4f));

        register(new PlantDefinition(
                "carrot", 6, 5, 5, 1100, 1100, 1,
                0.10f, 1.5f, 25, 25, false, 2, 4, 3, 0.5f));

        register(new PlantDefinition(
                "beetroot", 6, 5, 5, 1300, 1300, 1,
                0.10f, 1.45f, 25, 25, false, 1, 3, 3, 0.45f));

        // New non-vanilla vegetable crops.
        register(new PlantDefinition(
                "zucchini", 6, 4, 3, 1000, 1000, 1,
                0.16f, 1.55f, 25, 18, true, 1, 3, 3, 0.5f));

        register(new PlantDefinition(
                "eggplant", 6, 4, 3, 1250, 1250, 1,
                0.18f, 1.65f, 25, 22, true, 1, 3, 3, 0.55f));

        register(new PlantDefinition(
                "bell_pepper", 6, 4, 3, 1150, 1150, 1,
                0.16f, 1.6f, 25, 20, true, 1, 4, 2, 0.45f));

        register(new PlantDefinition(
                "chili_pepper", 6, 4, 3, 1050, 1050, 1,
                0.18f, 1.7f, 25, 18, true, 1, 5, 2, 0.4f));

        register(new PlantDefinition(
                "broccoli", 6, 5, 5, 1450, 1450, 1,
                0.10f, 1.45f, 30, 28, false, 1, 2, 4, 0.6f));
    }

    private PlantRegistry() {
    }

    public static void register(PlantDefinition definition) {
        Objects.requireNonNull(definition, "Plant definition cannot be null");
        if (PLANTS.containsKey(definition.getId())) {
            throw new IllegalArgumentException(
                    "Plant already registered: " + definition.getId());
        }
        PLANTS.put(definition.getId(), definition);
    }

    public static PlantDefinition get(String id) {
        return PLANTS.get(id);
    }

    public static Collection<PlantDefinition> getAll() {
        return Collections.unmodifiableCollection(PLANTS.values());
    }

    public static boolean contains(String id) {
        return PLANTS.containsKey(id);
    }
}
