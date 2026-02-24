package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ExtraUtilities.MODID, value = Dist.CLIENT)
public class ConstantRightClickHandler {
    private static BlockPos targetPos;
    private static BlockState targetState;
    private static int cooldown;

    public static void setPlayerRightClicking(BlockPos pos, BlockState state) {
        targetPos = pos;
        targetState = state;
        cooldown = 0;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (targetPos == null || targetState == null) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.level == null || player == null) {
            clear();
            return;
        }

        if (player.isShiftKeyDown()
                || mc.options.keyAttack.isDown()
                || mc.hitResult == null
                || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            clear();
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
        if (!targetPos.equals(blockHit.getBlockPos()) || mc.level.getBlockState(targetPos) != targetState) {
            clear();
            return;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        cooldown = 4;

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = player.getItemInHand(hand);
            int sizeBefore = held.isEmpty() ? 0 : held.getCount();
            InteractionResult result = mc.gameMode.useItemOn(player, hand, blockHit);
            if (result.consumesAction()) {
                if (result.shouldSwing()) {
                    player.swing(hand);
                }
                if (!held.isEmpty() && (held.isEmpty() || held.getCount() != sizeBefore)) {
                    mc.gameRenderer.itemInHandRenderer.itemUsed(hand);
                }
                return;
            }
        }
    }

    private static void clear() {
        targetPos = null;
        targetState = null;
    }
}
