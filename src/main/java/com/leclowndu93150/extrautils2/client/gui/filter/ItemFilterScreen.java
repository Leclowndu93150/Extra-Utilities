package com.leclowndu93150.extrautils2.client.gui.filter;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.gui.XUBaseScreen;
import com.leclowndu93150.extrautils2.gui.filter.ItemFilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class ItemFilterScreen extends XUBaseScreen<ItemFilterMenu> {
    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");
    private static final int BUTTON_TEXT_COLOR = 0x202020;
    private static final int BUTTON_TEXT_PADDING = 4;

    public ItemFilterScreen(ItemFilterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = ItemFilterMenu.GUI_W;
        this.imageHeight = ItemFilterMenu.GUI_H;
        this.inventoryLabelX = ItemFilterMenu.PLAYER_INV_X;
        this.inventoryLabelY = ItemFilterMenu.PLAYER_INV_Y;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBasicBackground(graphics, GUI_BASE, leftPos, topPos, imageWidth, imageHeight);

        for (int i = 0; i < 16; i++) {
            drawSlotBackground(graphics, GUI_WIDGETS, ItemFilterMenu.GRID_X + (i % 4) * 18, ItemFilterMenu.GRID_Y + (i / 4) * 18);
        }
        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, ItemFilterMenu.PLAYER_INV_X, ItemFilterMenu.PLAYER_INV_Y);
        drawButton(graphics, mouseX, mouseY, 1, ItemFilterMenu.LEFT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_1_Y);
        drawButton(graphics, mouseX, mouseY, 2, ItemFilterMenu.RIGHT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_1_Y);
        drawButton(graphics, mouseX, mouseY, 3, ItemFilterMenu.LEFT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_2_Y);
        drawButton(graphics, mouseX, mouseY, 4, ItemFilterMenu.RIGHT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_2_Y);
    }

    private void drawButton(GuiGraphics graphics, int mouseX, int mouseY, int id, int x, int y) {
        boolean hovered = mouseX >= leftPos + x && mouseX < leftPos + x + ItemFilterMenu.BUTTON_W
                && mouseY >= topPos + y && mouseY < topPos + y + ItemFilterMenu.BUTTON_H;
        int fill = hovered ? 0xFFB6B6B6 : 0xFFA0A0A0;
        graphics.fill(leftPos + x, topPos + y, leftPos + x + ItemFilterMenu.BUTTON_W, topPos + y + ItemFilterMenu.BUTTON_H, fill);
        graphics.renderOutline(leftPos + x, topPos + y, ItemFilterMenu.BUTTON_W, ItemFilterMenu.BUTTON_H, 0xFF404040);
        drawButtonLabel(graphics, menu.getButtonLabel(id), leftPos + x, topPos + y, ItemFilterMenu.BUTTON_W, ItemFilterMenu.BUTTON_H);
    }

    private void drawButtonLabel(GuiGraphics graphics, Component label, int x, int y, int width, int height) {
        int textWidth = font.width(label);
        int maxWidth = width - BUTTON_TEXT_PADDING * 2;
        float scale = textWidth > maxWidth ? (float) maxWidth / (float) textWidth : 1.0F;
        float scaledWidth = textWidth * scale;
        float scaledHeight = font.lineHeight * scale;
        float drawX = x + (width - scaledWidth) / 2.0F;
        float drawY = y + (height - scaledHeight) / 2.0F;

        graphics.pose().pushPose();
        graphics.pose().translate(drawX, drawY, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.drawString(font, label, 0, 0, BUTTON_TEXT_COLOR, false);
        graphics.pose().popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 5, 5, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 || button == 1) {
            if (clickButton(mouseX, mouseY, 1, ItemFilterMenu.LEFT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_1_Y)) return true;
            if (clickButton(mouseX, mouseY, 2, ItemFilterMenu.RIGHT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_1_Y)) return true;
            if (clickButton(mouseX, mouseY, 3, ItemFilterMenu.LEFT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_2_Y)) return true;
            if (clickButton(mouseX, mouseY, 4, ItemFilterMenu.RIGHT_BUTTON_X, ItemFilterMenu.BUTTON_ROW_2_Y)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickButton(double mouseX, double mouseY, int id, int x, int y) {
        if (mouseX < leftPos + x || mouseX >= leftPos + x + ItemFilterMenu.BUTTON_W
                || mouseY < topPos + y || mouseY >= topPos + y + ItemFilterMenu.BUTTON_H) {
            return false;
        }
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
