package com.realisticplants;

import net.minecraft.world.level.block.Block;

/**
 * Purple-spectrum grow light.
 *
 * The block emits ordinary Minecraft block light and provides its plant-growth
 * bonus independently. True purple-colored illumination on nearby surfaces is
 * an optional client-side visual feature supplied by the Colored Light mod.
 */
public class PurpleGrowLightBlock extends Block {

    public PurpleGrowLightBlock(Properties properties) {
        super(properties);
    }
}
