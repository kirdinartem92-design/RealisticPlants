package com.realisticplants;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GreenhouseControlPanelScreen extends AbstractContainerScreen<GreenhouseControlPanelMenu> {
    private static final int PANEL_WIDTH = 340;
    private static final int PANEL_HEIGHT = 224;

    public GreenhouseControlPanelScreen(
            GreenhouseControlPanelMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title, PANEL_WIDTH, PANEL_HEIGHT);
        this.titleLabelX = 12;
        this.titleLabelY = 10;
        this.inventoryLabelY = PANEL_HEIGHT + 20;
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
        graphics.fill(x + 10, y + 36, x + 164, y + PANEL_HEIGHT - 10, 0xFF202B35);
        graphics.fill(x + 172, y + 36, x + PANEL_WIDTH - 10, y + PANEL_HEIGHT - 10, 0xFF202B35);
        graphics.fill(x + 10, y + 32, x + PANEL_WIDTH - 10, y + 34, 0xFFA56BE8);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.text(this.font, Component.translatable("gui.realisticplants.greenhouse.title"),
                x + 14, y + 14, 0xFFFFFFFF, true);
        graphics.text(this.font, Component.translatable("gui.realisticplants.greenhouse.live"),
                x + 268, y + 14, 0xFF9BE7B0, true);

        // Crop overview
        section(graphics, x + 18, y + 43, "gui.realisticplants.greenhouse.crops");
        line(graphics, x + 18, y + 57, "gui.realisticplants.greenhouse.plants", value(GreenhouseControlPanelMenu.PLANTS));
        line(graphics, x + 18, y + 70, "gui.realisticplants.greenhouse.care", value(GreenhouseControlPanelMenu.NEEDS_CARE));

        // Plant condition bars
        line(graphics, x + 18, y + 88, "gui.realisticplants.greenhouse.moisture", value(GreenhouseControlPanelMenu.MOISTURE) + "%");
        bar(graphics, x + 18, y + 101, 128, value(GreenhouseControlPanelMenu.MOISTURE), 100, 0xFF4C9FE8);
        line(graphics, x + 18, y + 112, "gui.realisticplants.greenhouse.health", value(GreenhouseControlPanelMenu.HEALTH) + "%");
        bar(graphics, x + 18, y + 125, 128, value(GreenhouseControlPanelMenu.HEALTH), 100, 0xFF65C982);

        line(graphics, x + 18, y + 143, "gui.realisticplants.greenhouse.sky", value(GreenhouseControlPanelMenu.SKY_LIGHT) + "/15");
        line(graphics, x + 18, y + 156, "gui.realisticplants.greenhouse.block", value(GreenhouseControlPanelMenu.BLOCK_LIGHT) + "/15");
        line(graphics, x + 18, y + 174, "gui.realisticplants.greenhouse.temperature",
                String.format(java.util.Locale.ROOT, "%.3f", value(GreenhouseControlPanelMenu.TEMPERATURE_MILLI) / 1000.0));
        line(graphics, x + 18, y + 187, "gui.realisticplants.greenhouse.growth",
                String.format(java.util.Locale.ROOT, "%.2fx", value(GreenhouseControlPanelMenu.GROWTH_PERMILLE) / 1000.0));

        // Equipment and water
        section(graphics, x + 180, y + 43, "gui.realisticplants.greenhouse.equipment");
        line(graphics, x + 180, y + 57, "gui.realisticplants.greenhouse.sprinklers", value(GreenhouseControlPanelMenu.SPRINKLERS));
        line(graphics, x + 180, y + 70, "gui.realisticplants.greenhouse.drippers", value(GreenhouseControlPanelMenu.DRIPPERS));
        line(graphics, x + 180, y + 83, "gui.realisticplants.greenhouse.lamps", value(GreenhouseControlPanelMenu.GROW_LIGHTS));
        line(graphics, x + 180, y + 96, "gui.realisticplants.greenhouse.fans", value(GreenhouseControlPanelMenu.FANS));

        section(graphics, x + 180, y + 117, "gui.realisticplants.greenhouse.water");
        int stored = value(GreenhouseControlPanelMenu.STORED_WATER);
        int capacity = value(GreenhouseControlPanelMenu.WATER_CAPACITY);
        line(graphics, x + 180, y + 132, "gui.realisticplants.greenhouse.reservoir",
                stored + " / " + capacity);
        bar(graphics, x + 180, y + 146, 138, stored, capacity, 0xFF4C9FE8);

        section(graphics, x + 180, y + 165, "gui.realisticplants.greenhouse.alerts");
        int alertY = y + 179;
        alertLine(graphics, x + 180, alertY, "gui.realisticplants.greenhouse.dead", GreenhouseControlPanelMenu.DEAD);
        alertLine(graphics, x + 180, alertY + 12, "gui.realisticplants.greenhouse.drought", GreenhouseControlPanelMenu.DROUGHT);
        alertLine(graphics, x + 180, alertY + 24, "gui.realisticplants.greenhouse.thermal",
                GreenhouseControlPanelMenu.OVERHEATED, GreenhouseControlPanelMenu.COLD);
        alertLine(graphics, x + 180, alertY + 36, "gui.realisticplants.greenhouse.light_alert",
                GreenhouseControlPanelMenu.LOW_LIGHT, GreenhouseControlPanelMenu.WEAK);
    }

    private int value(int index) {
        return menu.getValue(index);
    }

    private void section(GuiGraphicsExtractor graphics, int x, int y, String key) {
        graphics.text(this.font, Component.translatable(key), x, y, 0xFFBFA0F4, true);
    }

    private void line(GuiGraphicsExtractor graphics, int x, int y, String key, Object value) {
        graphics.text(this.font, Component.translatable(key), x, y, 0xFFCFD8E0, false);
        graphics.text(this.font, String.valueOf(value), x + 92, y, 0xFFFFFFFF, false);
    }

    private void alertLine(GuiGraphicsExtractor graphics, int x, int y, String key, int index) {
        graphics.text(this.font, Component.translatable(key), x, y, 0xFFCFD8E0, false);
        int count = value(index);
        graphics.text(this.font, Integer.toString(count), x + 124, y,
                count > 0 ? 0xFFFF7777 : 0xFF8FE0A2, false);
    }

    private void alertLine(GuiGraphicsExtractor graphics, int x, int y, String key, int first, int second) {
        graphics.text(this.font, Component.translatable(key), x, y, 0xFFCFD8E0, false);
        int count = value(first) + value(second);
        graphics.text(this.font, Integer.toString(count), x + 124, y,
                count > 0 ? 0xFFFFB36B : 0xFF8FE0A2, false);
    }

    private void bar(GuiGraphicsExtractor graphics, int x, int y, int width, int amount, int maximum, int color) {
        graphics.fill(x, y, x + width, y + 5, 0xFF101820);
        int filled = maximum <= 0 ? 0 : Math.max(0, Math.min(width, width * amount / maximum));
        if (filled > 0) {
            graphics.fill(x, y, x + filled, y + 5, color);
        }
    }
}
