package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.gui.CreativeChestScreen;
import com.leclowndu93150.extrautils2.client.gui.MachineGeneratorScreen;
import com.leclowndu93150.extrautils2.client.gui.ResonatorScreen;
import com.leclowndu93150.extrautils2.client.gui.TrashCanScreen;
import com.leclowndu93150.extrautils2.client.gui.filter.FluidFilterScreen;
import com.leclowndu93150.extrautils2.client.gui.filter.ItemFilterScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.AnalogCrafterScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.CrafterScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.CrusherScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.EnchanterScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.FurnaceScreen;
import com.leclowndu93150.extrautils2.client.gui.transfer.FluidTransferNodeScreen;
import com.leclowndu93150.extrautils2.client.gui.transfer.TransferNodeScreen;
import com.leclowndu93150.extrautils2.client.ctm.CTMModelLoader;
import com.leclowndu93150.extrautils2.client.sprite.ModSpriteSourceTypes;
import com.leclowndu93150.extrautils2.block.DrumBlock;
import com.leclowndu93150.extrautils2.blockentity.DrumBlockEntity;
import com.leclowndu93150.extrautils2.item.GoldenLassoItem;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModItems;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ResourceLocation filledProp = ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "filled");
            ItemProperties.register(ModItems.GOLDEN_LASSO.get(), filledProp,
                    (stack, level, entity, seed) -> {
                        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                        return data.contains(GoldenLassoItem.TAG_ENTITY) ? 1.0F : 0.0F;
                    });
            ItemProperties.register(ModItems.CURSED_LASSO.get(), filledProp,
                    (stack, level, entity, seed) -> {
                        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                        return data.contains(GoldenLassoItem.TAG_ENTITY) ? 1.0F : 0.0F;
                    });
        });
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MACHINE_GENERATOR.get(), MachineGeneratorScreen::new);
        event.register(ModMenus.TRASH_CAN.get(), TrashCanScreen::new);
        event.register(ModMenus.RESONATOR.get(), ResonatorScreen::new);
        event.register(ModMenus.CREATIVE_CHEST.get(), CreativeChestScreen::new);
        event.register(ModMenus.MACHINE_FURNACE.get(), FurnaceScreen::new);
        event.register(ModMenus.MACHINE_CRUSHER.get(), CrusherScreen::new);
        event.register(ModMenus.MACHINE_ENCHANTER.get(), EnchanterScreen::new);
        event.register(ModMenus.CRAFTER.get(), CrafterScreen::new);
        event.register(ModMenus.ANALOG_CRAFTER.get(), AnalogCrafterScreen::new);
        event.register(ModMenus.TRANSFER_NODE.get(), TransferNodeScreen::new);
        event.register(ModMenus.FLUID_TRANSFER_NODE.get(), FluidTransferNodeScreen::new);
        event.register(ModMenus.ITEM_FILTER.get(), ItemFilterScreen::new);
        event.register(ModMenus.FLUID_FILTER.get(), FluidFilterScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterSpriteSourceTypes(RegisterSpriteSourceTypesEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "compressed"), ModSpriteSourceTypes.COMPRESSED);
        event.register(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "ctm"), ModSpriteSourceTypes.CTM);
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "ctm_glass"), CTMModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.HAND_CRANK.get(), HandCrankRenderer::new);
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

    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        Item[] opiniumCores = new Item[9];
        for (int i = 0; i <= 8; i++) {
            opiniumCores[i] = ModItems.getOpiniumCore(i).get();
        }
        event.registerItem(new IClientItemExtensions() {
            private OpiniumCoreRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new OpiniumCoreRenderer();
                }
                return renderer;
            }
        }, opiniumCores);
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
