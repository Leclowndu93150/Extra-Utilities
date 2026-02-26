package com.leclowndu93150.extrautils2.client.gui;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.gui.HasEnergyBar;
import com.leclowndu93150.extrautils2.gui.HasFluidBar;
import com.leclowndu93150.extrautils2.gui.HasMachinePreview;
import com.leclowndu93150.extrautils2.gui.HasProgressArrow;
import com.leclowndu93150.extrautils2.gui.HasRedstoneControl;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.leclowndu93150.extrautils2.util.StringHelper;
import net.minecraft.ChatFormatting;
import com.leclowndu93150.extrautils2.gui.XUBaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
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

    private static final int PREVIEW_W = 50;
    private static final int PREVIEW_H = 50;
    private static final ResourceLocation GUI_BASE_TEX = ResourceLocation.fromNamespaceAndPath(
            ExtraUtilities.MODID, "textures/block/gui_base.png");

    protected void drawMachinePreviewIfPresent(GuiGraphics graphics) {
        if (!(menu instanceof HasMachinePreview preview)) return;
        int x = leftPos + (imageWidth - PREVIEW_W) / 2;
        int y = topPos + 16;

        var atlas = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite base = atlas.apply(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID,
                "block/" + preview.getPreviewBaseTexture()));
        TextureAtlasSprite front = atlas.apply(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID,
                "block/" + preview.getPreviewFrontTexture()));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int tint = preview.getPreviewTint();
        if (tint != -1) {
            float r = ((tint >> 16) & 0xFF) / 255f;
            float g = ((tint >> 8) & 0xFF) / 255f;
            float b = (tint & 0xFF) / 255f;
            graphics.setColor(r, g, b, 1f);
        }
        graphics.blit(x, y, 0, PREVIEW_W, PREVIEW_H, base);
        graphics.setColor(1f, 1f, 1f, 1f);
        graphics.blit(x, y, 0, PREVIEW_W, PREVIEW_H, front);

        graphics.setColor(1f, 1f, 1f, 0.9f);
        graphics.blit(GUI_BASE_TEX, x, y, 103f, 103f, PREVIEW_W, PREVIEW_H, 256, 256);
        graphics.setColor(1f, 1f, 1f, 1f);

        RenderSystem.disableBlend();
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

    private static final Component SHOW_RECIPES_HINT = Component.translatable("jei.tooltip.show.recipes").withStyle(ChatFormatting.BLUE);

    protected void renderProgressArrowTooltipIfPresent(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!(menu instanceof HasProgressArrow arrow)) return;
        int x = leftPos + arrow.getArrowX();
        int y = topPos + arrow.getArrowY();
        if (mouseX < x || mouseX >= x + ARROW_W || mouseY < y || mouseY >= y + ARROW_H) return;
        List<Component> lines;
        if (arrow.isArrowOverloaded()) {
            lines = new ArrayList<>(arrow.getArrowErrorTooltip());
        } else {
            var base = arrow.getArrowTooltip();
            lines = base != null ? new ArrayList<>(base) : new ArrayList<>();
        }
        lines.add(SHOW_RECIPES_HINT);
        graphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
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
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, rs.getRedstoneButtonId());
                }
                return true;
            }
        }
        if (button == 0 && menu instanceof HasFluidBar fluid) {
            int x = leftPos + fluid.getFluidBarX();
            int y = topPos + fluid.getFluidBarY();
            if (mouseX >= x && mouseX < x + FLUID_W && mouseY >= y && mouseY < y + FLUID_H) {
                if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, fluid.getFluidBarButtonId());
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

    private static final int ENERGY_U_EMPTY  = 160, ENERGY_V_EMPTY  = 0;
    private static final int ENERGY_U_FILLED = 178, ENERGY_V_FILLED = 0;
    private static final int ENERGY_W = 18,  ENERGY_H = 54;

    protected void drawEnergyBarIfPresent(GuiGraphics graphics, ResourceLocation widgetsTexture) {
        if (!(menu instanceof HasEnergyBar energy)) return;
        int x = leftPos + energy.getEnergyBarX();
        int y = topPos + energy.getEnergyBarY();
        int stored = energy.getEnergyStored();
        int capacity = energy.getEnergyCapacity();
        int level = (capacity > 0) ? 1 + Math.round((float) stored / capacity * 52f) : 0;
        int emptyH = ENERGY_H - level;
        graphics.blit(widgetsTexture, x, y, (float) ENERGY_U_EMPTY, (float) ENERGY_V_EMPTY, ENERGY_W, emptyH, 256, 256);
        if (level > 0) {
            graphics.blit(widgetsTexture, x, y + emptyH, (float) ENERGY_U_FILLED, (float) (ENERGY_V_FILLED + emptyH), ENERGY_W, level, 256, 256);
        }
    }

    protected void renderEnergyBarTooltipIfPresent(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!(menu instanceof HasEnergyBar energy)) return;
        int x = leftPos + energy.getEnergyBarX();
        int y = topPos + energy.getEnergyBarY();
        if (mouseX < x || mouseX >= x + ENERGY_W || mouseY < y || mouseY >= y + ENERGY_H) return;
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(StringHelper.format(energy.getEnergyStored()) + " / " + StringHelper.format(energy.getEnergyCapacity()) + " RF"));
        graphics.renderTooltip(font, tooltip, Optional.<TooltipComponent>empty(), mouseX, mouseY);
    }

    private static final int FLUID_BG_U = 32, FLUID_BG_V = 0, FLUID_W = 18, FLUID_H = 33;
    private static final int FLUID_BORDER_U = 18, FLUID_BORDER_V = 0, FLUID_BORDER_W = 7;

    protected void drawFluidBarIfPresent(GuiGraphics graphics, ResourceLocation widgetsTexture) {
        if (!(menu instanceof HasFluidBar fluid)) return;
        int x = leftPos + fluid.getFluidBarX();
        int y = topPos + fluid.getFluidBarY();
        graphics.blit(widgetsTexture, x, y, (float) FLUID_BG_U, (float) FLUID_BG_V, FLUID_W, FLUID_H, 256, 256);
        int amt = fluid.getFluidAmount();
        int cap = fluid.getFluidCapacity();
        if (amt > 0 && cap > 0) {
            int fillH = Math.max(1, amt * (FLUID_H - 2) / cap);
            FluidStack stack = fluid.getFluidStack();
            if (!stack.isEmpty()) {
                renderFluid(graphics, stack, x + 1, y + 1 + (FLUID_H - 2 - fillH), FLUID_W - 2, fillH);
            }
        }
        graphics.blit(widgetsTexture, x + FLUID_W - FLUID_BORDER_W - 1, y + 1, (float) (FLUID_BORDER_U + FLUID_BORDER_W), (float) FLUID_BORDER_V, FLUID_BORDER_W, FLUID_H - 2, 256, 256);
        graphics.blit(widgetsTexture, x + 1, y + 1, (float) FLUID_BORDER_U, (float) FLUID_BORDER_V, FLUID_BORDER_W, FLUID_H - 2, 256, 256);
    }

    protected void renderFluidBarTooltipIfPresent(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!(menu instanceof HasFluidBar fluid)) return;
        int x = leftPos + fluid.getFluidBarX();
        int y = topPos + fluid.getFluidBarY();
        if (mouseX < x || mouseX >= x + FLUID_W || mouseY < y || mouseY >= y + FLUID_H) return;
        FluidStack stack = fluid.getFluidStack();
        List<Component> tooltip = new ArrayList<>();
        if (!stack.isEmpty()) {
            tooltip.add(Component.literal(stack.getDisplayName().getString()));
            tooltip.add(Component.literal(fluid.getFluidAmount() + " / " + fluid.getFluidCapacity() + " mB"));
        } else {
            tooltip.add(Component.literal("Empty (" + fluid.getFluidCapacity() + " mB)"));
        }
        graphics.renderTooltip(font, tooltip, Optional.<TooltipComponent>empty(), mouseX, mouseY);
    }

    protected void renderFluid(GuiGraphics graphics, FluidStack fluid, int x, int y, int w, int h) {
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
