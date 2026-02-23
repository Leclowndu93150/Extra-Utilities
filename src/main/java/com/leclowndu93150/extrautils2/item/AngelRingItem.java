package com.leclowndu93150.extrautils2.item;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.client.power.ClientGpData;
import com.leclowndu93150.extrautils2.data.power.GpAttachments;
import com.leclowndu93150.extrautils2.network.WingsPacket;
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

public class AngelRingItem extends XUItem implements ICurioItem {

    public static final String[] WING_TYPES = {
        "base", "feather", "butterfly", "demon", "golden", "bat"
    };

    public static final Map<UUID, Integer> clientWings = new HashMap<>();
    private static final Map<UUID, AngelRingSource> activeSources = new HashMap<>();

    private final int type;

    public AngelRingItem(int type) {
        super(new Properties().stacksTo(1));
        this.type = type;
    }

    public int getWingType() {
        return type + 1;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Uses 32 GP").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal(String.format("GP: %.0f / %.0f",
                ClientGpData.gpDrained, ClientGpData.gpCreated)).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer sp)) return;
        if (!sp.isSpectator()) {
            sp.getAbilities().mayfly = true;
            sp.onUpdateAbilities();
        }
        WingsPacket.sendToAll(sp, getWingType());

        AngelRingSource existing = activeSources.remove(sp.getUUID());
        if (existing != null) GpManager.INSTANCE.removeSource(existing);

        AngelRingSource source = new AngelRingSource(sp);
        activeSources.put(sp.getUUID(), source);
        GpManager.INSTANCE.addSource(source);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer sp)) return;
        if (!sp.isSpectator() && !sp.isCreative()) {
            sp.getAbilities().mayfly = false;
            sp.getAbilities().flying = false;
            sp.onUpdateAbilities();
        }
        WingsPacket.sendToAll(sp, 0);

        AngelRingSource source = activeSources.remove(sp.getUUID());
        if (source != null) GpManager.INSTANCE.removeSource(source);
    }

    public static class AngelRingSource implements IGpSource {
        private final ServerPlayer player;

        AngelRingSource(ServerPlayer player) {
            this.player = player;
        }

        @Override
        public float getGp() {
            return player.getAbilities().flying ? 32f : 1f;
        }

        @Override
        public int frequency() {
            return player.getData(GpAttachments.GP_DATA.get()).frequency;
        }

        @Override
        public void onPowerChanged(boolean powered) {
            if (player.isRemoved()) return;
            boolean canFly = powered || player.isCreative() || player.isSpectator();
            if (player.getAbilities().mayfly != canFly) {
                player.getAbilities().mayfly = canFly;
                if (!canFly) player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
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
