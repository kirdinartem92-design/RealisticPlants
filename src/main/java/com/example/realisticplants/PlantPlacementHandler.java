package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Prevents placing any block directly above a Realistic Plants crop.
 * The crop itself is preserved; placement is rejected before the world changes.
 */
public final class PlantPlacementHandler {

    private PlantPlacementHandler() {
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        BlockPos placedPos = event.getPos();
        BlockPos cropPos = placedPos.below();
        BlockState belowState = event.getLevel().getBlockState(cropPos);

        if (belowState.getBlock() instanceof CropPlantBlock) {
            event.setCanceled(true);
        }
    }
}
