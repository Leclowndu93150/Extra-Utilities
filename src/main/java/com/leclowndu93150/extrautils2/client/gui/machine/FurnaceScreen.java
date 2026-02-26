package com.leclowndu93150.extrautils2.client.gui.machine;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.gui.XUBaseScreen;
import com.leclowndu93150.extrautils2.gui.machine.FurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FurnaceScreen extends XUBaseScreen<FurnaceMenu> {

    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 166;

    public FurnaceScreen(FurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = GUI_W;
        this.imageHeight = GUI_H;
        this.inventoryLabelX = menu.getPlayerInvX();
        this.inventoryLabelY = GUI_H - 95;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBasicBackground(graphics, GUI_BASE, leftPos, topPos, GUI_W, GUI_H);
        drawMachinePreviewIfPresent(graphics);

        drawSlotBackground(graphics, GUI_WIDGETS, 50, 32);
        drawSlotBackground(graphics, GUI_WIDGETS, 102, 32);

        drawUpgradeSlotBackgroundIfPresent(graphics, GUI_WIDGETS);
        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, menu.getPlayerInvX(), menu.getPlayerInvY());
        drawRedstoneControlIfPresent(graphics, mouseX, mouseY);
        drawEnergyBarIfPresent(graphics, GUI_WIDGETS);
        drawProgressArrowIfPresent(graphics, GUI_WIDGETS);
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

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        renderRedstoneControlTooltipIfPresent(graphics, mouseX, mouseY);
        renderEnergyBarTooltipIfPresent(graphics, mouseX, mouseY);
        renderProgressArrowTooltipIfPresent(graphics, mouseX, mouseY);
    }
}
