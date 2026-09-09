package dev.chaevsfe.createreiviewer.client;

import com.zurrtum.create.AllItems;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.client.category.MixingCategory;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class CreateReiClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MixingCategory());
        registry.addWorkstations(
            CreateReiCategories.MIXING,
            EntryStacks.of(AllItems.MECHANICAL_MIXER),
            EntryStacks.of(AllItems.BASIN)
        );
        CreateReiViewer.LOGGER.info("Registered 1 REI category");
    }
}
