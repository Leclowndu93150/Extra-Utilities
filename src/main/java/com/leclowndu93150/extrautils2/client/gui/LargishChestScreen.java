package com.leclowndu93150.extrautils2.client.gui;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.gui.LargishChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LargishChestScreen extends XUBaseScreen<LargishChestMenu> {
    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");

    public LargishChestScreen(LargishChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = LargishChestMenu.GUI_W;
        imageHeight = LargishChestMenu.GUI_H;
        inventoryLabelX = LargishChestMenu.PLAYER_INV_X;
        inventoryLabelY = LargishChestMenu.PLAYER_INV_Y;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBasicBackground(graphics, GUI_BASE, leftPos, topPos, imageWidth, imageHeight);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBackground(graphics, GUI_WIDGETS,
                        LargishChestMenu.SLOT_START_X + col * 18,
                        LargishChestMenu.SLOT_START_Y + row * 18);
            }
        }

        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, LargishChestMenu.PLAYER_INV_X, LargishChestMenu.PLAYER_INV_Y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 5, 5, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
