package com.leclowndu93150.extrautils2.network.power;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.power.ClientGpData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GpSyncPacket(float created, float drained) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GpSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "gp_sync"));

    public static final StreamCodec<FriendlyByteBuf, GpSyncPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeFloat(pkt.created);
                buf.writeFloat(pkt.drained);
            },
            buf -> new GpSyncPacket(buf.readFloat(), buf.readFloat())
    );

    @Override
    public CustomPacketPayload.Type<GpSyncPacket> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player, float created, float drained) {
        PacketDistributor.sendToPlayer(player, new GpSyncPacket(created, drained));
    }

    public static void handle(GpSyncPacket packet, IPayloadContext ctx) {
        ClientGpData.gpCreated = packet.created;
        ClientGpData.gpDrained = packet.drained;
    }
}
