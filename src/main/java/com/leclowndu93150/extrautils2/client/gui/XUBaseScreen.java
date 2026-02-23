package com.leclowndu93150.extrautils2.client.gui;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.gui.XUBaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class XUBaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected static final int SCREEN_INV_SLOT_X_OFFSET = 0;
    protected static final int SCREEN_INV_SLOT_Y_OFFSET = 0;
    private static final ResourceLocation UPGRADE_SLOT_BG = ResourceLocation.fromNamespaceAndPath(
            ExtraUtilities.MODID, "textures/gui/upgrade_speed_skeleton.png");
    protected XUBaseScreen(T menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    protected void drawBasicBackground(GuiGraphics graphics, ResourceLocation texture, int x, int y, int w, int h) {
        int w2 = w >> 1;
        int h2 = h >> 1;
        int w3 = w - w2;
        int h3 = h - h2;
        graphics.blit(texture, x, y, 0f, 0f, w2, h2, 256, 256);
        graphics.blit(texture, x + w2, y, (float) (256 - w3), 0f, w3, h2, 256, 256);
        graphics.blit(texture, x, y + h2, 0f, (float) (256 - h3), w2, h3, 256, 256);
        graphics.blit(texture, x + w2, y + h2, (float) (256 - w3), (float) (256 - h3), w3, h3, 256, 256);
    }

    protected void drawPlayerInventorySlotBackgrounds(GuiGraphics graphics, ResourceLocation widgetsTexture, int invX, int invY) {
        int x = leftPos + invX + SCREEN_INV_SLOT_X_OFFSET;
        int y = topPos + invY + SCREEN_INV_SLOT_Y_OFFSET;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                graphics.blit(widgetsTexture, x + col * 18, y + 14 + row * 18, 0f, 0f, 18, 18, 256, 256);
            }
        }
        for (int col = 0; col < 9; col++) {
            graphics.blit(widgetsTexture, x + col * 18, y + 14 + 58, 0f, 0f, 18, 18, 256, 256);
        }
    }

    protected void drawSlotBackground(GuiGraphics graphics, ResourceLocation widgetsTexture, int x, int y) {
        graphics.blit(widgetsTexture, leftPos + x, topPos + y, 0f, 0f, 18, 18, 256, 256);
    }

    protected void drawUpgradeSlotBackground(GuiGraphics graphics, ResourceLocation widgetsTexture, int slotX, int slotY) {
        graphics.blit(widgetsTexture, leftPos + slotX, topPos + slotY, 0f, 0f, 18, 18, 256, 256);
        graphics.blit(UPGRADE_SLOT_BG, leftPos + slotX + 1, topPos + slotY + 1, 0f, 0f, 16, 16, 16, 16);
    }

    protected void drawUpgradeSlotBackgroundIfPresent(GuiGraphics graphics, ResourceLocation widgetsTexture) {
        if (menu instanceof com.leclowndu93150.extrautils2.gui.HasUpgradeSlot upgrade) {
            drawUpgradeSlotBackground(graphics, widgetsTexture, upgrade.getUpgradeX(), upgrade.getUpgradeY());
        }
    }
}
