package com.realisticplants;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = RealisticPlants.MOD_ID, value = Dist.CLIENT)
public final class RealisticPlantsClient {

    private static final Map<GreenhouseFanBlockEntity, FanHumSound> FAN_SOUNDS =
            new WeakHashMap<>();

    private RealisticPlantsClient() {}

    /**
     * Keeps one looping positional hum for each active fan on the client.
     * This method is called only on the client distribution, after the block entity's client-side check.
     */
    public static void updateFanAudio(
            GreenhouseFanBlockEntity fan,
            boolean active
    ) {
        if (fan == null) {
            return;
        }

        FanHumSound current = FAN_SOUNDS.get(fan);
        if (!active) {
            if (current != null) {
                current.fadeOut();
            }
            return;
        }

        if (current == null || current.isStopped()) {
            BlockPos pos = fan.getBlockPos();
            FanHumSound sound = new FanHumSound(
                    ModSounds.GREENHOUSE_FAN_HUM.get(), pos
            );
            FAN_SOUNDS.put(fan, sound);
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    private static final class FanHumSound extends AbstractTickableSoundInstance {
        private final BlockPos fanPos;
        private static final int FADE_TICKS = 10;
        private int fadeInTicks;
        private int fadeOutTicks = -1;
        private boolean fadeOutRequested;

        private FanHumSound(SoundEvent event, BlockPos pos) {
            super(event, SoundSource.BLOCKS, RandomSource.create());
            this.fanPos = pos.immutable();
            this.looping = true;
            this.delay = 0;
            this.volume = 0.0f;
            this.pitch = 1.0f;
            this.x = pos.getX() + 0.5;
            this.y = pos.getY() + 0.5;
            this.z = pos.getZ() + 0.5;
            this.relative = false;
            this.attenuation = Attenuation.LINEAR;
        }

        public void fadeOut() {
            if (!fadeOutRequested) {
                fadeOutRequested = true;
                fadeOutTicks = FADE_TICKS;
            }
        }

        @Override
        public void tick() {
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            if (level == null || !level.hasChunkAt(fanPos)) {
                stop();
                return;
            }

            BlockState state = level.getBlockState(fanPos);
            if (!state.is(ModBlocks.GREENHOUSE_FAN.get())
                    || !state.getValue(GreenhouseFanBlock.ACTIVE)) {
                fadeOut();
            }

            if (fadeOutRequested) {
                volume = Math.max(0.0f, volume * (fadeOutTicks - 1) / Math.max(1, fadeOutTicks));
                fadeOutTicks--;
                if (fadeOutTicks <= 0) {
                    stop();
                }
                return;
            }

            if (fadeInTicks < FADE_TICKS) {
                fadeInTicks++;
            }
            float fadeIn = fadeInTicks / (float) FADE_TICKS;

            // Several nearby fans should create a fuller ambience, not a
            // volume spike. Normalize each source by the square root of the
            // number of active fan loops audible in the same dimension.
            int nearbyFans = 1;
            for (Map.Entry<GreenhouseFanBlockEntity, FanHumSound> entry
                    : FAN_SOUNDS.entrySet()) {
                GreenhouseFanBlockEntity otherFan = entry.getKey();
                FanHumSound otherSound = entry.getValue();
                if (otherFan == null || otherSound == this
                        || otherSound.isStopped()
                        || otherFan.getLevel() != level) {
                    continue;
                }

                if (otherSound.fanPos.distSqr(fanPos) <= 256.0) {
                    nearbyFans++;
                }
            }
            this.volume = (0.22f / (float) Math.sqrt(nearbyFans)) * fadeIn;
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                GreenhouseFanBlockEntity.TYPE.get(),
                GreenhouseFanRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.GREENHOUSE_CONTROL_PANEL.get(), GreenhouseControlPanelScreen::new);
    }
}
