package com.leclowndu93150.extrautils2.blockentity;

import com.leclowndu93150.extrautils2.network.MufflerHeartbeatPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class SoundMufflerBlockEntity extends XUBlockEntity {

    private static final int HEARTBEAT_INTERVAL = 100;

    public SoundMufflerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoundMufflerBlockEntity tile) {
        if (level.isClientSide) return;
        if (level.getGameTime() % HEARTBEAT_INTERVAL != 0) return;
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(pos),
                new MufflerHeartbeatPacket(pos));
    }
}
