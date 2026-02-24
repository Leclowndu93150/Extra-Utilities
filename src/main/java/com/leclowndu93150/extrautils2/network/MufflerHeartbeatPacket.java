package com.leclowndu93150.extrautils2.network;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.event.SoundMufflerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MufflerHeartbeatPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MufflerHeartbeatPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "muffler_heartbeat"));

    public static final StreamCodec<FriendlyByteBuf, MufflerHeartbeatPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> buf.writeBlockPos(pkt.pos),
            buf -> new MufflerHeartbeatPacket(buf.readBlockPos())
    );

    @Override
    public CustomPacketPayload.Type<MufflerHeartbeatPacket> type() {
        return TYPE;
    }

    public static void handle(MufflerHeartbeatPacket packet, IPayloadContext ctx) {
        SoundMufflerEvents.heartbeat(packet.pos);
    }
}
