package com.leclowndu93150.extrautils2.client.gui.transfer;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.gui.XUBaseScreen;
import com.leclowndu93150.extrautils2.gui.transfer.FluidTransferNodeMenu;
import com.leclowndu93150.extrautils2.registry.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;
import java.util.Optional;

public class FluidTransferNodeScreen extends XUBaseScreen<FluidTransferNodeMenu> {
    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");
    private static final ResourceLocation FILTER_SKELETON = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/filter_skeleton.png");
    private static final int TEXT_COLOR = 4210752;

    public FluidTransferNodeScreen(FluidTransferNodeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = FluidTransferNodeMenu.GUI_W;
        this.imageHeight = FluidTransferNodeMenu.GUI_H;
        this.inventoryLabelX = FluidTransferNodeMenu.PLAYER_INV_X;
        this.inventoryLabelY = FluidTransferNodeMenu.PLAYER_INV_Y;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBasicBackground(graphics, GUI_BASE, leftPos, topPos, imageWidth, imageHeight);
        drawFluidBarIfPresent(graphics, GUI_WIDGETS);

        drawSlotBackground(graphics, GUI_WIDGETS, FluidTransferNodeMenu.FILTER_X, FluidTransferNodeMenu.FILTER_Y);
        if (!menu.slots.get(menu.getFilterSlotIndex()).hasItem()) {
            graphics.blit(FILTER_SKELETON, leftPos + FluidTransferNodeMenu.FILTER_X + 1, topPos + FluidTransferNodeMenu.FILTER_Y + 1,
                    0f, 0f, 16, 16, 16, 16);
        }

        drawUpgradeSlotBackgroundIfPresent(graphics, GUI_WIDGETS);
        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, FluidTransferNodeMenu.PLAYER_INV_X, FluidTransferNodeMenu.PLAYER_INV_Y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, FluidTransferNodeMenu.TITLE_X, FluidTransferNodeMenu.TITLE_Y, TEXT_COLOR, false);
        Component fluidText = menu.getFluidText();
        if (!fluidText.getString().isEmpty()) {
            graphics.drawString(font, fluidText,
                    FluidTransferNodeMenu.TEXT_CENTER_X - font.width(fluidText) / 2,
                    FluidTransferNodeMenu.FLUID_TEXT_Y, TEXT_COLOR, false);
        }
        graphics.drawString(font, menu.getPingText(),
                FluidTransferNodeMenu.TEXT_CENTER_X - font.width(menu.getPingText()) / 2,
                FluidTransferNodeMenu.PING_Y, TEXT_COLOR, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_COLOR, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        int filterX = leftPos + FluidTransferNodeMenu.FILTER_X;
        int filterY = topPos + FluidTransferNodeMenu.FILTER_Y;
        if (!menu.slots.get(menu.getFilterSlotIndex()).hasItem()
                && mouseX >= filterX && mouseX < filterX + 18
                && mouseY >= filterY && mouseY < filterY + 18) {
            graphics.renderTooltip(font, List.of(ModItems.FILTER_FLUID.get().getDefaultInstance().getHoverName()),
                    Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderFluidBarTooltipIfPresent(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
