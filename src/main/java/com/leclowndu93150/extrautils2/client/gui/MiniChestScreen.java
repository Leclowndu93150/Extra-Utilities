package com.leclowndu93150.extrautils2.client.gui;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.gui.MiniChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MiniChestScreen extends XUBaseScreen<MiniChestMenu> {
    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");

    public MiniChestScreen(MiniChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        imageWidth = MiniChestMenu.GUI_W;
        imageHeight = MiniChestMenu.GUI_H;
        inventoryLabelX = MiniChestMenu.PLAYER_INV_X;
        inventoryLabelY = MiniChestMenu.PLAYER_INV_Y;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBasicBackground(graphics, GUI_BASE, leftPos, topPos, imageWidth, imageHeight);
        drawSlotBackground(graphics, GUI_WIDGETS, MiniChestMenu.SLOT_X, MiniChestMenu.SLOT_Y);
        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, MiniChestMenu.PLAYER_INV_X, MiniChestMenu.PLAYER_INV_Y);
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
