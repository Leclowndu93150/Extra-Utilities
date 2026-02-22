package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper efh = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(event.includeClient(), new XUBlockStateProvider(output, efh));
        gen.addProvider(event.includeClient(), new XUItemModelProvider(output, efh));
        gen.addProvider(event.includeClient(), new XULanguageProvider(output));
        gen.addProvider(event.includeServer(), new XURecipeProvider(output, lookupProvider));
        gen.addProvider(event.includeServer(), new XULootTableProvider(output, lookupProvider));

        XUBlockTagProvider blockTags = new XUBlockTagProvider(output, lookupProvider, efh);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new XUItemTagProvider(output, lookupProvider, efh, blockTags));
    }
}
