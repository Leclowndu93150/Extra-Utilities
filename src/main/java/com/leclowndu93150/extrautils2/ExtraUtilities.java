package com.leclowndu93150.extrautils2;

import com.leclowndu93150.extrautils2.data.power.GpAttachments;
import com.leclowndu93150.extrautils2.network.power.GpNetworking;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.registry.ModAttachments;
import com.leclowndu93150.extrautils2.registry.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ExtraUtilities.MODID)
public class ExtraUtilities {
    public static final String MODID = "extrautils2";

    public ExtraUtilities(IEventBus modEventBus) {
        GpAttachments.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModRegistries.register(modEventBus);
        modEventBus.addListener(GpNetworking::register);
        NeoForge.EVENT_BUS.register(GpManager.INSTANCE);
    }
}
