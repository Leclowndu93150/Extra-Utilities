package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.LuxSaberColor;
import com.leclowndu93150.extrautils2.item.LuxSaberItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;

import java.util.List;

@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class LuxSaberFirstPersonRenderer {
    private static final float LUX_SABER_BLADE_START_Y = 15.0F / 16.0F;
    private static final float LUX_SABER_MAX_MODEL_Y = 32.0F;

    private LuxSaberFirstPersonRenderer() {
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;
        ItemStack stack = event.getItemStack();
        if (player == null || !(stack.getItem() instanceof LuxSaberItem saberItem)) {
            return;
        }

        boolean mainHand = event.getHand() == InteractionHand.MAIN_HAND;
        HumanoidArm arm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean rightHand = arm == HumanoidArm.RIGHT;
        ItemDisplayContext displayContext = rightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        event.setCanceled(true);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        applyFirstPersonTransforms(poseStack, arm, event.getSwingProgress(), event.getEquipProgress());

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        ModelManager modelManager = itemRenderer.getItemModelShaper().getModelManager();
        BakedModel baseModel = modelManager.getModel(itemModel(saberItem.getColor().itemId()));
        itemRenderer.render(
                stack,
                displayContext,
                !rightHand,
                poseStack,
                event.getMultiBufferSource(),
                event.getPackedLight(),
                OverlayTexture.NO_OVERLAY,
                baseModel
        );

        int stage = LuxSaberClientState.getStage(stack);
        if (stage > 0) {
            BakedModel bladeModel = modelManager.getModel(itemModel(saberItem.getColor().itemId() + "_blade_stage_" + "%02d".formatted(stage)));
            RenderType renderType = saberItem.getColor() == LuxSaberColor.BLACK ? XURenderTypes.LUX_SABER_BLACK_BLADE : XURenderTypes.LUX_SABER_BLADE;
            renderModel(
                    stack,
                    displayContext,
                    !rightHand,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight(),
                    bladeModel,
                    renderType,
                    bladeScaleY(stage)
            );
        }

        poseStack.popPose();
    }

    private static void applyFirstPersonTransforms(PoseStack poseStack, HumanoidArm arm, float swingProgress, float equipProgress) {
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float swingRoot = Mth.sqrt(swingProgress);
        poseStack.translate(
                side * -0.4F * Mth.sin(swingRoot * (float) Math.PI),
                0.2F * Mth.sin(swingRoot * (float) (Math.PI * 2)),
                -0.2F * Mth.sin(swingProgress * (float) Math.PI)
        );
        poseStack.translate(side * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);

        float swingSquared = Mth.sin(swingProgress * swingProgress * (float) Math.PI);
        float swingMain = Mth.sin(swingRoot * (float) Math.PI);
        poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + swingSquared * -20.0F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(side * swingMain * -20.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(swingMain * -80.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F));
    }

    private static void renderModel(
            ItemStack stack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            BakedModel model,
            RenderType renderType,
            float scaleY
    ) {
        poseStack.pushPose();
        model.getTransforms().getTransform(displayContext).apply(leftHand, poseStack);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        if (scaleY != 1.0F) {
            poseStack.translate(0.0F, LUX_SABER_BLADE_START_Y, 0.0F);
            poseStack.scale(1.0F, scaleY, 1.0F);
            poseStack.translate(0.0F, -LUX_SABER_BLADE_START_Y, 0.0F);
        }
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, stack.hasFoil());
        renderModelLists(model, poseStack, vertexConsumer, packedLight);
        poseStack.popPose();
    }

    private static float bladeScaleY(int stage) {
        float extension = stage / (float) LuxSaberClientState.MODEL_STAGES;
        float targetTopY = 15.0F + 49.6F * extension;
        float clampedTopY = Math.min(LUX_SABER_MAX_MODEL_Y, targetTopY);
        float clampedLength = clampedTopY - 15.0F;
        if (clampedLength <= 0.0F || targetTopY <= clampedTopY) {
            return 1.0F;
        }
        return (targetTopY - 15.0F) / clampedLength;
    }

    private static void renderModelLists(BakedModel model, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight) {
        RandomSource random = RandomSource.create();
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            renderQuadList(poseStack, vertexConsumer, model.getQuads(null, direction, random), packedLight);
        }
        random.setSeed(42L);
        renderQuadList(poseStack, vertexConsumer, model.getQuads(null, null, random), packedLight);
    }

    private static void renderQuadList(PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> quads, int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        for (BakedQuad quad : quads) {
            vertexConsumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, 1.0F, packedLight, 0);
        }
    }

    private static ModelResourceLocation itemModel(String path) {
        return ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, path));
    }
}
