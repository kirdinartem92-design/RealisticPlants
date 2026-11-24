package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Handheld tool that reports the current condition of one of this mod's
 * seven original vegetable crops.
 */
public class AgronomicAnalyzerItem extends Item {

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato",
            "cucumber",
            "zucchini",
            "eggplant",
            "bell_pepper",
            "chili_pepper",
            "broccoli"
    );

    public AgronomicAnalyzerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof GreenhouseControlPanelBlockEntity panel) {
            if (!level.isClientSide() && context.getPlayer() != null) {
                GreenhouseInspectionData data =
                        GreenhouseInspectionData.capture(level, pos);

                context.getPlayer().sendSystemMessage(
                        Component.translatable("message.realisticplants.panel.title")
                );
                context.getPlayer().sendSystemMessage(
                        Component.translatable(
                                "message.realisticplants.panel.plants",
                                data.plantCount(),
                                data.plantsNeedingCare()
                        )
                );
                context.getPlayer().sendSystemMessage(
                        Component.translatable(
                                "message.realisticplants.panel.condition",
                                data.averageHealth(),
                                data.averageMoisture()
                        )
                );
                context.getPlayer().sendSystemMessage(
                        Component.translatable(
                                "message.realisticplants.panel.light",
                                data.averageSkyLight(),
                                data.averageBlockLight(),
                                Math.round(data.averageGrowthMultiplier() * 100.0)
                        )
                );
                context.getPlayer().sendSystemMessage(
                        Component.translatable(
                                "message.realisticplants.panel.temperature",
                                String.format(java.util.Locale.ROOT, "%.3f", data.averageTemperature())
                        )
                );
                context.getPlayer().sendSystemMessage(
                        Component.translatable(
                                "message.realisticplants.panel.equipment",
                                data.sprinklerCount(),
                                data.dripIrrigationCount(),
                                data.purpleGrowLightCount(),
                                data.fanCount()
                        )
                );
                context.getPlayer().sendSystemMessage(
                        Component.translatable(
                                "message.realisticplants.panel.water",
                                data.storedWater(),
                                data.totalWaterCapacity(),
                                data.getWaterPercent()
                        )
                );
            }
            return InteractionResult.SUCCESS;
        }

        if (!(blockEntity instanceof CropPlantBlockEntity crop)) {
            return InteractionResult.PASS;
        }

        String plantId = crop.getPlantData().getDefinition().getId();
        if (!SUPPORTED_CROPS.contains(plantId)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && context.getPlayer() != null) {
            PlantInspectionData data =
                    PlantInspectionData.capture(level, pos, crop);

            Component plantName = Component.translatable(
                    "item.realisticplants." + plantId
            );

            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.title",
                            plantName
                    )
            );
            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.condition",
                            Component.translatable(
                                    "message.realisticplants.condition."
                                            + data.condition().getSerializedName()
                            ),
                            data.health(),
                            data.moisture()
                    )
            );
            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.growth",
                            data.growthStage() + 1,
                            crop.getPlantData().getDefinition().getMaxGrowthStage() + 1,
                            data.growthProgressPercent()
                    )
            );
            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.light",
                            data.skyLight(),
                            data.blockLight(),
                            Math.round(data.environmentalGrowthMultiplier() * 100.0)
                    )
            );
            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.temperature",
                            Component.translatable(
                                    "message.realisticplants.temperature."
                                            + data.getTemperatureStatus()
                            )
                    )
            );
            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.greenhouse",
                            Component.translatable(
                                    data.greenhouse()
                                            ? "message.realisticplants.yes"
                                            : "message.realisticplants.no"
                            )
                    )
            );
            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.lifecycle",
                            Component.translatable(
                                    data.flowering()
                                            ? "message.realisticplants.flowering"
                                            : "message.realisticplants.not_flowering"
                            ),
                            Component.translatable(
                                    data.harvestReady()
                                            ? "message.realisticplants.harvest_ready"
                                            : "message.realisticplants.not_ready"
                            )
                    )
            );
            context.getPlayer().sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.analyzer.advice",
                            Component.translatable(
                                    "message.realisticplants.advice."
                                            + data.getRecommendationKey()
                            )
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}
