package com.leclowndu93150.extrautils2.client.ctm;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public final class CTMModelLoader implements IGeometryLoader<CTMModelLoader.CTMGeometry> {
    public static final CTMModelLoader INSTANCE = new CTMModelLoader();

    private CTMModelLoader() {}

    @Override
    public CTMGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException {
        String texture = json.get("texture").getAsString();
        boolean translucent = json.has("translucent") && json.get("translucent").getAsBoolean();
        return new CTMGeometry(ResourceLocation.parse(texture), translucent);
    }

    public static final class CTMGeometry implements IUnbakedGeometry<CTMGeometry> {
        private final ResourceLocation textureBase;
        private final boolean translucent;

        CTMGeometry(ResourceLocation textureBase, boolean translucent) {
            this.textureBase = textureBase;
            this.translucent = translucent;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                               Function<Material, TextureAtlasSprite> spriteGetter,
                               ModelState modelState, ItemOverrides overrides) {
            String basePath = textureBase.getPath();
            if (basePath.startsWith("block/")) basePath = basePath.substring("block/".length());

            TextureAtlasSprite[] sprites = new TextureAtlasSprite[47];
            for (int i = 0; i < 47; i++) {
                ResourceLocation spriteId = ResourceLocation.fromNamespaceAndPath(
                        textureBase.getNamespace(), "ctm/" + basePath + "_" + i);
                Material mat = new Material(InventoryMenu.BLOCK_ATLAS, spriteId);
                sprites[i] = spriteGetter.apply(mat);
            }

            TextureAtlasSprite particle = sprites[ConnectedTexturesHelper.textureFromArrangement[15]];

            return new CTMBakedModel(sprites, particle, context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), translucent);
        }
    }
}
