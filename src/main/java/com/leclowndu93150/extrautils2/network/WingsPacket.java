package com.leclowndu93150.extrautils2.network;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.AngelRingItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record WingsPacket(UUID playerUuid, int wingType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WingsPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "wings"));

    public static final StreamCodec<FriendlyByteBuf, WingsPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeUUID(pkt.playerUuid);
                buf.writeByte(pkt.wingType);
            },
            buf -> new WingsPacket(buf.readUUID(), buf.readByte())
    );

    @Override
    public CustomPacketPayload.Type<WingsPacket> type() {
        return TYPE;
    }

    public static void sendToAll(ServerPlayer source, int wingType) {
        WingsPacket pkt = new WingsPacket(source.getUUID(), wingType);
        PacketDistributor.sendToAllPlayers(pkt);
    }

    public static void handle(WingsPacket packet, IPayloadContext ctx) {
        if (packet.wingType > 0) {
            AngelRingItem.clientWings.put(packet.playerUuid, packet.wingType);
        } else {
            AngelRingItem.clientWings.remove(packet.playerUuid);
        }
    }
}
