package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.client.gui.MachineGeneratorScreen;
import com.leclowndu93150.extrautils2.client.sprite.ModSpriteSourceTypes;
import com.leclowndu93150.extrautils2.block.DrumBlock;
import com.leclowndu93150.extrautils2.blockentity.DrumBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModItems;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MACHINE_GENERATOR.get(), MachineGeneratorScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterSpriteSourceTypes(RegisterSpriteSourceTypesEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "compressed"), ModSpriteSourceTypes.COMPRESSED);
    }

    @SubscribeEvent
    public static void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        CuriosRendererRegistry.register(ModItems.ANGEL_RING_BASE.get(), WingsRenderer::new);
        CuriosRendererRegistry.register(ModItems.ANGEL_RING_FEATHER.get(), WingsRenderer::new);
        CuriosRendererRegistry.register(ModItems.ANGEL_RING_BUTTERFLY.get(), WingsRenderer::new);
        CuriosRendererRegistry.register(ModItems.ANGEL_RING_DEMON.get(), WingsRenderer::new);
        CuriosRendererRegistry.register(ModItems.ANGEL_RING_GOLDEN.get(), WingsRenderer::new);
        CuriosRendererRegistry.register(ModItems.ANGEL_RING_BAT.get(), WingsRenderer::new);
        CuriosRendererRegistry.load();
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

        Block[] machineGenerators = {
            ModBlocks.MACHINE_GENERATOR_FURNACE.get(), ModBlocks.MACHINE_GENERATOR_SURVIVALIST.get(),
            ModBlocks.MACHINE_GENERATOR_CULINARY.get(), ModBlocks.MACHINE_GENERATOR_POTION.get(),
            ModBlocks.MACHINE_GENERATOR_TNT.get(), ModBlocks.MACHINE_GENERATOR_LAVA.get(),
            ModBlocks.MACHINE_GENERATOR_PINK.get(), ModBlocks.MACHINE_GENERATOR_NETHERSTAR.get(),
            ModBlocks.MACHINE_GENERATOR_ENDER.get(), ModBlocks.MACHINE_GENERATOR_REDSTONE.get(),
            ModBlocks.MACHINE_GENERATOR_OVERCLOCK.get(), ModBlocks.MACHINE_GENERATOR_DRAGON.get(),
            ModBlocks.MACHINE_GENERATOR_ICE.get(), ModBlocks.MACHINE_GENERATOR_DEATH.get(),
            ModBlocks.MACHINE_GENERATOR_ENCHANT.get(), ModBlocks.MACHINE_GENERATOR_SLIME.get()
        };
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0 && tintIndex != 1) return -1;
            if (!(state.getBlock() instanceof MachineGeneratorBlock mgb)) return -1;
            return mgb.generatorType.color | 0xFF000000;
        }, machineGenerators);
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

        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) return -1;
            if (!(stack.getItem() instanceof net.minecraft.world.item.BlockItem bi)) return -1;
            if (!(bi.getBlock() instanceof MachineGeneratorBlock mgb)) return -1;
            return mgb.generatorType.color | 0xFF000000;
        }, ModBlocks.MACHINE_GENERATOR_FURNACE.get(), ModBlocks.MACHINE_GENERATOR_SURVIVALIST.get(),
            ModBlocks.MACHINE_GENERATOR_CULINARY.get(), ModBlocks.MACHINE_GENERATOR_POTION.get(),
            ModBlocks.MACHINE_GENERATOR_TNT.get(), ModBlocks.MACHINE_GENERATOR_LAVA.get(),
            ModBlocks.MACHINE_GENERATOR_PINK.get(), ModBlocks.MACHINE_GENERATOR_NETHERSTAR.get(),
            ModBlocks.MACHINE_GENERATOR_ENDER.get(), ModBlocks.MACHINE_GENERATOR_REDSTONE.get(),
            ModBlocks.MACHINE_GENERATOR_OVERCLOCK.get(), ModBlocks.MACHINE_GENERATOR_DRAGON.get(),
            ModBlocks.MACHINE_GENERATOR_ICE.get(), ModBlocks.MACHINE_GENERATOR_DEATH.get(),
            ModBlocks.MACHINE_GENERATOR_ENCHANT.get(), ModBlocks.MACHINE_GENERATOR_SLIME.get());
    }
}
