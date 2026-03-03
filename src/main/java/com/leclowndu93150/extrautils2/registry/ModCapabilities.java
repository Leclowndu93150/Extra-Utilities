package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.TrashCanBlock;
import com.leclowndu93150.extrautils2.blockentity.CreativeChestBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.CreativeEnergyBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.DrumBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.ResonatorBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.TrashCanBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.generator.MachineGeneratorTile;
import com.leclowndu93150.extrautils2.blockentity.machine.AnalogCrafterBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.machine.CrafterBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.machine.CrusherBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.machine.EnchanterBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.machine.FurnaceBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.transfer.FluidTransferNodeBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.transfer.ItemTransferNodeBlockEntity;
import com.leclowndu93150.extrautils2.util.MachineItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
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
                (ResonatorBlockEntity be, Direction side) -> new MachineItemHandler(
                        new IItemHandlerModifiable[]{be.getInput()},
                        new IItemHandlerModifiable[]{be.getOutput()}));

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CREATIVE_CHEST.get(),
                (CreativeChestBlockEntity be, Direction side) -> be.getHandler());

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.CREATIVE_ENERGY.get(),
                (CreativeEnergyBlockEntity be, Direction side) -> CreativeEnergyBlockEntity.INFINITE_ENERGY);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (TrashCanBlockEntity be, Direction side) -> be.getTrashType() == TrashCanBlock.TrashType.ITEM ? be.itemHandler : null);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (TrashCanBlockEntity be, Direction side) -> be.getTrashType() == TrashCanBlock.TrashType.FLUID ? be.fluidHandler : null);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.TRASH_CAN.get(),
                (TrashCanBlockEntity be, Direction side) -> be.getTrashType() == TrashCanBlock.TrashType.ENERGY ? be.energyHandler : null);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MACHINE_FURNACE.get(),
                (FurnaceBlockEntity be, Direction side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MACHINE_FURNACE.get(),
                (FurnaceBlockEntity be, Direction side) -> new MachineItemHandler(
                        new IItemHandlerModifiable[]{be.getInput()},
                        new IItemHandlerModifiable[]{be.getOutput()}));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MACHINE_CRUSHER.get(),
                (CrusherBlockEntity be, Direction side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MACHINE_CRUSHER.get(),
                (CrusherBlockEntity be, Direction side) -> new MachineItemHandler(
                        new IItemHandlerModifiable[]{be.getInput()},
                        new IItemHandlerModifiable[]{be.getOutput(), be.getSecondaryOutput()}));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MACHINE_ENCHANTER.get(),
                (EnchanterBlockEntity be, Direction side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MACHINE_ENCHANTER.get(),
                (EnchanterBlockEntity be, Direction side) -> new MachineItemHandler(
                        new IItemHandlerModifiable[]{be.getInput(), be.getCatalyst()},
                        new IItemHandlerModifiable[]{be.getOutput()}));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.CRAFTER.get(),
                (CrafterBlockEntity be, Direction side) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CRAFTER.get(),
                (CrafterBlockEntity be, Direction side) -> new MachineItemHandler(
                        new IItemHandlerModifiable[]{be.getInput()},
                        new IItemHandlerModifiable[]{be.getOutput()}));

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ANALOG_CRAFTER.get(),
                (AnalogCrafterBlockEntity be, Direction side) -> side != null ? be.getSidedHandler(side) : be.getSidedHandler(Direction.NORTH));

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ITEM_TRANSFER_NODE.get(),
                (ItemTransferNodeBlockEntity be, Direction side) -> be.getBuffer());

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.FLUID_TRANSFER_NODE.get(),
                (FluidTransferNodeBlockEntity be, Direction side) -> be.getBuffer());
    }
}
