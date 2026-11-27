package com.realisticplants;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class RPShowcaseCommand {
    private static final int HALF_SIZE = 7;

    private RPShowcaseCommand() {}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("rp")
                        .then(Commands.literal("village")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> createShowcase(
                                        context.getSource().getPlayerOrException()
                                )))
        );
    }

    private static int createShowcase(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos center = findClearLocation(level, player.blockPosition());
        if (center == null) {
            player.sendSystemMessage(Component.literal(
                    "Не нашёл рядом свободную ровную площадку. Отойди от построек и повтори /rp village."
            ));
            return 0;
        }

        int floorY = center.getY() - 1;
        buildGreenhouse(level, center, floorY);

        player.teleportTo(
                center.getX() + 0.5,
                floorY + 2.0,
                center.getZ() + 11.5
        );
        player.sendSystemMessage(Component.literal(
                "Тестовая деревня Realistic Plants создана. Здесь представлены все 7 культур и оборудование теплицы."
        ));
        return Command.SINGLE_SUCCESS;
    }

    private static BlockPos findClearLocation(ServerLevel level, BlockPos origin) {
        for (int radius = 48; radius <= 256; radius += 16) {
            for (int dx = -radius; dx <= radius; dx += 16) {
                for (int dz = -radius; dz <= radius; dz += 16) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    int x = origin.getX() + dx;
                    int z = origin.getZ() + dz;
                    int y = level.getHeight(
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z
                    );
                    BlockPos candidate = new BlockPos(x, y, z);
                    if (isClearArea(level, candidate)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isClearArea(ServerLevel level, BlockPos center) {
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int dx = -HALF_SIZE; dx <= HALF_SIZE; dx++) {
            for (int dz = -HALF_SIZE; dz <= HALF_SIZE; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                int y = level.getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z
                );
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
                if (maxY - minY > 1) {
                    return false;
                }
                BlockPos ground = new BlockPos(x, y - 1, z);
                if (!level.getBlockState(ground).isFaceSturdy(
                        level, ground, Direction.UP
                )) {
                    return false;
                }
                for (int dy = 0; dy < 6; dy++) {
                    if (!level.isEmptyBlock(new BlockPos(x, y + dy, z))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void buildGreenhouse(ServerLevel level, BlockPos center, int floorY) {
        BlockState glass = Blocks.GLASS.defaultBlockState();
        BlockState floor = Blocks.OAK_PLANKS.defaultBlockState();

        // A compact 15x15 glass greenhouse, with a clear entrance on the south side.
        for (int dx = -HALF_SIZE; dx <= HALF_SIZE; dx++) {
            for (int dz = -HALF_SIZE; dz <= HALF_SIZE; dz++) {
                BlockPos floorPos = new BlockPos(
                        center.getX() + dx, floorY, center.getZ() + dz
                );
                level.setBlock(floorPos, floor, 3);

                boolean wall = Math.abs(dx) == HALF_SIZE || Math.abs(dz) == HALF_SIZE;
                if (wall) {
                    boolean entrance = dz == HALF_SIZE && Math.abs(dx) <= 1;
                    if (!entrance) {
                        level.setBlock(floorPos.above(2), glass, 3);
                        level.setBlock(floorPos.above(3), glass, 3);
                    }
                }

                // Leave gaps for the two connected grow-light panels.
                if (dx == -1 && dz == 0 || dx == 0 && dz == 0) {
                    continue;
                }
                level.setBlock(floorPos.above(4), glass, 3);
            }
        }

        // All seven new crops are planted at their mature visual stage.
        placeCrop(level, center.offset(-4, 1, -3), ModBlocks.TOMATO_PLANT.get());
        placeCrop(level, center.offset(-2, 1, -3), ModBlocks.CUCUMBER_PLANT.get());
        placeCrop(level, center.offset(0, 1, -3), ModBlocks.ZUCCHINI_PLANT.get());
        placeCrop(level, center.offset(2, 1, -3), ModBlocks.EGGPLANT_PLANT.get());
        placeCrop(level, center.offset(4, 1, -3), ModBlocks.BELL_PEPPER_PLANT.get());
        placeCrop(level, center.offset(-2, 1, 1), ModBlocks.CHILI_PEPPER_PLANT.get());
        placeCrop(level, center.offset(2, 1, 1), ModBlocks.BROCCOLI_PLANT.get());

        BlockPos sprinklerPos = center.offset(0, 1, 4);
        level.setBlock(sprinklerPos, ModBlocks.IRRIGATION_SPRINKLER.get().defaultBlockState(), 3);
        if (level.getBlockEntity(sprinklerPos) instanceof IrrigationSprinklerBlockEntity sprinkler) {
            sprinkler.addWater(4000);
        }

        level.setBlock(center.offset(0, 1, 2),
                ModBlocks.DRIP_IRRIGATION.get().defaultBlockState(), 3);
        level.setBlock(center.offset(-1, 3, 0),
                ModBlocks.PURPLE_GROW_LIGHT.get().defaultBlockState(), 3);
        level.setBlock(center.offset(0, 3, 0),
                ModBlocks.PURPLE_GROW_LIGHT.get().defaultBlockState(), 3);
        level.setBlock(center.offset(0, 2, -5),
                ModBlocks.GREENHOUSE_FAN.get().defaultBlockState(), 3);

        BlockPos panelFloor = center.offset(0, 0, HALF_SIZE + 1);
        level.setBlock(panelFloor, floor, 3);
        BlockPos panelPos = panelFloor.above();
        level.setBlock(panelPos,
                ModBlocks.GREENHOUSE_CONTROL_PANEL.get().defaultBlockState(), 3);

        // A small signpost marks the entrance without replacing village terrain.
        BlockPos lanternFloor = center.offset(0, 0, HALF_SIZE + 2);
        level.setBlock(lanternFloor, floor, 3);
        level.setBlock(lanternFloor.above(), Blocks.LANTERN.defaultBlockState(), 3);
    }

    private static void placeCrop(ServerLevel level, BlockPos pos,
                                  net.minecraft.world.level.block.Block crop) {
        level.setBlock(pos.below(), Blocks.FARMLAND.defaultBlockState(), 3);
        BlockState state = crop.defaultBlockState()
                .setValue(CropPlantBlock.AGE, 6)
                .setValue(CropPlantBlock.CONDITION, PlantCondition.HEALTHY);
        level.setBlock(pos, state, 3);
    }
}
