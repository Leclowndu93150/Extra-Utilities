package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.sprite.ModSpriteSourceTypes;
import com.leclowndu93150.extrautils2.block.DrumBlock;
import com.leclowndu93150.extrautils2.blockentity.DrumBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterSpriteSourceTypes(RegisterSpriteSourceTypesEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "compressed"), ModSpriteSourceTypes.COMPRESSED);
    }

    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 1 || level == null || pos == null) return -1;
            if (!(level.getBlockEntity(pos) instanceof DrumBlockEntity drum)) return -1;
            FluidStack fluid = drum.getTank().getFluid();
            if (fluid.isEmpty()) return -1;
            return IClientFluidTypeExtensions.of(fluid.getFluidType()).getTintColor(fluid);
        }, ModBlocks.DRUM_16.get(), ModBlocks.DRUM_256.get(), ModBlocks.DRUM_4096.get(), ModBlocks.DRUM_65536.get(), ModBlocks.DRUM_CREATIVE.get());
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) return -1;
            HolderLookup.Provider lookup;
            if (Minecraft.getInstance().level != null) {
                lookup = Minecraft.getInstance().level.registryAccess();
            } else if (Minecraft.getInstance().getConnection() != null) {
                lookup = Minecraft.getInstance().getConnection().registryAccess();
            } else {
                return -1;
            }
            FluidStack fluid = DrumBlock.getFluidFromStack(stack, lookup);
            if (fluid.isEmpty()) return -1;
            return IClientFluidTypeExtensions.of(fluid.getFluidType()).getTintColor(fluid);
        }, ModBlocks.DRUM_16.get(), ModBlocks.DRUM_256.get(), ModBlocks.DRUM_4096.get(), ModBlocks.DRUM_65536.get(), ModBlocks.DRUM_CREATIVE.get());
    }
}
