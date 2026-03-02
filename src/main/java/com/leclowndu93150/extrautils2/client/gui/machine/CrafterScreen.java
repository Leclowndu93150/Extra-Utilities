package com.leclowndu93150.extrautils2.client.gui.machine;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.gui.XUBaseScreen;
import com.leclowndu93150.extrautils2.gui.machine.CrafterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class CrafterScreen extends XUBaseScreen<CrafterMenu> {

    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 232;

    public CrafterScreen(CrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = GUI_W;
        this.imageHeight = GUI_H;
        this.inventoryLabelX = menu.getPlayerInvX();
        this.inventoryLabelY = GUI_H - 95;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBasicBackground(graphics, GUI_BASE, leftPos, topPos, GUI_W, GUI_H);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                drawSlotBackground(graphics, GUI_WIDGETS, menu.getGhostX() + col * 18, menu.getGhostY() + row * 18);
            }
        }

        drawSlotBackground(graphics, GUI_WIDGETS, menu.getGhostOutputX(), menu.getGhostOutputY());
        ItemStack ghostOutput = menu.tile.getGhostOutput();
        if (!ghostOutput.isEmpty()) {
            int gx = leftPos + menu.getGhostOutputX() + 1;
            int gy = topPos + menu.getGhostOutputY() + 1;
            graphics.renderItem(ghostOutput, gx, gy);
            graphics.renderItemDecorations(font, ghostOutput, gx, gy);
        }

        for (int i = 0; i < 9; i++) {
            drawSlotBackground(graphics, GUI_WIDGETS, menu.getInputX() + i * 18, menu.getInputY());
        }
        for (int i = 0; i < 9; i++) {
            drawSlotBackground(graphics, GUI_WIDGETS, menu.getOutputX() + i * 18, menu.getOutputY());
        }

        drawUpgradeSlotBackgroundIfPresent(graphics, GUI_WIDGETS);
        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, menu.getPlayerInvX(), menu.getPlayerInvY());
        drawRedstoneControlIfPresent(graphics, mouseX, mouseY);
        drawEnergyBarIfPresent(graphics, GUI_WIDGETS);
        drawProgressArrowIfPresent(graphics, GUI_WIDGETS);

        drawContainerModeButton(graphics, mouseX, mouseY);
    }

    private void drawContainerModeButton(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = leftPos + menu.getContainerModeX();
        int y = topPos + menu.getContainerModeY();
        boolean hovered = mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18;
        ResourceLocation sprite = hovered
                ? ResourceLocation.withDefaultNamespace("widget/button_highlighted")
                : ResourceLocation.withDefaultNamespace("widget/button");
        graphics.blitSprite(sprite, x, y, 18, 18);
        ItemStack icon = menu.getContainerMode() == 1
                ? new ItemStack(Items.CHEST)
                : new ItemStack(Items.HOPPER);
        graphics.renderItem(icon, x + 1, y + 1);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 5, 5, 0x404040, false);
        graphics.drawString(font, Component.literal("Input"), menu.getInputX(), menu.getInputY() - 9, 0x404040, false);
        graphics.drawString(font, Component.literal("Output"), menu.getOutputX(), menu.getOutputY() - 9, 0x404040, false);
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

        int bx = leftPos + menu.getContainerModeX();
        int by = topPos + menu.getContainerModeY();
        if (mouseX >= bx && mouseX < bx + 18 && mouseY >= by && mouseY < by + 18) {
            Component tip = menu.getContainerMode() == 1
                    ? Component.literal("Container items -> Output")
                    : Component.literal("Container items -> Input");
            graphics.renderTooltip(font, List.of(tip), Optional.empty(), mouseX, mouseY);
        }

        int gx = leftPos + menu.getGhostOutputX();
        int gy = topPos + menu.getGhostOutputY();
        if (mouseX >= gx && mouseX < gx + 18 && mouseY >= gy && mouseY < gy + 18) {
            ItemStack ghostOutput = menu.tile.getGhostOutput();
            if (!ghostOutput.isEmpty()) {
                graphics.renderTooltip(font, ghostOutput, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int bx = leftPos + menu.getContainerModeX();
            int by = topPos + menu.getContainerModeY();
            if (mouseX >= bx && mouseX < bx + 18 && mouseY >= by && mouseY < by + 18) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, menu.getContainerModeButtonId());
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
