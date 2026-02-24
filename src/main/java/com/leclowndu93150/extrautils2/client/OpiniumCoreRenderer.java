package com.leclowndu93150.extrautils2.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class OpiniumCoreRenderer extends BlockEntityWithoutLevelRenderer {

    private static final float ISO_X_DEG = 30.0f;
    private static final float ISO_Y_DEG = 45.0f;
    private long baseGameTime = 0L;
    private Object lastLevel = null;
    private static final ItemStack[][] TIER_DATA = {
            {new ItemStack(Blocks.IRON_BLOCK), new ItemStack(Items.CHARCOAL)},
            {new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Blocks.IRON_BLOCK)},
            {new ItemStack(Blocks.DIAMOND_BLOCK), new ItemStack(Blocks.GOLD_BLOCK)},
            {new ItemStack(Blocks.EMERALD_BLOCK), new ItemStack(Blocks.DIAMOND_BLOCK)},
            {new ItemStack(Blocks.CHORUS_FLOWER), new ItemStack(Blocks.EMERALD_BLOCK)},
            {new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack(Blocks.CHORUS_FLOWER)},
            {new ItemStack(Items.ELYTRA), new ItemStack(Items.EXPERIENCE_BOTTLE)},
            {new ItemStack(Items.NETHER_STAR), new ItemStack(Items.ELYTRA)},
            {new ItemStack(Items.IRON_INGOT), new ItemStack(Items.NETHER_STAR)},
    };

    public OpiniumCoreRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack pose,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        int tier = getTier(stack);
        if (tier < 0 || tier > 8) return;

        ItemStack centerStack = TIER_DATA[tier][0];
        ItemStack orbitStack = TIER_DATA[tier][1];

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        float time = getTime();

        renderCenter(renderer, centerStack, pose, buffer, displayContext, packedLight, packedOverlay, time);
        renderOrbitals(renderer, orbitStack, pose, buffer, displayContext, packedLight, packedOverlay, time);
    }

    private void renderCenter(ItemRenderer renderer, ItemStack stack, PoseStack pose,
                              MultiBufferSource buffer, ItemDisplayContext ctx, int light, int overlay, float time) {
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);

        pose.mulPose(Axis.YP.rotationDegrees(Mth.wrapDegrees(-time * 3f)));
        applyIsometric(pose);

        pose.scale(0.4f, 0.4f, 0.4f);

        renderer.renderStatic(stack, ctx, light, overlay, pose, buffer, null, 0);
        pose.popPose();
    }

    private void renderOrbitals(ItemRenderer renderer, ItemStack stack, PoseStack pose,
                                MultiBufferSource buffer, ItemDisplayContext ctx, int light, int overlay, float time) {
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);

        float orbitTime = time * 1.6f;
        pose.mulPose(Axis.YP.rotationDegrees(Mth.wrapDegrees(orbitTime * 1.8f)));
        pose.mulPose(Axis.XP.rotationDegrees(Mth.wrapDegrees(orbitTime * 1.2f)));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.wrapDegrees(orbitTime * 0.6f)));

        float dist = 0.35f;
        float[][] offsets = {
                {dist, 0, 0},
                {-dist, 0, 0},
                {0, 0, dist},
                {0, 0, -dist}
        };

        for (float[] off : offsets) {
            pose.pushPose();
            pose.translate(off[0], off[1], off[2]);
            applyIsometric(pose);
            pose.scale(0.25f, 0.25f, 0.25f);
            renderer.renderStatic(stack, ctx, light, overlay, pose, buffer, null, 0);
            pose.popPose();
        }

        pose.popPose();
    }

    private int getTier(ItemStack stack) {
        String id = stack.getItem().builtInRegistryHolder().key().location().getPath();
        if (id.startsWith("opinium_core_")) {
            try {
                return Integer.parseInt(id.substring("opinium_core_".length()));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private float getTime() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 0;
        if (mc.level != lastLevel) {
            lastLevel = mc.level;
            baseGameTime = mc.level.getGameTime();
        }
        float partial = mc.getTimer().getGameTimeDeltaPartialTick(true);
        long ticks = mc.level.getGameTime() - baseGameTime;
        return (ticks + partial) * 2;
    }

    private static void applyIsometric(PoseStack pose) {
        pose.mulPose(Axis.XP.rotationDegrees(ISO_X_DEG));
        pose.mulPose(Axis.YP.rotationDegrees(ISO_Y_DEG));
    }
}
