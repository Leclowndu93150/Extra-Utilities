package com.leclowndu93150.extrautils2.client.gui.machine;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.blockentity.machine.AnalogCrafterBlockEntity;
import com.leclowndu93150.extrautils2.client.gui.XUBaseScreen;
import com.leclowndu93150.extrautils2.gui.machine.AnalogCrafterMenu;
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

public class AnalogCrafterScreen extends XUBaseScreen<AnalogCrafterMenu> {

    private static final ResourceLocation GUI_BASE = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_base.png");
    private static final ResourceLocation GUI_WIDGETS = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/gui_widget.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 190;

    private static final int SIDE_BTN_SIZE = 18;

    private static final int[][] SIDE_COLORS = {
            {179, 179, 179},
            {255, 255, 255},
            {255, 179, 255},
            {179, 255, 255},
            {255, 179, 179},
            {179, 179, 255},
            {179, 255, 179},
            {255, 255, 179},
    };

    private static final String[] SIDE_LABELS = {"x", "a", "D", "U", "N", "S", "W", "E"};

    public AnalogCrafterScreen(AnalogCrafterMenu menu, Inventory inv, Component title) {
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
                int slotIdx = row * 3 + col;
                int side = menu.getSlotSide(slotIdx);
                int colorIdx = sideToColorIndex(side);
                int[] rgb = SIDE_COLORS[colorIdx];

                float r = rgb[0] / 255f;
                float g = rgb[1] / 255f;
                float b = rgb[2] / 255f;
                graphics.setColor(r, g, b, 1f);
                drawSlotBackground(graphics, GUI_WIDGETS, menu.getGridX() + col * 18, menu.getGridY() + row * 18);
                graphics.setColor(1f, 1f, 1f, 1f);
            }
        }

        int sbx = menu.getSideBtnX();
        int sby = menu.getSideBtnY();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIdx = row * 3 + col;
                int side = menu.getSlotSide(slotIdx);
                int colorIdx = sideToColorIndex(side);
                int[] rgb = SIDE_COLORS[colorIdx];
                int bx = leftPos + sbx + col * SIDE_BTN_SIZE;
                int by = topPos + sby + row * SIDE_BTN_SIZE;
                boolean hovered = mouseX >= bx && mouseX < bx + SIDE_BTN_SIZE && mouseY >= by && mouseY < by + SIDE_BTN_SIZE;

                ResourceLocation sprite = hovered
                        ? ResourceLocation.withDefaultNamespace("widget/button_highlighted")
                        : ResourceLocation.withDefaultNamespace("widget/button");
                float r = rgb[0] / 255f;
                float g = rgb[1] / 255f;
                float b = rgb[2] / 255f;
                graphics.setColor(r, g, b, 1f);
                graphics.blitSprite(sprite, bx, by, SIDE_BTN_SIZE, SIDE_BTN_SIZE);
                graphics.setColor(1f, 1f, 1f, 1f);

                String label = SIDE_LABELS[colorIdx];
                int tw = font.width(label);
                graphics.drawString(font, label, bx + (SIDE_BTN_SIZE - tw) / 2, by + 5, 0x404040, false);
            }
        }

        drawSlotBackground(graphics, GUI_WIDGETS, menu.getOutputSlotX(), menu.getOutputSlotY());
        drawPlayerInventorySlotBackgrounds(graphics, GUI_WIDGETS, menu.getPlayerInvX(), menu.getPlayerInvY());
        drawRedstoneControlIfPresent(graphics, mouseX, mouseY);
        drawProgressArrowIfPresent(graphics, GUI_WIDGETS);

        drawToggleButton(graphics, mouseX, mouseY, menu.getStickyX(), menu.getStickyY(), menu.isSticky(), Items.SLIME_BALL);
        drawToggleButton(graphics, mouseX, mouseY, menu.getSpreadX(), menu.getSpreadY(), menu.isSpread(), Items.COMPARATOR);
    }

    private void drawToggleButton(GuiGraphics graphics, int mouseX, int mouseY, int bx, int by, boolean active, net.minecraft.world.level.ItemLike icon) {
        int x = leftPos + bx;
        int y = topPos + by;
        boolean hovered = mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18;
        ResourceLocation sprite = hovered
                ? ResourceLocation.withDefaultNamespace("widget/button_highlighted")
                : ResourceLocation.withDefaultNamespace("widget/button");
        graphics.blitSprite(sprite, x, y, 18, 18);
        ItemStack iconStack = new ItemStack(icon);
        if (!active) {
            graphics.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        graphics.renderItem(iconStack, x + 1, y + 1);
        graphics.setColor(1f, 1f, 1f, 1f);
    }

    private int sideToColorIndex(int side) {
        return switch (side) {
            case AnalogCrafterBlockEntity.DISABLED -> 0;
            case AnalogCrafterBlockEntity.ANY_FACE -> 1;
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 5;
            case 4 -> 6;
            case 5 -> 7;
            default -> 1;
        };
    }

    private String getSideName(int side) {
        return switch (side) {
            case AnalogCrafterBlockEntity.DISABLED -> "Disabled";
            case AnalogCrafterBlockEntity.ANY_FACE -> "All Sides";
            case 0 -> "Down";
            case 1 -> "Up";
            case 2 -> "North";
            case 3 -> "South";
            case 4 -> "West";
            case 5 -> "East";
            default -> "Unknown";
        };
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 5, 5, 0x404040, false);
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
        renderProgressArrowTooltipIfPresent(graphics, mouseX, mouseY);

        int sbx = menu.getSideBtnX();
        int sby = menu.getSideBtnY();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIdx = row * 3 + col;
                int bx = leftPos + sbx + col * SIDE_BTN_SIZE;
                int by = topPos + sby + row * SIDE_BTN_SIZE;
                if (mouseX >= bx && mouseX < bx + SIDE_BTN_SIZE && mouseY >= by && mouseY < by + SIDE_BTN_SIZE) {
                    int side = menu.getSlotSide(slotIdx);
                    graphics.renderTooltip(font, List.of(
                            Component.literal("Slot " + (slotIdx + 1) + ": " + getSideName(side)),
                            Component.literal("Click to cycle")), Optional.empty(), mouseX, mouseY);
                }
            }
        }

        renderButtonTooltip(graphics, mouseX, mouseY, menu.getStickyX(), menu.getStickyY(),
                menu.isSticky() ? "Sticky: ON" : "Sticky: OFF", "Keep at least 1 item per slot");
        renderButtonTooltip(graphics, mouseX, mouseY, menu.getSpreadX(), menu.getSpreadY(),
                menu.isSpread() ? "Spread: ON" : "Spread: OFF", "Distribute items evenly");
    }

    private void renderButtonTooltip(GuiGraphics graphics, int mouseX, int mouseY, int bx, int by, String line1, String line2) {
        int x = leftPos + bx;
        int y = topPos + by;
        if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
            graphics.renderTooltip(font, List.of(Component.literal(line1), Component.literal(line2)), Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((button == 0 || button == 1) && minecraft != null && minecraft.gameMode != null) {
            if (button == 0) {
                int stickyX = leftPos + menu.getStickyX();
                int stickyY = topPos + menu.getStickyY();
                if (mouseX >= stickyX && mouseX < stickyX + 18 && mouseY >= stickyY && mouseY < stickyY + 18) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, AnalogCrafterMenu.BUTTON_STICKY);
                    return true;
                }

                int spreadX = leftPos + menu.getSpreadX();
                int spreadY = topPos + menu.getSpreadY();
                if (mouseX >= spreadX && mouseX < spreadX + 18 && mouseY >= spreadY && mouseY < spreadY + 18) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, AnalogCrafterMenu.BUTTON_SPREAD);
                    return true;
                }
            }

            int sbx = menu.getSideBtnX();
            int sby = menu.getSideBtnY();
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int slotIdx = row * 3 + col;
                    int bx = leftPos + sbx + col * SIDE_BTN_SIZE;
                    int by = topPos + sby + row * SIDE_BTN_SIZE;
                    if (mouseX >= bx && mouseX < bx + SIDE_BTN_SIZE && mouseY >= by && mouseY < by + SIDE_BTN_SIZE) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        int buttonId = button == 1
                                ? AnalogCrafterMenu.BUTTON_SLOT_SIDE_REVERSE_BASE + slotIdx
                                : AnalogCrafterMenu.BUTTON_SLOT_SIDE_BASE + slotIdx;
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
