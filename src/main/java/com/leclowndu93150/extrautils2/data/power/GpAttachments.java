package com.leclowndu93150.extrautils2.data.power;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class GpAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ExtraUtilities.MODID);

    public static final Supplier<AttachmentType<GpData>> GP_DATA = ATTACHMENT_TYPES.register(
            "gp_data",
            () -> AttachmentType.builder(() -> new GpData(0))
                    .serialize(GpData.CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
