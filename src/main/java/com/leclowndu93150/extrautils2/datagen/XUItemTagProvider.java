package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import java.util.concurrent.CompletableFuture;

public class XUItemTagProvider extends ItemTagsProvider {
    public XUItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper efh, XUBlockTagProvider blockTags) {
        super(output, lookupProvider, blockTags.contentsGetter(), ExtraUtilities.MODID, efh);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }
}
