package com.leclowndu93150.extrautils2.block;

import com.leclowndu93150.extrautils2.blockentity.DrumBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DrumBlock extends Block implements EntityBlock {
    public enum Capacity {
        DRUM_16("stone", 16, 0.4f),
        DRUM_256("iron", 256, 0.4f),
        DRUM_4096("highcapacity", 4096, 0.4f),
        DRUM_65536("insane", 65536, 0.484375f),
        DRUM_CREATIVE("creative", 10000, 0.4f);

        public final String texture;
        public final String textureSide;
        public final String textureTop;
        public final int capacity;
        public final float width;

        Capacity(String texture, int capacity, float width) {
            this.texture = texture;
            this.textureSide = "drum_center_stripe_" + texture;
            this.textureTop = "drum_top_" + texture;
            this.capacity = capacity;
            this.width = width;
        }
    }

    private final Capacity capacity;
    private final VoxelShape shape;

    public DrumBlock(Capacity capacity, BlockBehaviour.Properties props) {
        super(props);
        this.capacity = capacity;
        this.shape = Shapes.box(0.5 - capacity.width, 0.0, 0.5 - capacity.width, 0.5 + capacity.width, 1.0, 0.5 + capacity.width);
    }

    public Capacity getCapacity() {
        return capacity;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return shape;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DrumBlockEntity(ModBlockEntities.DRUM.get(), pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DrumBlockEntity drum)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (capacity == Capacity.DRUM_CREATIVE && drum.handleCreativeSetFluid(player, stack)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (FluidUtil.interactWithFluidHandler(player, hand, drum.getTank())) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof DrumBlockEntity drum)) return InteractionResult.PASS;
        FluidStack fluid = drum.getTank().getFluid();
        int cap = capacity.capacity * 1000;
        if (fluid.isEmpty()) {
            player.displayClientMessage(Component.translatable("tooltip.extrautils2.drum.empty"), false);
        } else {
            int displayAmt = capacity == Capacity.DRUM_CREATIVE ? cap : fluid.getAmount();
            String amt = NumberFormat.getIntegerInstance(Locale.US).format(displayAmt);
            String max = NumberFormat.getIntegerInstance(Locale.US).format(cap);
            player.displayClientMessage(Component.translatable("tooltip.extrautils2.drum", fluid.getHoverName(), amt, max), false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack stack = new ItemStack(this.asItem());
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof DrumBlockEntity drum) {
            drum.saveToItem(stack, builder.getLevel().registryAccess());
        }
        return List.of(stack);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof DrumBlockEntity drum)) return 0;
        int amount = drum.getTank().getFluidAmount();
        int cap = capacity.capacity * 1000;
        return amount == 0 ? 0 : 1 + amount * 14 / cap;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        HolderLookup.Provider lookup = context.registries();
        FluidStack fluid = getFluidFromStack(stack, lookup);
        int cap = capacity.capacity * 1000;
        if (fluid.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.extrautils2.drum.empty").withStyle(ChatFormatting.GRAY));
        } else {
            String amt = NumberFormat.getIntegerInstance(Locale.US).format(fluid.getAmount());
            String max = NumberFormat.getIntegerInstance(Locale.US).format(cap);
            tooltip.add(Component.translatable("tooltip.extrautils2.drum", fluid.getHoverName(), amt, max).withStyle(ChatFormatting.GRAY));
        }
    }

    public static FluidStack getFluidFromStack(ItemStack stack, HolderLookup.Provider lookup) {
        CustomData data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (data.isEmpty()) return FluidStack.EMPTY;
        CompoundTag tag = data.getUnsafe();
        if (!tag.contains("Fluid", CompoundTag.TAG_COMPOUND)) return FluidStack.EMPTY;
        return FluidStack.parseOptional(lookup, tag.getCompound("Fluid"));
    }
}
