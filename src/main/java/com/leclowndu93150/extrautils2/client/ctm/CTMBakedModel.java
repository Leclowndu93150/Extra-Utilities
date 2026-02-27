package com.leclowndu93150.extrautils2.client.ctm;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CTMBakedModel implements IDynamicBakedModel {
    public static final ModelProperty<int[]> CTM_DATA = new ModelProperty<>();

    private static final Direction[] UP_DIR = {Direction.NORTH, Direction.NORTH, Direction.UP, Direction.UP, Direction.UP, Direction.UP};
    private static final Direction[] LEFT_DIR = {Direction.WEST, Direction.WEST, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};

    private static final ItemTransforms BLOCK_TRANSFORMS = new ItemTransforms(
            new ItemTransform(new Vector3f(75, 225, 0), new Vector3f(0, 2.5f, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
            new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
            new ItemTransform(new Vector3f(0, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
            new ItemTransform(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
            ItemTransform.NO_TRANSFORM,
            new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.625f, 0.625f, 0.625f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 3, 0), new Vector3f(0.25f, 0.25f, 0.25f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f))
    );

    private final TextureAtlasSprite[] sprites;
    private final TextureAtlasSprite particle;
    private final boolean ambientOcclusion;
    private final boolean gui3d;
    private final boolean blockLight;
    private final boolean translucent;
    private final Map<Direction, List<BakedQuad>> itemFaceQuads;

    public CTMBakedModel(TextureAtlasSprite[] sprites, TextureAtlasSprite particle,
                         boolean ambientOcclusion, boolean gui3d, boolean blockLight, boolean translucent) {
        this.sprites = sprites;
        this.particle = particle;
        this.ambientOcclusion = ambientOcclusion;
        this.gui3d = gui3d;
        this.blockLight = blockLight;
        this.translucent = translucent;

        int itemTexIdx = ConnectedTexturesHelper.textureFromArrangement[15];
        this.itemFaceQuads = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            itemFaceQuads.put(dir, buildFaceQuads(dir, itemTexIdx));
        }
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        int[] faceData = new int[6];
        for (Direction face : Direction.values()) {
            faceData[face.get3DDataValue()] = computeArrangement(level, pos, state, face);
        }
        return modelData.derive().with(CTM_DATA, faceData).build();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand,
                                     ModelData extraData, @Nullable RenderType renderType) {
        if (side == null) return Collections.emptyList();

        if (state == null) {
            return itemFaceQuads.getOrDefault(side, Collections.emptyList());
        }

        int[] faceData = extraData.get(CTM_DATA);
        int arrangement;
        if (faceData != null) {
            arrangement = faceData[side.get3DDataValue()];
        } else {
            arrangement = 15;
        }

        int texIdx = ConnectedTexturesHelper.textureFromArrangement[arrangement];
        return buildFaceQuads(side, texIdx);
    }

    private List<BakedQuad> buildFaceQuads(Direction face, int texIdx) {
        int[][] bounds = ConnectedTexturesHelper.texBounds[texIdx];

        List<BakedQuad> quads = new ArrayList<>(bounds.length);
        for (int[] bound : bounds) {
            float py0 = bound[2] / 16f;
            float py1 = bound[4] / 16f;
            quads.add(makeFaceQuad(face, sprites[bound[0]], py0, py1));
        }
        return quads;
    }

    private BakedQuad makeFaceQuad(Direction face, TextureAtlasSprite sprite, float py0, float py1) {
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
        builder.setSprite(sprite);
        builder.setDirection(face);
        builder.setShade(true);
        builder.setHasAmbientOcclusion(true);
        builder.setTintIndex(-1);

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float spriteV0 = sprite.getV0();
        float spriteV1 = sprite.getV1();
        float sv0 = spriteV0 + (spriteV1 - spriteV0) * py0;
        float sv1 = spriteV0 + (spriteV1 - spriteV0) * py1;

        float[][] pos = getFaceVertices(face, py0, py1);
        float nx = face.getStepX();
        float ny = face.getStepY();
        float nz = face.getStepZ();

        vertex(builder, pos[0], u0, sv0, nx, ny, nz);
        vertex(builder, pos[1], u0, sv1, nx, ny, nz);
        vertex(builder, pos[2], u1, sv1, nx, ny, nz);
        vertex(builder, pos[3], u1, sv0, nx, ny, nz);

        return builder.bakeQuad();
    }

    private void vertex(QuadBakingVertexConsumer b, float[] p, float u, float v, float nx, float ny, float nz) {
        b.addVertex(p[0], p[1], p[2]);
        b.setColor(255, 255, 255, 255);
        b.setUv(u, v);
        b.setUv2(0, 0);
        b.setNormal(nx, ny, nz);
    }

    private float[][] getFaceVertices(Direction face, float py0, float py1) {
        float yLo = py0;
        float yHi = py1;
        return switch (face) {
            case DOWN -> new float[][]{
                    {0, 0, 1 - yLo}, {0, 0, 1 - yHi}, {1, 0, 1 - yHi}, {1, 0, 1 - yLo}
            };
            case UP -> new float[][]{
                    {0, 1, yLo}, {0, 1, yHi}, {1, 1, yHi}, {1, 1, yLo}
            };
            case NORTH -> new float[][]{
                    {1, 1 - yLo, 0}, {1, 1 - yHi, 0}, {0, 1 - yHi, 0}, {0, 1 - yLo, 0}
            };
            case SOUTH -> new float[][]{
                    {0, 1 - yLo, 1}, {0, 1 - yHi, 1}, {1, 1 - yHi, 1}, {1, 1 - yLo, 1}
            };
            case WEST -> new float[][]{
                    {0, 1 - yLo, 0}, {0, 1 - yHi, 0}, {0, 1 - yHi, 1}, {0, 1 - yLo, 1}
            };
            case EAST -> new float[][]{
                    {1, 1 - yLo, 1}, {1, 1 - yHi, 1}, {1, 1 - yHi, 0}, {1, 1 - yLo, 0}
            };
        };
    }

    private int computeArrangement(BlockAndTintGetter level, BlockPos pos, BlockState state, Direction face) {
        int faceIdx = face.get3DDataValue();
        Direction up = UP_DIR[faceIdx];
        Direction left = LEFT_DIR[faceIdx];
        Direction right = left.getOpposite();
        Direction down = up.getOpposite();

        int ar = 0;
        boolean u = matches(level, pos, state, up);
        boolean r = matches(level, pos, state, right);
        boolean d = matches(level, pos, state, down);
        boolean l = matches(level, pos, state, left);

        if (!u) ar |= 1;
        if (!r) ar |= 2;
        if (!d) ar |= 4;
        if (!l) ar |= 8;

        if (!ConnectedTexturesHelper.isAdvancedArrangement[ar]) {
            return ar;
        }

        if (!matches(level, pos, state, up, right)) ar |= 16;
        if (!matches(level, pos, state, down, right)) ar |= 32;
        if (!matches(level, pos, state, down, left)) ar |= 64;
        if (!matches(level, pos, state, up, left)) ar |= 128;

        return ar;
    }

    private boolean matches(BlockAndTintGetter level, BlockPos pos, BlockState state, Direction... dirs) {
        BlockPos target = pos;
        for (Direction dir : dirs) {
            target = target.relative(dir);
        }
        return level.getBlockState(target).is(state.getBlock());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ambientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return gui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return blockLight;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return ChunkRenderTypeSet.of(translucent ? RenderType.translucent() : RenderType.cutout());
    }

    @Override
    public ItemTransforms getTransforms() {
        return BLOCK_TRANSFORMS;
    }
}
