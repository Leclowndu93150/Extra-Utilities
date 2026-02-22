package com.leclowndu93150.extrautils2.client.sprite;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public record CompressedSpriteSource(ResourceLocation baseTexture, int level, int maxLevel) implements SpriteSource {
    public static final MapCodec<CompressedSpriteSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("base").forGetter(CompressedSpriteSource::baseTexture),
                    com.mojang.serialization.Codec.INT.fieldOf("level").forGetter(CompressedSpriteSource::level),
                    com.mojang.serialization.Codec.INT.fieldOf("max_level").forGetter(CompressedSpriteSource::maxLevel)
            ).apply(instance, CompressedSpriteSource::new)
    );

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        output.add(spriteId(), new Supplier(resourceManager, baseTexture, level, maxLevel));
    }

    @Override
    public SpriteSourceType type() {
        return ModSpriteSourceTypes.COMPRESSED;
    }

    private ResourceLocation spriteId() {
        return ResourceLocation.fromNamespaceAndPath("extrautils2", "compressed/" + Supplier.stripPrefix(baseTexture.getPath()) + "_" + level);
    }

    private static class Supplier implements SpriteSupplier {
        private final ResourceManager resourceManager;
        private final ResourceLocation baseTexture;
        private final int level;
        private final int maxLevel;

        private Supplier(ResourceManager resourceManager, ResourceLocation baseTexture, int level, int maxLevel) {
            this.resourceManager = resourceManager;
            this.baseTexture = baseTexture;
            this.level = level;
            this.maxLevel = maxLevel;
        }

        @Override
        public SpriteContents apply(SpriteResourceLoader loader) {
            ResourceLocation file = SpriteSource.TEXTURE_ID_CONVERTER.idToFile(baseTexture);
            Optional<Resource> resource = resourceManager.getResource(file);
            if (resource.isEmpty()) return null;

            ResourceMetadata metadata;
            try {
                Collection<MetadataSectionSerializer<?>> sections = List.of(AnimationMetadataSection.SERIALIZER);
                metadata = resource.get().metadata().copySections(sections);
            } catch (Exception e) {
                SpriteResourceLoader.LOGGER.error("Unable to parse metadata from {}", file, e);
                return null;
            }

            NativeImage image;
            try (InputStream input = resource.get().open()) {
                image = NativeImage.read(input);
            } catch (IOException e) {
                SpriteResourceLoader.LOGGER.error("Using missing texture, unable to load {}", file, e);
                return null;
            }

            AnimationMetadataSection animation = metadata.getSection(AnimationMetadataSection.SERIALIZER)
                    .orElse(AnimationMetadataSection.EMPTY);
            FrameSize frameSize = animation.calculateFrameSize(image.getWidth(), image.getHeight());
            if (!Mth.isMultipleOf(image.getWidth(), frameSize.width()) || !Mth.isMultipleOf(image.getHeight(), frameSize.height())) {
                SpriteResourceLoader.LOGGER.error(
                        "Image {} size {},{} is not multiple of frame size {},{}",
                        file,
                        image.getWidth(),
                        image.getHeight(),
                        frameSize.width(),
                        frameSize.height()
                );
                image.close();
                return null;
            }

            NativeImage shaded = shade(image, level, maxLevel);
            image.close();
            return new SpriteContents(spriteId(baseTexture, level), frameSize, shaded, metadata);
        }

        private static ResourceLocation spriteId(ResourceLocation base, int level) {
            return ResourceLocation.fromNamespaceAndPath("extrautils2", "compressed/" + stripPrefix(base.getPath()) + "_" + level);
        }

        private static String stripPrefix(String path) {
            if (path.startsWith("block/")) return path.substring("block/".length());
            if (path.startsWith("item/")) return path.substring("item/".length());
            return path;
        }

        private static NativeImage shade(NativeImage base, int level, int maxLevel) {
            int w = base.getWidth();
            int h = base.getHeight();
            NativeImage out = new NativeImage(w, h, false);
            float maxN = Mth.sqrt(maxLevel * 8.0F) + 0.5F;
            float border = (2.0F + level / maxN / 2.0F) / 32.0F;

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    float fx = x / (float)(w - 1);
                    float fy = (y % w) / (float)(w - 1);
                    int col = base.getPixelRGBA(x, y);
                    float d = Mth.sqrt((fx - 0.5F) * (fx - 0.5F) + (fy - 0.5F) * (fy - 0.5F));
                    float br = 1.0F - (float)level / maxN + (0.5F - d) / 2.0F;
                    if (br > 1.0F) br = 1.0F;

                    if (fx <= border || fy < border || 1.0F - fx <= border || 1.0F - fy <= border) {
                        br *= 0.5F;
                    }

                    int a = FastColor.ABGR32.alpha(col);
                    int r = Mth.clamp(Math.round(FastColor.ABGR32.red(col) * br), 0, 255);
                    int g = Mth.clamp(Math.round(FastColor.ABGR32.green(col) * br), 0, 255);
                    int b = Mth.clamp(Math.round(FastColor.ABGR32.blue(col) * br), 0, 255);
                    out.setPixelRGBA(x, y, FastColor.ABGR32.color(a, b, g, r));
                }
            }

            return out;
        }
    }
}
