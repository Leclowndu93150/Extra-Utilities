package com.leclowndu93150.extrautils2.client.gui;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import com.leclowndu93150.extrautils2.util.StringHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MachineGeneratorScreen extends XUBaseScreen<MachineGeneratorMenu> {

    private static final ResourceLocation GUI_BASE    = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 166;
    private static final int PREVIEW_W = 50;
    private static final int PREVIEW_H = 50;

    private static final int SLOT_U  = 0,   SLOT_V  = 0,  SLOT_W  = 18, SLOT_H  = 18;
    private static final int ENERGY_U_EMPTY  = 160, ENERGY_V_EMPTY  = 0;
    private static final int ENERGY_U_FILLED = 178, ENERGY_V_FILLED = 0;
    private static final int ENERGY_W = 18,  ENERGY_H = 54;
    private static final int FLUID_BG_U = 32, FLUID_BG_V = 0, FLUID_W = 18, FLUID_H = 33;
    private static final int FLUID_BORDER_U = 18, FLUID_BORDER_V = 0, FLUID_BORDER_W = 7;
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

        int energyX = leftPos + menu.getEnergyX();
        int energyY = topPos + menu.getEnergyY();

        boolean hasFluid = type != null && type.usesFluid();
        int fluidX = leftPos + menu.getFluidX();
        int fluidY = topPos + menu.getFluidY();

        int slots = menu.tile.getInventory().getSlots();
        int slotStartX = leftPos + menu.getSlotStartX();
        int slotY = topPos + menu.getSlotY();

        drawMachinePreview(graphics, type);

        int stored = menu.getEnergyStored();
        int capacity = menu.getEnergyCapacity();
        int energyLevel = (capacity > 0) ? 1 + Math.round((float) stored / capacity * 52f) : 0;
        int emptyH = ENERGY_H - energyLevel;

        graphics.blit(GUI_WIDGETS, energyX, energyY, (float) ENERGY_U_EMPTY, (float) ENERGY_V_EMPTY, ENERGY_W, emptyH, 256, 256);
        if (energyLevel > 0) {
            graphics.blit(GUI_WIDGETS, energyX, energyY + emptyH, (float) ENERGY_U_FILLED, (float) (ENERGY_V_FILLED + emptyH), ENERGY_W, energyLevel, 256, 256);
        }

        if (hasFluid) {
            graphics.blit(GUI_WIDGETS, fluidX, fluidY, (float) FLUID_BG_U, (float) FLUID_BG_V, FLUID_W, FLUID_H, 256, 256);
            int fluidAmt = menu.getFluidAmount();
            int fluidCap = menu.getFluidCapacity();
            if (fluidAmt > 0 && fluidCap > 0) {
                int fillH = Math.max(1, fluidAmt * (FLUID_H - 2) / fluidCap);
                FluidStack fluid = menu.tile.getFluidTank() != null ? menu.tile.getFluidTank().getFluid() : FluidStack.EMPTY;
                if (!fluid.isEmpty()) {
                    renderFluid(graphics, fluid, fluidX + 1, fluidY + 1 + (FLUID_H - 2 - fillH), FLUID_W - 2, fillH);
                }
                graphics.blit(GUI_WIDGETS, fluidX + FLUID_W - FLUID_BORDER_W - 1, fluidY + 1, (float) (FLUID_BORDER_U + FLUID_BORDER_W), (float) FLUID_BORDER_V, FLUID_BORDER_W, FLUID_H - 2, 256, 256);
                graphics.blit(GUI_WIDGETS, fluidX + 1, fluidY + 1, (float) FLUID_BORDER_U, (float) FLUID_BORDER_V, FLUID_BORDER_W, FLUID_H - 2, 256, 256);
            }
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

        MachineGeneratorType type = menu.getGeneratorType();
        int energyX = leftPos + menu.getEnergyX();
        int energyY = topPos + menu.getEnergyY();

        if (mouseX >= energyX && mouseX < energyX + ENERGY_W && mouseY >= energyY && mouseY < energyY + ENERGY_H) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(StringHelper.format(menu.getEnergyStored()) + " / " + StringHelper.format(menu.getEnergyCapacity()) + " RF"));
            graphics.renderTooltip(font, tooltip, Optional.<TooltipComponent>empty(), mouseX, mouseY);
        }

        if (type != null && type.usesFluid()) {
            int fluidX = leftPos + menu.getFluidX();
            int fluidY = topPos + menu.getFluidY();
            if (mouseX >= fluidX && mouseX < fluidX + FLUID_W && mouseY >= fluidY && mouseY < fluidY + FLUID_H) {
                FluidStack fluid = menu.tile.getFluidTank() != null ? menu.tile.getFluidTank().getFluid() : FluidStack.EMPTY;
                List<Component> tooltip = new ArrayList<>();
                if (!fluid.isEmpty()) {
                    tooltip.add(Component.literal(fluid.getDisplayName().getString()));
                    tooltip.add(Component.literal(menu.getFluidAmount() + " / " + menu.getFluidCapacity() + " mB"));
                } else {
                    tooltip.add(Component.literal("Empty (" + menu.getFluidCapacity() + " mB)"));
                }
                graphics.renderTooltip(font, tooltip, Optional.<TooltipComponent>empty(), mouseX, mouseY);
            }
        }

        renderRedstoneControlTooltipIfPresent(graphics, mouseX, mouseY);
        renderProgressArrowTooltipIfPresent(graphics, mouseX, mouseY);
    }

    private void drawMachinePreview(GuiGraphics graphics, MachineGeneratorType type) {
        if (type == null) return;
        int x = leftPos + (GUI_W - PREVIEW_W) / 2;
        int y = topPos + 16;

        var atlas = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite base = atlas.apply(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "machine/machine_base"));
        TextureAtlasSprite front = atlas.apply(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID,
                menu.tile.getBlockState().getValue(MachineGeneratorBlock.POWERED)
                        ? "machine/generator_on"
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

    private void renderFluid(GuiGraphics graphics, FluidStack fluid, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) return;
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluidType());
        ResourceLocation still = ext.getStillTexture(fluid);
        if (still == null) return;
        int color = ext.getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        graphics.setColor(r, g, b, 1f);
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);
        for (int tx = x; tx < x + w; tx += 16) {
            int tw = Math.min(16, x + w - tx);
            for (int ty = y; ty < y + h; ty += 16) {
                int th = Math.min(16, y + h - ty);
                graphics.blit(tx, ty, 0, tw, th, sprite);
            }
        }
        graphics.setColor(1f, 1f, 1f, 1f);
    }

}
