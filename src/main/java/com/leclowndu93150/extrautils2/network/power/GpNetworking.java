package com.leclowndu93150.extrautils2.network.power;

import com.leclowndu93150.extrautils2.network.MufflerHeartbeatPacket;
import com.leclowndu93150.extrautils2.network.WingsPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class GpNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(GpSyncPacket.TYPE, GpSyncPacket.STREAM_CODEC, GpSyncPacket::handle);
        registrar.playToClient(GpBlockInfoPacket.TYPE, GpBlockInfoPacket.STREAM_CODEC, GpBlockInfoPacket::handle);
        registrar.playToServer(GpBlockInfoRequestPacket.TYPE, GpBlockInfoRequestPacket.STREAM_CODEC, GpBlockInfoRequestPacket::handle);
        registrar.playToClient(WingsPacket.TYPE, WingsPacket.STREAM_CODEC, WingsPacket::handle);
        registrar.playToClient(MufflerHeartbeatPacket.TYPE, MufflerHeartbeatPacket.STREAM_CODEC, MufflerHeartbeatPacket::handle);
    }
}
