package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.blockentity.generator.HandCrankTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class HandCrankRenderer implements BlockEntityRenderer<HandCrankTile> {
    private static final int FACE_DOWN  = 1;
    private static final int FACE_UP    = 2;
    private static final int FACE_NORTH = 4;
    private static final int FACE_SOUTH = 8;
    private static final int FACE_WEST  = 16;
    private static final int FACE_EAST  = 32;
    private static final int ALL_FACES  = 63;

    private static final Material GEAR_TEXTURE = new Material(
            InventoryMenu.BLOCK_ATLAS,
            ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/redstone_gear")
    );

    public HandCrankRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(HandCrankTile tile, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        float crankTime = tile.getCrankTime();
        float renderOffset = tile.getRenderOffset();

        double angle = renderOffset
                + partialTick * Math.max(Math.min(crankTime, 0.5f) - 0.05f * partialTick, 0.0) * (Math.PI / 10.0);

        TextureAtlasSprite sprite = GEAR_TEXTURE.sprite();
        VertexConsumer vc = buffer.getBuffer(RenderType.cutout());

        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Axis.YP.rotation((float) angle));
        pose.translate(-0.5, -0.5, -0.5);

        renderBox(pose, vc, sprite, 6, 6, 6, 10, 10, 10, packedLight, packedOverlay, ALL_FACES);
        renderBox(pose, vc, sprite, 2, 7, 7, 14, 9, 9, packedLight, packedOverlay, ALL_FACES);
        renderBox(pose, vc, sprite, 7, 7, 2, 9, 9, 14, packedLight, packedOverlay, ALL_FACES);

        renderCrossPlanes(pose, vc, sprite, packedLight, packedOverlay);

        pose.popPose();
    }

    private void renderBox(PoseStack pose, VertexConsumer vc, TextureAtlasSprite sprite,
                           int x0, int y0, int z0, int x1, int y1, int z1,
                           int light, int overlay, int faces) {
        float fx0 = x0 / 16f, fy0 = y0 / 16f, fz0 = z0 / 16f;
        float fx1 = x1 / 16f, fy1 = y1 / 16f, fz1 = z1 / 16f;

        PoseStack.Pose entry = pose.last();

        float su0 = sprite.getU(x0 / 16f);
        float su1 = sprite.getU(x1 / 16f);
        float sv0y = sprite.getV(1f - y1 / 16f);
        float sv1y = sprite.getV(1f - y0 / 16f);
        float sv0z = sprite.getV(z0 / 16f);
        float sv1z = sprite.getV(z1 / 16f);
        float su0z = sprite.getU(z0 / 16f);
        float su1z = sprite.getU(z1 / 16f);

        if ((faces & FACE_DOWN) != 0)
            quad(entry, vc, light, overlay,
                    fx0, fy0, fz1, su0, sv1z,
                    fx0, fy0, fz0, su0, sv0z,
                    fx1, fy0, fz0, su1, sv0z,
                    fx1, fy0, fz1, su1, sv1z,
                    0, -1, 0);

        if ((faces & FACE_UP) != 0)
            quad(entry, vc, light, overlay,
                    fx0, fy1, fz0, su0, sv0z,
                    fx0, fy1, fz1, su0, sv1z,
                    fx1, fy1, fz1, su1, sv1z,
                    fx1, fy1, fz0, su1, sv0z,
                    0, 1, 0);

        if ((faces & FACE_NORTH) != 0)
            quad(entry, vc, light, overlay,
                    fx1, fy1, fz0, su0, sv0y,
                    fx1, fy0, fz0, su0, sv1y,
                    fx0, fy0, fz0, su1, sv1y,
                    fx0, fy1, fz0, su1, sv0y,
                    0, 0, -1);

        if ((faces & FACE_SOUTH) != 0)
            quad(entry, vc, light, overlay,
                    fx0, fy1, fz1, su0, sv0y,
                    fx0, fy0, fz1, su0, sv1y,
                    fx1, fy0, fz1, su1, sv1y,
                    fx1, fy1, fz1, su1, sv0y,
                    0, 0, 1);

        if ((faces & FACE_WEST) != 0)
            quad(entry, vc, light, overlay,
                    fx0, fy1, fz0, su0z, sv0y,
                    fx0, fy0, fz0, su0z, sv1y,
                    fx0, fy0, fz1, su1z, sv1y,
                    fx0, fy1, fz1, su1z, sv0y,
                    -1, 0, 0);

        if ((faces & FACE_EAST) != 0)
            quad(entry, vc, light, overlay,
                    fx1, fy1, fz1, su0z, sv0y,
                    fx1, fy0, fz1, su0z, sv1y,
                    fx1, fy0, fz0, su1z, sv1y,
                    fx1, fy1, fz0, su1z, sv0y,
                    1, 0, 0);
    }

    private void renderCrossPlanes(PoseStack pose, VertexConsumer vc, TextureAtlasSprite sprite,
                                   int light, int overlay) {
        PoseStack.Pose entry = pose.last();

        float u0 = sprite.getU(0f);
        float u1 = sprite.getU(1f);
        float v0 = sprite.getV(0f);
        float v1 = sprite.getV(1f);

        float x0 = 1 / 16f, x1 = 15 / 16f;
        float z0 = 1 / 16f, z1 = 15 / 16f;

        // horizontal disc — UP face
        float yUp = 9 / 16f;
        quad(entry, vc, light, overlay,
                x0, yUp, z0, u0, v0,
                x0, yUp, z1, u0, v1,
                x1, yUp, z1, u1, v1,
                x1, yUp, z0, u1, v0,
                0, 1, 0);

        // horizontal disc — DOWN face
        float yDn = 7 / 16f;
        quad(entry, vc, light, overlay,
                x0, yDn, z1, u0, v1,
                x0, yDn, z0, u0, v0,
                x1, yDn, z0, u1, v0,
                x1, yDn, z1, u1, v1,
                0, -1, 0);

        // vertical NS — NORTH face at z=7/16
        float zN = 7 / 16f;
        float yBot = 6 / 16f, yTop = 10 / 16f;
        quad(entry, vc, light, overlay,
                x1, yTop, zN, u0, v0,
                x1, yBot, zN, u0, v1,
                x0, yBot, zN, u1, v1,
                x0, yTop, zN, u1, v0,
                0, 0, -1);

        // vertical NS — SOUTH face at z=9/16
        float zS = 9 / 16f;
        quad(entry, vc, light, overlay,
                x0, yTop, zS, u0, v0,
                x0, yBot, zS, u0, v1,
                x1, yBot, zS, u1, v1,
                x1, yTop, zS, u1, v0,
                0, 0, 1);

        // vertical EW — WEST face at x=7/16
        float xW = 7 / 16f;
        quad(entry, vc, light, overlay,
                xW, yTop, z0, u0, v0,
                xW, yBot, z0, u0, v1,
                xW, yBot, z1, u1, v1,
                xW, yTop, z1, u1, v0,
                -1, 0, 0);

        // vertical EW — EAST face at x=9/16
        float xE = 9 / 16f;
        quad(entry, vc, light, overlay,
                xE, yTop, z1, u0, v0,
                xE, yBot, z1, u0, v1,
                xE, yBot, z0, u1, v1,
                xE, yTop, z0, u1, v0,
                1, 0, 0);
    }

    private void quad(PoseStack.Pose entry, VertexConsumer vc, int light, int overlay,
                      float x0, float y0, float z0, float u0, float v0,
                      float x1, float y1, float z1, float u1, float v1,
                      float x2, float y2, float z2, float u2, float v2,
                      float x3, float y3, float z3, float u3, float v3,
                      float nx, float ny, float nz) {
        vertex(entry, vc, x0, y0, z0, u0, v0, nx, ny, nz, light, overlay);
        vertex(entry, vc, x1, y1, z1, u1, v1, nx, ny, nz, light, overlay);
        vertex(entry, vc, x2, y2, z2, u2, v2, nx, ny, nz, light, overlay);
        vertex(entry, vc, x3, y3, z3, u3, v3, nx, ny, nz, light, overlay);
    }

    private void vertex(PoseStack.Pose entry, VertexConsumer vc,
                        float x, float y, float z, float u, float v,
                        float nx, float ny, float nz, int light, int overlay) {
        vc.addVertex(entry, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(entry, nx, ny, nz);
    }
}
