package com.leclowndu93150.extrautils2.network.power;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.api.power.IPassiveGp;
import com.leclowndu93150.extrautils2.block.generator.GeneratorType;
import com.leclowndu93150.extrautils2.blockentity.generator.GeneratorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GpBlockInfoRequestPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GpBlockInfoRequestPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "gp_block_info_request"));

    public static final StreamCodec<FriendlyByteBuf, GpBlockInfoRequestPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> buf.writeBlockPos(pkt.pos),
            buf -> new GpBlockInfoRequestPacket(buf.readBlockPos())
    );

    @Override
    public CustomPacketPayload.Type<GpBlockInfoRequestPacket> type() {
        return TYPE;
    }

    public static void handle(GpBlockInfoRequestPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            ServerLevel level = player.serverLevel();
            BlockEntity be = level.getBlockEntity(packet.pos());
            if (!(be instanceof IGpSource source)) return;

            float raw;
            float effective;

            if (be instanceof GeneratorTile tile) {
                GeneratorType type = tile.getGeneratorType();
                if (type != null) {
                    raw = type.computeGp(tile, level);
                    effective = type.applyEfficiencyLoss(raw);
                } else {
                    raw = Float.NaN;
                    effective = Float.NaN;
                }
            } else {
                float gp = source.getGp();
                raw = source instanceof IPassiveGp ? gp : -gp;
                effective = raw;
            }

            PacketDistributor.sendToPlayer(player, new GpBlockInfoPacket(raw, effective));
        });
    }
}
