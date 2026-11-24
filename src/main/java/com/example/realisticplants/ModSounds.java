package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Sound event registry for greenhouse machinery and irrigation.
 *
 * The event IDs are kept stable while the sound definitions use original
 * OGG assets for the fan and irrigation equipment.
 */
public final class ModSounds {

    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, RealisticPlants.MOD_ID);

    public static final Supplier<SoundEvent> GREENHOUSE_FAN_HUM =
            register("greenhouse_fan_hum");
    public static final Supplier<SoundEvent> GREENHOUSE_FAN_START =
            register("greenhouse_fan_start");
    public static final Supplier<SoundEvent> GREENHOUSE_FAN_STOP =
            register("greenhouse_fan_stop");
    public static final Supplier<SoundEvent> SPRINKLER_SPRAY =
            register("sprinkler_spray");
    public static final Supplier<SoundEvent> DRIP_IRRIGATION =
            register("drip_irrigation");

    private ModSounds() {
    }

    private static Supplier<SoundEvent> register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(
                RealisticPlants.MOD_ID, name
        );
        return SOUND_EVENTS.register(
                name,
                () -> SoundEvent.createVariableRangeEvent(id)
        );
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
