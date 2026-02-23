package com.leclowndu93150.extrautils2.client.gui;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.gui.HasProgressArrow;
import com.leclowndu93150.extrautils2.gui.HasRedstoneControl;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import com.leclowndu93150.extrautils2.gui.XUBaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public abstract class XUBaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected static final int SCREEN_INV_SLOT_X_OFFSET = 0;
    protected static final int SCREEN_INV_SLOT_Y_OFFSET = 0;
    private static final ResourceLocation UPGRADE_SLOT_BG = ResourceLocation.fromNamespaceAndPath(
            ExtraUtilities.MODID, "textures/gui/upgrade_speed_skeleton.png");
    private static final int ARROW_BG_U = 98;
    private static final int ARROW_BG_V = 0;
    private static final int ARROW_FILL_U = 98;
    private static final int ARROW_FILL_V = 16;
    private static final int ARROW_ERR_U = 98;
    private static final int ARROW_ERR_V = 32;
    private static final int ARROW_W = 22;
    private static final int ARROW_H = 16;
    private static final ResourceLocation BUTTON_SPRITE = ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation BUTTON_HOVER_SPRITE = ResourceLocation.withDefaultNamespace("widget/button_highlighted");
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

    protected void drawProgressArrowIfPresent(GuiGraphics graphics, ResourceLocation widgetsTexture) {
        if (!(menu instanceof HasProgressArrow arrow)) return;
        int x = leftPos + arrow.getArrowX();
        int y = topPos + arrow.getArrowY();
        if (arrow.isArrowOverloaded()) {
            graphics.blit(widgetsTexture, x, y, ARROW_ERR_U, ARROW_ERR_V, ARROW_W, ARROW_H, 256, 256);
            return;
        }
        graphics.blit(widgetsTexture, x, y, ARROW_BG_U, ARROW_BG_V, ARROW_W, ARROW_H, 256, 256);
        float progress = arrow.getArrowProgress();
        if (progress > 0f) {
            int w = 1 + Math.round(progress * 21f);
            graphics.blit(widgetsTexture, x, y, ARROW_FILL_U, ARROW_FILL_V, w, ARROW_H, 256, 256);
        }
    }

    protected void renderProgressArrowTooltipIfPresent(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!(menu instanceof HasProgressArrow arrow)) return;
        int x = leftPos + arrow.getArrowX();
        int y = topPos + arrow.getArrowY();
        if (mouseX < x || mouseX >= x + ARROW_W || mouseY < y || mouseY >= y + ARROW_H) return;
        if (arrow.isArrowOverloaded()) {
            graphics.renderTooltip(font, arrow.getArrowErrorTooltip(), Optional.empty(), mouseX, mouseY);
        } else {
            var tooltip = arrow.getArrowTooltip();
            if (tooltip != null && !tooltip.isEmpty()) {
                graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    protected void drawRedstoneControlIfPresent(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!(menu instanceof HasRedstoneControl rs)) return;
        int x = leftPos + rs.getRedstoneX();
        int y = topPos + rs.getRedstoneY();
        boolean hovered = mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18;
        drawVanillaButton18(graphics, x, y, hovered);
        ItemStack icon = getRedstoneIcon(rs.getRedstoneState());
        if (!icon.isEmpty()) {
            graphics.renderItem(icon, x + 1, y + 1);
        }
    }

    protected void renderRedstoneControlTooltipIfPresent(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!(menu instanceof HasRedstoneControl rs)) return;
        int x = leftPos + rs.getRedstoneX();
        int y = topPos + rs.getRedstoneY();
        if (mouseX < x || mouseX >= x + 18 || mouseY < y || mouseY >= y + 18) return;
        graphics.renderTooltip(font, getRedstoneTooltip(rs.getRedstoneState()), mouseX, mouseY);
    }

    private static void drawVanillaButton18(GuiGraphics graphics, int x, int y, boolean hovered) {
        ResourceLocation sprite = hovered ? BUTTON_HOVER_SPRITE : BUTTON_SPRITE;
        graphics.blitSprite(sprite, x, y, 18, 18);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && menu instanceof HasRedstoneControl rs) {
            int x = leftPos + rs.getRedstoneX();
            int y = topPos + rs.getRedstoneY();
            if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, rs.getRedstoneButtonId());
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static ItemStack getRedstoneIcon(RedstoneState state) {
        return switch (state) {
            case OPERATE_ALWAYS -> new ItemStack(Items.GUNPOWDER);
            case OPERATE_REDSTONE_ON -> new ItemStack(Items.REDSTONE);
            case OPERATE_REDSTONE_OFF -> new ItemStack(Items.REDSTONE_TORCH);
            case OPERATE_REDSTONE_PULSE -> new ItemStack(Items.REPEATER);
        };
    }

    private static Component getRedstoneTooltip(RedstoneState state) {
        return switch (state) {
            case OPERATE_ALWAYS -> Component.translatable("tooltip.extrautils2.redstone.always_on");
            case OPERATE_REDSTONE_ON -> Component.translatable("tooltip.extrautils2.redstone.on");
            case OPERATE_REDSTONE_OFF -> Component.translatable("tooltip.extrautils2.redstone.off");
            case OPERATE_REDSTONE_PULSE -> Component.translatable("tooltip.extrautils2.redstone.pulse");
        };
    }
}
