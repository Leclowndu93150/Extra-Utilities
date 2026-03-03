package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.item.BuildersWandItem;
import com.leclowndu93150.extrautils2.item.DestructionWandItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WandRenderer {

    private static final float[] NORMAL_COLOR = {0.957f, 0.902f, 0.306f};
    private static final float[] CREATIVE_COLOR = {0.749f, 0.294f, 0.957f};

    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        boolean isBuilder = held.getItem() instanceof BuildersWandItem;
        boolean isDestruction = held.getItem() instanceof DestructionWandItem;
        if (!isBuilder && !isDestruction) return;

        BlockHitResult hitResult = event.getTarget();
        Level level = player.level();
        BlockPos origin = hitResult.getBlockPos();
        Direction face = hitResult.getDirection();

        if (level.getBlockState(origin).isAir()) return;

        List<BlockPos> positions;
        if (isBuilder) {
            positions = ((BuildersWandItem) held.getItem()).getPlacementPositions(level, origin, face, player);
        } else {
            positions = ((DestructionWandItem) held.getItem()).getDestructionPositions(level, origin, face, player);
        }

        if (positions.isEmpty()) return;

        event.setCanceled(true);

        boolean creative = (isBuilder && ((BuildersWandItem) held.getItem()).getRange() > 9)
                || (isDestruction && ((DestructionWandItem) held.getItem()).getRange() > 9);
        float[] color = creative ? CREATIVE_COLOR : NORMAL_COLOR;

        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();

        renderSelection(poseStack, bufferSource, positions, camPos, color);
    }

    private static void renderSelection(PoseStack poseStack, MultiBufferSource bufferSource,
                                         List<BlockPos> positions, Vec3 camPos, float[] color) {
        Set<Long> posSet = new HashSet<>();
        for (BlockPos p : positions) posSet.add(p.asLong());

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        for (BlockPos pos : positions) {
            double x = pos.getX() - camPos.x;
            double y = pos.getY() - camPos.y;
            double z = pos.getZ() - camPos.z;

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (posSet.contains(neighbor.asLong())) continue;

                drawFaceOutline(poseStack, consumer, x, y, z, dir, color);
            }
        }
    }

    private static void drawFaceOutline(PoseStack poseStack, VertexConsumer consumer,
                                         double x, double y, double z, Direction face, float[] color) {
        float r = color[0], g = color[1], b = color[2], a = 0.8f;
        PoseStack.Pose pose = poseStack.last();

        float x0 = (float) x, y0 = (float) y, z0 = (float) z;
        float x1 = x0 + 1, y1 = y0 + 1, z1 = z0 + 1;

        switch (face) {
            case DOWN -> {
                line(consumer, pose, x0, y0, z0, x1, y0, z0, r, g, b, a);
                line(consumer, pose, x1, y0, z0, x1, y0, z1, r, g, b, a);
                line(consumer, pose, x1, y0, z1, x0, y0, z1, r, g, b, a);
                line(consumer, pose, x0, y0, z1, x0, y0, z0, r, g, b, a);
            }
            case UP -> {
                line(consumer, pose, x0, y1, z0, x1, y1, z0, r, g, b, a);
                line(consumer, pose, x1, y1, z0, x1, y1, z1, r, g, b, a);
                line(consumer, pose, x1, y1, z1, x0, y1, z1, r, g, b, a);
                line(consumer, pose, x0, y1, z1, x0, y1, z0, r, g, b, a);
            }
            case NORTH -> {
                line(consumer, pose, x0, y0, z0, x1, y0, z0, r, g, b, a);
                line(consumer, pose, x1, y0, z0, x1, y1, z0, r, g, b, a);
                line(consumer, pose, x1, y1, z0, x0, y1, z0, r, g, b, a);
                line(consumer, pose, x0, y1, z0, x0, y0, z0, r, g, b, a);
            }
            case SOUTH -> {
                line(consumer, pose, x0, y0, z1, x1, y0, z1, r, g, b, a);
                line(consumer, pose, x1, y0, z1, x1, y1, z1, r, g, b, a);
                line(consumer, pose, x1, y1, z1, x0, y1, z1, r, g, b, a);
                line(consumer, pose, x0, y1, z1, x0, y0, z1, r, g, b, a);
            }
            case WEST -> {
                line(consumer, pose, x0, y0, z0, x0, y0, z1, r, g, b, a);
                line(consumer, pose, x0, y0, z1, x0, y1, z1, r, g, b, a);
                line(consumer, pose, x0, y1, z1, x0, y1, z0, r, g, b, a);
                line(consumer, pose, x0, y1, z0, x0, y0, z0, r, g, b, a);
            }
            case EAST -> {
                line(consumer, pose, x1, y0, z0, x1, y0, z1, r, g, b, a);
                line(consumer, pose, x1, y0, z1, x1, y1, z1, r, g, b, a);
                line(consumer, pose, x1, y1, z1, x1, y1, z0, r, g, b, a);
                line(consumer, pose, x1, y1, z0, x1, y0, z0, r, g, b, a);
            }
        }
    }

    private static void line(VertexConsumer consumer, PoseStack.Pose pose,
                              float x1, float y1, float z1, float x2, float y2, float z2,
                              float r, float g, float b, float a) {
        float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) return;
        dx /= len;
        dy /= len;
        dz /= len;
        consumer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
        consumer.addVertex(pose, x2, y2, z2).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
    }
}
