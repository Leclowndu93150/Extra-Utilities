package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.TrashCanBlock;
import com.leclowndu93150.extrautils2.blockentity.DrumBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.ResonatorBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.TrashCanBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.generator.MachineGeneratorTile;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.DRUM.get(), (DrumBlockEntity be, net.minecraft.core.Direction side) -> be.getTank());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MACHINE_GENERATOR.get(), (MachineGeneratorTile be, net.minecraft.core.Direction side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MACHINE_GENERATOR.get(), (MachineGeneratorTile be, net.minecraft.core.Direction side) -> be.getFluidTank());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MACHINE_GENERATOR.get(), (MachineGeneratorTile be, net.minecraft.core.Direction side) -> be.getInventory());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.RESONATOR.get(),
                (ResonatorBlockEntity be, Direction side) -> new CombinedInvWrapper(be.getInput(), be.getOutput()));

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (TrashCanBlockEntity be, Direction side) -> be.getTrashType() == TrashCanBlock.TrashType.ITEM ? be.itemHandler : null);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (TrashCanBlockEntity be, Direction side) -> be.getTrashType() == TrashCanBlock.TrashType.FLUID ? be.fluidHandler : null);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (TrashCanBlockEntity be, Direction side) -> be.getTrashType() == TrashCanBlock.TrashType.ENERGY ? be.energyHandler : null);
    }
}
