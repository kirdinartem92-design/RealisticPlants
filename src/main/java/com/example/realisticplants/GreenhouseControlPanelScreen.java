package com.example.realisticplants;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GreenhouseControlPanelScreen extends AbstractContainerScreen<GreenhouseControlPanelMenu> {
    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_HEIGHT = 230;

    public GreenhouseControlPanelScreen(
            GreenhouseControlPanelMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title, PANEL_WIDTH, PANEL_HEIGHT);
        this.titleLabelX = 0;
        this.titleLabelY = 1000;
        this.inventoryLabelY = 1000;
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x - 1, y - 1, x + PANEL_WIDTH + 1, y + PANEL_HEIGHT + 1, 0xFF171D25);
        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, 0xFF293542);
        graphics.fill(x + 8, y + 8, x + PANEL_WIDTH - 8, y + 30, 0xFF394B5C);
        graphics.fill(x + 10, y + 36, x + 145, y + 145, 0xFF202B35);
        graphics.fill(x + 153, y + 36, x + PANEL_WIDTH - 10, y + 145, 0xFF202B35);
        graphics.fill(x + 10, y + 151, x + PANEL_WIDTH - 10, y + PANEL_HEIGHT - 10, 0xFF18242E);
        graphics.fill(x + 10, y + 32, x + PANEL_WIDTH - 10, y + 34, 0xFFA56BE8);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        centered(graphics, Component.translatable("gui.realisticplants.greenhouse.title"),
                x + PANEL_WIDTH / 2, y + 14, 0xFFFFFFFF);

        section(graphics, x + 18, y + 43, "gui.realisticplants.greenhouse.crops");
        line(graphics, x + 18, y + 58, "gui.realisticplants.greenhouse.plants", value(GreenhouseControlPanelMenu.PLANTS));
        line(graphics, x + 18, y + 72, "gui.realisticplants.greenhouse.care", value(GreenhouseControlPanelMenu.NEEDS_CARE));
        line(graphics, x + 18, y + 91, "gui.realisticplants.greenhouse.moisture", value(GreenhouseControlPanelMenu.MOISTURE) + "%");
        bar(graphics, x + 18, y + 104, 112, value(GreenhouseControlPanelMenu.MOISTURE), 100, 0xFF4C9FE8);
        line(graphics, x + 18, y + 116, "gui.realisticplants.greenhouse.health", value(GreenhouseControlPanelMenu.HEALTH) + "%");
        bar(graphics, x + 18, y + 129, 112, value(GreenhouseControlPanelMenu.HEALTH), 100, 0xFF65C982);

        section(graphics, x + 161, y + 43, "gui.realisticplants.greenhouse.equipment");
        line(graphics, x + 161, y + 58, "gui.realisticplants.greenhouse.sprinklers", value(GreenhouseControlPanelMenu.SPRINKLERS));
        line(graphics, x + 161, y + 72, "gui.realisticplants.greenhouse.drippers", value(GreenhouseControlPanelMenu.DRIPPERS));
        line(graphics, x + 161, y + 86, "gui.realisticplants.greenhouse.lamps", value(GreenhouseControlPanelMenu.GROW_LIGHTS));
        line(graphics, x + 161, y + 100, "gui.realisticplants.greenhouse.fans", value(GreenhouseControlPanelMenu.FANS));
        int stored = menu.getInt32(GreenhouseControlPanelMenu.STORED_WATER, GreenhouseControlPanelMenu.STORED_WATER_HIGH);
        int capacity = menu.getInt32(GreenhouseControlPanelMenu.WATER_CAPACITY, GreenhouseControlPanelMenu.WATER_CAPACITY_HIGH);
        line(graphics, x + 161, y + 119, "gui.realisticplants.greenhouse.reservoir", stored + " / " + capacity);
        bar(graphics, x + 161, y + 132, 118, stored, capacity, 0xFF4C9FE8);

        section(graphics, x + 18, y + 158, "gui.realisticplants.greenhouse.crop_breakdown");
        cropCount(graphics, x + 18, y + 174, "gui.realisticplants.greenhouse.tomato_short", GreenhouseControlPanelMenu.TOMATOES);
        cropCount(graphics, x + 57, y + 174, "gui.realisticplants.greenhouse.cucumber_short", GreenhouseControlPanelMenu.CUCUMBERS);
        cropCount(graphics, x + 96, y + 174, "gui.realisticplants.greenhouse.zucchini_short", GreenhouseControlPanelMenu.ZUCCHINIS);
        cropCount(graphics, x + 135, y + 174, "gui.realisticplants.greenhouse.eggplant_short", GreenhouseControlPanelMenu.EGGPLANTS);
        cropCount(graphics, x + 174, y + 174, "gui.realisticplants.greenhouse.bell_pepper_short", GreenhouseControlPanelMenu.BELL_PEPPERS);
        cropCount(graphics, x + 213, y + 174, "gui.realisticplants.greenhouse.chili_short", GreenhouseControlPanelMenu.CHILI_PEPPERS);
        cropCount(graphics, x + 252, y + 174, "gui.realisticplants.greenhouse.broccoli_short", GreenhouseControlPanelMenu.BROCCOLI);

        section(graphics, x + 18, y + 202, "gui.realisticplants.greenhouse.condition_breakdown");
        compactCondition(graphics, x + 18, y + 216, "gui.realisticplants.greenhouse.healthy_short", GreenhouseControlPanelMenu.HEALTHY);
        compactCondition(graphics, x + 76, y + 216, "gui.realisticplants.greenhouse.weak_short", GreenhouseControlPanelMenu.WEAK);
        compactCondition(graphics, x + 134, y + 216, "gui.realisticplants.greenhouse.wilting_short", GreenhouseControlPanelMenu.WILTING);
        compactCondition(graphics, x + 192, y + 216, "gui.realisticplants.greenhouse.dry_short", GreenhouseControlPanelMenu.DRY);
        compactCondition(graphics, x + 250, y + 216, "gui.realisticplants.greenhouse.dead_short", GreenhouseControlPanelMenu.DEAD);
    }

    private int value(int index) {
        return menu.getValue(index);
    }

    private void centered(GuiGraphicsExtractor graphics, Component text, int centerX, int y, int color) {
        graphics.text(this.font, text, centerX - this.font.width(text) / 2, y, color, true);
    }

    private void section(GuiGraphicsExtractor graphics, int x, int y, String key) {
        graphics.text(this.font, Component.translatable(key), x, y, 0xFFBFA0F4, true);
    }

    private void line(GuiGraphicsExtractor graphics, int x, int y, String key, Object value) {
        Component label = Component.translatable(key);
        graphics.text(this.font, label, x, y, 0xFFCFD8E0, false);
        graphics.text(this.font, String.valueOf(value), x + 88, y, 0xFFFFFFFF, false);
    }

    private void cropCount(GuiGraphicsExtractor graphics, int x, int y, String key, int index) {
        Component label = Component.translatable(key);
        graphics.text(this.font, label, x, y, 0xFFBFD0DE, false);
        graphics.text(this.font, Integer.toString(value(index)), x, y + 10, 0xFF8FE0A2, false);
    }

    private void compactCondition(GuiGraphicsExtractor graphics, int x, int y, String key, int index) {
        Component label = Component.translatable(key);
        graphics.text(this.font, label, x, y, 0xFFBFD0DE, false);
        int count = value(index);
        graphics.text(this.font, Integer.toString(count), x + 35, y,
                count > 0 && index != GreenhouseControlPanelMenu.HEALTHY
                        ? 0xFFFFB36B : 0xFF8FE0A2, false);
    }

    private void bar(GuiGraphicsExtractor graphics, int x, int y, int width, int amount, int maximum, int color) {
        graphics.fill(x, y, x + width, y + 5, 0xFF101820);
        int filled = maximum <= 0 ? 0 : (int) Math.max(0L, Math.min((long) width, (long) width * amount / maximum));
        if (filled > 0) graphics.fill(x, y, x + filled, y + 5, color);
    }
}
