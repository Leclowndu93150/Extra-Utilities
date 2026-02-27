package com.leclowndu93150.extrautils2.client.ctm;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.leclowndu93150.extrautils2.client.sprite.ModSpriteSourceTypes;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public record CTMSpriteSource(ResourceLocation base) implements SpriteSource {
    public static final MapCodec<CTMSpriteSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("base").forGetter(CTMSpriteSource::base)
            ).apply(instance, CTMSpriteSource::new)
    );

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        for (int i = 0; i < 47; i++) {
            ResourceLocation spriteId = spriteId(base, i);
            output.add(spriteId, new Supplier(resourceManager, base, i));
        }
    }

    @Override
    public SpriteSourceType type() {
        return ModSpriteSourceTypes.CTM;
    }

    private static ResourceLocation spriteId(ResourceLocation base, int index) {
        String path = base.getPath();
        if (path.startsWith("block/")) path = path.substring("block/".length());
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), "ctm/" + path + "_" + index);
    }

    private static class Supplier implements SpriteSupplier {
        private final ResourceManager resourceManager;
        private final ResourceLocation base;
        private final int texID;

        Supplier(ResourceManager resourceManager, ResourceLocation base, int texID) {
            this.resourceManager = resourceManager;
            this.base = base;
            this.texID = texID;
        }

        @Override
        public SpriteContents apply(SpriteResourceLoader loader) {
            ResourceLocation file = SpriteSource.TEXTURE_ID_CONVERTER.idToFile(base);
            Optional<Resource> resource = resourceManager.getResource(file);
            if (resource.isEmpty()) return null;

            NativeImage image;
            try (InputStream input = resource.get().open()) {
                image = NativeImage.read(input);
            } catch (IOException e) {
                return null;
            }

            int size = image.getWidth();
            if (image.getHeight() != size * 5) {
                image.close();
                return null;
            }

            int half = size / 2;
            NativeImage out = new NativeImage(size, size, false);

            int textureIndexMask = ConnectedTexturesHelper.textureIds[texID];

            int subImage = textureIndexMask / 125;
            copyRegion(image, 0, subImage * size, half, half, out, 0, 0);
            textureIndexMask -= subImage * 125;

            subImage = textureIndexMask / 25;
            copyRegion(image, 0, subImage * size + half, half, half, out, 0, half);
            textureIndexMask -= subImage * 25;

            subImage = textureIndexMask / 5;
            copyRegion(image, half, subImage * size + half, half, half, out, half, half);
            textureIndexMask -= subImage * 5;

            copyRegion(image, half, textureIndexMask * size, half, half, out, half, 0);

            image.close();

            ResourceLocation spriteId = spriteId(base, texID);
            return new SpriteContents(spriteId, new FrameSize(size, size), out, ResourceMetadata.EMPTY);
        }

        private static void copyRegion(NativeImage src, int srcX, int srcY, int w, int h, NativeImage dst, int dstX, int dstY) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    dst.setPixelRGBA(dstX + x, dstY + y, src.getPixelRGBA(srcX + x, srcY + y));
                }
            }
        }
    }
}
