package com.realisticplants;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = RealisticPlants.MOD_ID, value = Dist.CLIENT)
public final class RealisticPlantsClient {
    private RealisticPlantsClient() {}

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
