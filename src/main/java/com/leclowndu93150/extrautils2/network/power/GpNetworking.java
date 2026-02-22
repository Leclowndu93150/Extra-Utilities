package com.leclowndu93150.extrautils2.network.power;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class GpNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GpSyncPacket.TYPE.id().getPath());
        registrar.playToClient(GpSyncPacket.TYPE, GpSyncPacket.STREAM_CODEC, GpSyncPacket::handle);
    }
}
