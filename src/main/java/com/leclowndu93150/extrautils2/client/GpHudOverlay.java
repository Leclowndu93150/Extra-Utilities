package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.block.generator.GeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.GeneratorType;
import com.leclowndu93150.extrautils2.client.power.ClientGpData;
import com.leclowndu93150.extrautils2.network.power.GpBlockInfoRequestPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class GpHudOverlay {

    private static boolean lookingAtGpBlock = false;
    private static BlockPos lookedAtPos = null;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            lookingAtGpBlock = false;
            lookedAtPos = null;
            ClientGpData.blockRawGp = Float.NaN;
            ClientGpData.blockEffectiveGp = Float.NaN;
            return;
        }
        HitResult hit = mc.hitResult;
        if (hit instanceof BlockHitResult blockHit && mc.level.getBlockEntity(blockHit.getBlockPos()) instanceof IGpSource) {
            lookingAtGpBlock = true;
            BlockPos pos = blockHit.getBlockPos();
            if (!pos.equals(lookedAtPos)) {
                ClientGpData.blockRawGp = Float.NaN;
                ClientGpData.blockEffectiveGp = Float.NaN;
                lookedAtPos = pos;
            }
            PacketDistributor.sendToServer(new GpBlockInfoRequestPacket(pos));
        } else {
            lookingAtGpBlock = false;
            lookedAtPos = null;
            ClientGpData.blockRawGp = Float.NaN;
            ClientGpData.blockEffectiveGp = Float.NaN;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!lookingAtGpBlock) return;
        if (ClientGpData.hasNoPower()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        GuiGraphics graphics = event.getGuiGraphics();
        Font font = mc.font;

        String label = String.format("GP: %.1f / %.1f", ClientGpData.gpDrained, ClientGpData.gpCreated);
        int x = (graphics.guiWidth() - font.width(label)) / 2;
        int y = graphics.guiHeight() - 68 - (int)(graphics.guiHeight() * 0.05f);
        graphics.drawString(font, label, x, y, 0xFFFFFF);

        float raw = ClientGpData.blockRawGp;
        float eff = ClientGpData.blockEffectiveGp;
        if (!Float.isNaN(raw) && raw != 0f) {
            y += font.lineHeight + 1;
            String genLine = raw < 0f
                    ? String.format("Power Drain: %.2f GP", -raw)
                    : String.format("Power Generating: %.2f GP", raw);
            graphics.drawString(font, genLine, (graphics.guiWidth() - font.width(genLine)) / 2, y, 0xFFFFFF);

            if (!Float.isNaN(eff) && Math.abs(eff - raw) > 0.001f) {
                y += font.lineHeight + 1;
                float lossPct = (1f - eff / raw) * 100f;
                String effLine = String.format("Effective Rate: %.2f GP (%.0f%% Power Loss)", eff, lossPct);
                graphics.drawString(font, effLine, (graphics.guiWidth() - font.width(effLine)) / 2, y, 0xFFFFFF);
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Block block = Block.byItem(event.getItemStack().getItem());
        if (!(block instanceof GeneratorBlock gen)) return;
        addGeneratorTooltip(event.getToolTip(), gen.generatorType);
    }

    private static void addGeneratorTooltip(List<Component> tooltip, GeneratorType type) {
        float base = type.baseGp;

        switch (type) {
            case SOLAR -> {
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.solar.1").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.solar.2").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.solar.3").withStyle(ChatFormatting.GRAY));
            }
            case LUNAR -> {
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.lunar.1").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.lunar.2").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.lunar.3").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.lunar.4").withStyle(ChatFormatting.GRAY));
            }
            case LAVA ->
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.lava").withStyle(ChatFormatting.GRAY));
            case WATER -> {
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.water.1").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.water.2").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.water.3").withStyle(ChatFormatting.GRAY));
            }
            case WIND -> {
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.wind.1").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.wind.2").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.wind.3").withStyle(ChatFormatting.GRAY));
            }
            case FIRE ->
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.fire").withStyle(ChatFormatting.GRAY));
            case PLAYER_WIND_UP ->
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.manual").withStyle(ChatFormatting.GRAY));
            case DRAGON_EGG ->
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.dragon_egg").withStyle(ChatFormatting.GRAY));
            case CREATIVE ->
                tooltip.add(Component.translatable("tooltip.extrautils2.generator.creative").withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Base Power: " + ChatFormatting.WHITE + base + " GP"));

        if (type.hasEfficiencyLoss()) {
            boolean shiftHeld = Screen.hasShiftDown();
            if (!shiftHeld) {
                tooltip.add(Component.literal(ChatFormatting.DARK_GRAY + "Progressive Efficiency Loss:"));
                tooltip.add(Component.literal(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + "   <Press Shift for details>"));
            } else {
                tooltip.add(Component.literal(ChatFormatting.GRAY + "Progressive Efficiency Loss:"));
                for (var entry : type.getCaps().entrySet()) {
                    float threshold = entry.getKey();
                    float loss = (1f - entry.getValue()) * 100f;
                    Float next = type.getCaps().higherKey(threshold);
                    String range = next == null
                            ? String.format("Above %.0f GP", threshold)
                            : String.format("%.0f to %.0f GP", threshold, next);
                    tooltip.add(Component.literal(ChatFormatting.GRAY + "   " + range + ": " + String.format("%.0f%%", loss) + " loss"));
                }
            }
        }
    }
}
