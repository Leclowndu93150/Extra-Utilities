package com.leclowndu93150.extrautils2.client.gui;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

public class MachineGeneratorScreen extends XUBaseScreen<MachineGeneratorMenu> {

    private static final ResourceLocation GUI_BASE    = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 166;
    private static final int PREVIEW_W = 50;
    private static final int PREVIEW_H = 50;

    private static final int SLOT_U  = 0,   SLOT_V  = 0,  SLOT_W  = 18, SLOT_H  = 18;

    public MachineGeneratorScreen(MachineGeneratorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth  = GUI_W;
        this.imageHeight = GUI_H;
        this.inventoryLabelX = menu.getPlayerInvX();
        this.inventoryLabelY = GUI_H - 95;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawBasicBackground(graphics, GUI_BASE, leftPos, topPos, GUI_W, GUI_H);

        MachineGeneratorType type = menu.getGeneratorType();

        int slots = menu.tile.getInventory().getSlots();
        int slotStartX = leftPos + menu.getSlotStartX();
        int slotY = topPos + menu.getSlotY();

        drawMachinePreview(graphics, type);
        drawEnergyBarIfPresent(graphics, GUI_WIDGETS);
        if (type != null && type.usesFluid()) {
            drawFluidBarIfPresent(graphics, GUI_WIDGETS);
        }

        for (int i = 0; i < slots; i++) {
            graphics.blit(GUI_WIDGETS, slotStartX + i * (SLOT_W + 2), slotY, (float) SLOT_U, (float) SLOT_V, SLOT_W, SLOT_H, 256, 256);
        }

        drawUpgradeSlotBackgroundIfPresent(graphics, GUI_WIDGETS);
        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, menu.getPlayerInvX(), menu.getPlayerInvY());
        drawUpgradeSlotBackgroundIfPresent(graphics, GUI_WIDGETS);
        drawRedstoneControlIfPresent(graphics, mouseX, mouseY);
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
        renderEnergyBarTooltipIfPresent(graphics, mouseX, mouseY);
        if (menu.getGeneratorType() != null && menu.getGeneratorType().usesFluid()) {
            renderFluidBarTooltipIfPresent(graphics, mouseX, mouseY);
        }
        renderRedstoneControlTooltipIfPresent(graphics, mouseX, mouseY);
        renderProgressArrowTooltipIfPresent(graphics, mouseX, mouseY);
    }

    private void drawMachinePreview(GuiGraphics graphics, MachineGeneratorType type) {
        if (type == null) return;
        int x = leftPos + (GUI_W - PREVIEW_W) / 2;
        int y = topPos + 16;

        var atlas = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite base = atlas.apply(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "machine/machine_base_white"));
        TextureAtlasSprite front = atlas.apply(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID,
                menu.tile.getBlockState().getValue(MachineGeneratorBlock.POWERED)
                        ? type.getOnFrontTexture()
                        : "machine/generator_off"));

        int color = type.color;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        graphics.setColor(r, g, b, 1f);
        graphics.blit(x, y, 0, PREVIEW_W, PREVIEW_H, base);
        graphics.setColor(1f, 1f, 1f, 1f);
        graphics.blit(x, y, 0, PREVIEW_W, PREVIEW_H, front);

        graphics.setColor(1f, 1f, 1f, 0.9f);
        graphics.blit(GUI_BASE, x, y, 103f, 103f, PREVIEW_W, PREVIEW_H, 256, 256);
        graphics.setColor(1f, 1f, 1f, 1f);
    }
}
