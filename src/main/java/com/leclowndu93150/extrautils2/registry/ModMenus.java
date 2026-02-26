package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.gui.CreativeChestMenu;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import com.leclowndu93150.extrautils2.gui.ResonatorMenu;
import com.leclowndu93150.extrautils2.gui.TrashCanMenu;
import com.leclowndu93150.extrautils2.gui.machine.CrusherMenu;
import com.leclowndu93150.extrautils2.gui.machine.EnchanterMenu;
import com.leclowndu93150.extrautils2.gui.machine.FurnaceMenu;
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

    public static final DeferredHolder<MenuType<?>, MenuType<FurnaceMenu>> MACHINE_FURNACE =
            MENUS.register("machine_furnace", () -> IMenuTypeExtension.create(FurnaceMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<CrusherMenu>> MACHINE_CRUSHER =
            MENUS.register("machine_crusher", () -> IMenuTypeExtension.create(CrusherMenu::fromNetwork));

    public static final DeferredHolder<MenuType<?>, MenuType<EnchanterMenu>> MACHINE_ENCHANTER =
            MENUS.register("machine_enchanter", () -> IMenuTypeExtension.create(EnchanterMenu::fromNetwork));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
