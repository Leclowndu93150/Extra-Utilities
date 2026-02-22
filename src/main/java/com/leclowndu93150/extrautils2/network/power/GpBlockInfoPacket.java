package com.leclowndu93150.extrautils2.network.power;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.client.power.ClientGpData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GpBlockInfoPacket(float rawGp, float effectiveGp) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GpBlockInfoPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "gp_block_info"));

    public static final StreamCodec<FriendlyByteBuf, GpBlockInfoPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeFloat(pkt.rawGp);
                buf.writeFloat(pkt.effectiveGp);
            },
            buf -> new GpBlockInfoPacket(buf.readFloat(), buf.readFloat())
    );

    @Override
    public CustomPacketPayload.Type<GpBlockInfoPacket> type() {
        return TYPE;
    }

    public static void handle(GpBlockInfoPacket packet, IPayloadContext ctx) {
        ClientGpData.blockRawGp = packet.rawGp;
        ClientGpData.blockEffectiveGp = packet.effectiveGp;
    }
}
