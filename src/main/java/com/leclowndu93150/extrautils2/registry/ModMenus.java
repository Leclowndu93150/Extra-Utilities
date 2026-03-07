package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.gui.CreativeChestMenu;
import com.leclowndu93150.extrautils2.gui.LargishChestMenu;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import com.leclowndu93150.extrautils2.gui.MiniChestMenu;
import com.leclowndu93150.extrautils2.gui.ResonatorMenu;
import com.leclowndu93150.extrautils2.gui.TrashCanMenu;
import com.leclowndu93150.extrautils2.gui.filter.FluidFilterMenu;
import com.leclowndu93150.extrautils2.gui.filter.ItemFilterMenu;
import com.leclowndu93150.extrautils2.gui.machine.AnalogCrafterMenu;
import com.leclowndu93150.extrautils2.gui.machine.CrafterMenu;
import com.leclowndu93150.extrautils2.gui.machine.CrusherMenu;
import com.leclowndu93150.extrautils2.gui.machine.EnchanterMenu;
import com.leclowndu93150.extrautils2.gui.machine.FurnaceMenu;
import com.leclowndu93150.extrautils2.gui.transfer.FluidTransferNodeMenu;
import com.leclowndu93150.extrautils2.gui.transfer.TransferNodeMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ExtraUtilities.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<MachineGeneratorMenu>> MACHINE_GENERATOR =
            MENUS.register("machine_generator", () -> IMenuTypeExtension.create(MachineGeneratorMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<TrashCanMenu>> TRASH_CAN =
            MENUS.register("trash_can", () -> IMenuTypeExtension.create(TrashCanMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<ResonatorMenu>> RESONATOR =
            MENUS.register("resonator", () -> IMenuTypeExtension.create(ResonatorMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<CreativeChestMenu>> CREATIVE_CHEST =
            MENUS.register("creative_chest", () -> IMenuTypeExtension.create(CreativeChestMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<LargishChestMenu>> LARGISH_CHEST =
            MENUS.register("largish_chest", () -> IMenuTypeExtension.create(LargishChestMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<MiniChestMenu>> MINI_CHEST =
            MENUS.register("mini_chest", () -> IMenuTypeExtension.create(MiniChestMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<FurnaceMenu>> MACHINE_FURNACE =
            MENUS.register("machine_furnace", () -> IMenuTypeExtension.create(FurnaceMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<CrusherMenu>> MACHINE_CRUSHER =
            MENUS.register("machine_crusher", () -> IMenuTypeExtension.create(CrusherMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<EnchanterMenu>> MACHINE_ENCHANTER =
            MENUS.register("machine_enchanter", () -> IMenuTypeExtension.create(EnchanterMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<CrafterMenu>> CRAFTER =
            MENUS.register("crafter", () -> IMenuTypeExtension.create(CrafterMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<AnalogCrafterMenu>> ANALOG_CRAFTER =
            MENUS.register("analog_crafter", () -> IMenuTypeExtension.create(AnalogCrafterMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<TransferNodeMenu>> TRANSFER_NODE =
            MENUS.register("transfer_node", () -> IMenuTypeExtension.create(TransferNodeMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<FluidTransferNodeMenu>> FLUID_TRANSFER_NODE =
            MENUS.register("fluid_transfer_node", () -> IMenuTypeExtension.create(FluidTransferNodeMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<ItemFilterMenu>> ITEM_FILTER =
            MENUS.register("item_filter", () -> IMenuTypeExtension.create(ItemFilterMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<FluidFilterMenu>> FLUID_FILTER =
            MENUS.register("fluid_filter", () -> IMenuTypeExtension.create(FluidFilterMenu::fromNetwork));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
