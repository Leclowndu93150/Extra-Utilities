package com.leclowndu93150.extrautils2.client.sprite;

import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModSpriteSourceTypes {
    public static final SpriteSourceType COMPRESSED = new SpriteSourceType(CompressedSpriteSource.CODEC);

    private ModSpriteSourceTypes() {}
}
