package com.leclowndu93150.extrautils2.item;

import com.leclowndu93150.extrautils2.api.fluids.IFluidFilter;
import com.leclowndu93150.extrautils2.gui.filter.FluidFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.List;

public class ItemFilterFluids extends XUItem implements IFluidFilter {
    public static final int NUM_SLOTS = 16;
    private static final String TAG_FLAGS = "Flags";

    public ItemFilterFluids() {
        super(new Properties().stacksTo(1));
    }

    public enum Flag {
        INVERTED("filter.extrautils2.fluid.inverted.off", "filter.extrautils2.fluid.inverted.on"),
        IGNORE_NBT("filter.extrautils2.fluid.ignore_nbt.off", "filter.extrautils2.fluid.ignore_nbt.on");

        public final int bit = 1 << ordinal();
        public final String offKey;
        public final String onKey;

        Flag(String offKey, String onKey) {
            this.offKey = offKey;
            this.onKey = onKey;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.openMenu(new SimpleMenuProvider((id, inv, p) -> new FluidFilterMenu(id, inv, hand), stack.getHoverName()),
                    buf -> buf.writeEnum(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isFluidFilter(ItemStack filterStack) {
        return true;
    }

    @Override
    public boolean matches(ItemStack filterStack, FluidStack target) {
        boolean inverted = getFlag(filterStack, Flag.INVERTED);
        if (target.isEmpty()) return inverted;
        boolean ignoreNbt = getFlag(filterStack, Flag.IGNORE_NBT);

        for (int i = 0; i < NUM_SLOTS; i++) {
            ItemStack ghost = getGhostStack(filterStack, i);
            if (ghost.isEmpty()) continue;

            if (ghost.getItem() instanceof IFluidFilter nested && nested.matches(ghost, target)) {
                return !inverted;
            }

            FluidStack fluid = FluidUtil.getFluidContained(ghost).orElse(FluidStack.EMPTY);
            if (fluid.isEmpty()) continue;

            if (ignoreNbt ? FluidStack.isSameFluid(fluid, target) : FluidStack.isSameFluidSameComponents(fluid, target)) {
                return !inverted;
            }
        }

        return inverted;
    }

    public static boolean getFlag(ItemStack stack, Flag flag) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return (data.copyTag().getInt(TAG_FLAGS) & flag.bit) != 0;
    }

    public static void setFlag(ItemStack stack, Flag flag, boolean value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            int flags = tag.getInt(TAG_FLAGS);
            if (value) {
                flags |= flag.bit;
            } else {
                flags &= ~flag.bit;
            }

            if (flags == 0) {
                tag.remove(TAG_FLAGS);
            } else {
                tag.putInt(TAG_FLAGS, flags);
            }
        });
    }

    public static ItemStack getGhostStack(ItemStack stack, int slot) {
        NonNullList<ItemStack> items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
        stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        return items.get(slot);
    }

    public static void setGhostStack(ItemStack stack, int slot, ItemStack ghost) {
        NonNullList<ItemStack> items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
        stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        items.set(slot, ghost.isEmpty() ? ItemStack.EMPTY : ghost.copyWithCount(1));

        boolean any = items.stream().anyMatch(s -> !s.isEmpty());
        if (any) {
            stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        } else {
            stack.remove(DataComponents.CONTAINER);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        for (int i = 0; i < NUM_SLOTS; i++) {
            ItemStack ghost = getGhostStack(stack, i);
            if (ghost.isEmpty()) continue;

            FluidStack fluid = FluidUtil.getFluidContained(ghost).orElse(FluidStack.EMPTY);
            Component name = fluid.isEmpty() ? ghost.getHoverName() : fluid.getHoverName();
            tooltip.add(Component.literal((i + 1) + " - ").withStyle(ChatFormatting.GRAY)
                    .append(name.copy().withStyle(ChatFormatting.WHITE)));
        }

        for (Flag value : Flag.values()) {
            if (getFlag(stack, value)) {
                tooltip.add(Component.translatable(value.onKey).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}
