package com.leclowndu93150.extrautils2.item;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.client.power.ClientGpData;
import com.leclowndu93150.extrautils2.data.power.GpAttachments;
import com.leclowndu93150.extrautils2.power.GpManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChickenRingItem extends XUItem implements ICurioItem {

    private static final int MAX_FLIGHT_TICKS = 100;
    private static final Map<UUID, ChickenRingSource> activeSources = new HashMap<>();
    private static final Map<UUID, Integer> flightCharge = new HashMap<>();

    public ChickenRingItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Flight of the majestic beast!").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        tooltip.add(Component.literal("Uses 1 GP").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal(String.format("GP: %.0f / %.0f",
                ClientGpData.gpDrained, ClientGpData.gpCreated)).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();
        int charge = flightCharge.getOrDefault(uuid, MAX_FLIGHT_TICKS);

        if (player.onGround()) {
            charge = Math.min(MAX_FLIGHT_TICKS, charge + 4);
        } else if (!player.getAbilities().flying && player.getDeltaMovement().y < 0 && player.jumping && charge > 0) {
            ChickenRingSource source = activeSources.get(uuid);
            if (source != null && source.powered) {
                var motion = player.getDeltaMovement();
                double newY = Math.min(motion.y + 0.1, motion.y * 0.5);
                player.setDeltaMovement(motion.x, newY, motion.z);
                player.fallDistance = 0;
                player.hurtMarked = true;
                charge--;
            }
        } else {
            charge = Math.min(MAX_FLIGHT_TICKS, charge + 4);
        }

        flightCharge.put(uuid, charge);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer sp)) return;

        ChickenRingSource existing = activeSources.remove(sp.getUUID());
        if (existing != null) GpManager.INSTANCE.removeSource(existing);

        ChickenRingSource source = new ChickenRingSource(sp);
        activeSources.put(sp.getUUID(), source);
        GpManager.INSTANCE.addSource(source);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer sp)) return;

        ChickenRingSource source = activeSources.remove(sp.getUUID());
        if (source != null) GpManager.INSTANCE.removeSource(source);
        flightCharge.remove(sp.getUUID());
    }

    public static class ChickenRingSource implements IGpSource {
        private final ServerPlayer player;
        boolean powered = true;

        ChickenRingSource(ServerPlayer player) {
            this.player = player;
        }

        @Override
        public float getGp() {
            return 1f;
        }

        @Override
        public int frequency() {
            return player.getData(GpAttachments.GP_DATA.get()).frequency;
        }

        @Override
        public void onPowerChanged(boolean powered) {
            this.powered = powered;
        }

        @Override
        public boolean isLoaded() {
            return !player.isRemoved() && player.isAlive();
        }

        @Override
        public @Nullable Level level() {
            return player.level();
        }

        @Override
        public @Nullable BlockPos getPos() {
            return player.blockPosition();
        }
    }
}
