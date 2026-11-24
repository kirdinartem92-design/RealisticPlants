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
 * The sound definitions currently use quiet vanilla event aliases while
 * original audio assets are being prepared. The event IDs are kept stable
 * so the audio can be replaced later without changing gameplay code.
 */
public final class ModSounds {

    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, RealisticPlants.MOD_ID);

    public static final Supplier<SoundEvent> GREENHOUSE_FAN_HUM =
            register("greenhouse_fan_hum");
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
