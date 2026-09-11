package dev.chaevsfe.createreiviewer.client;

import com.zurrtum.create.AllItems;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.CreateReiApi;
import dev.chaevsfe.createreiviewer.client.category.AutomaticBrewingCategory;
import dev.chaevsfe.createreiviewer.client.category.AutomaticPackingCategory;
import dev.chaevsfe.createreiviewer.client.category.AutomaticShapelessCategory;
import dev.chaevsfe.createreiviewer.client.category.BlockCuttingCategory;
import dev.chaevsfe.createreiviewer.client.category.CrushingCategory;
import dev.chaevsfe.createreiviewer.client.category.DeployingCategory;
import dev.chaevsfe.createreiviewer.client.category.DrainingCategory;
import dev.chaevsfe.createreiviewer.client.category.FanBlastingCategory;
import dev.chaevsfe.createreiviewer.client.category.FanHauntingCategory;
import dev.chaevsfe.createreiviewer.client.category.FanSmokingCategory;
import dev.chaevsfe.createreiviewer.client.category.FanWashingCategory;
import dev.chaevsfe.createreiviewer.client.category.ItemApplicationCategory;
import dev.chaevsfe.createreiviewer.client.category.MechanicalCraftingCategory;
import dev.chaevsfe.createreiviewer.client.category.MillingCategory;
import dev.chaevsfe.createreiviewer.client.category.MixingCategory;
import dev.chaevsfe.createreiviewer.client.category.MysteryConversionCategory;
import dev.chaevsfe.createreiviewer.client.category.PackingCategory;
import dev.chaevsfe.createreiviewer.client.category.PressingCategory;
import dev.chaevsfe.createreiviewer.client.category.SandpaperPolishingCategory;
import dev.chaevsfe.createreiviewer.client.category.SawingCategory;
import dev.chaevsfe.createreiviewer.client.category.SequencedAssemblyCategory;
import dev.chaevsfe.createreiviewer.client.category.SpoutFillingCategory;
import dev.chaevsfe.createreiviewer.client.widget.FluidEntryRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import me.shedaniel.rei.api.client.entry.renderer.EntryRendererRegistry;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.StringJoiner;

public class CreateReiClientPlugin implements REIClientPlugin {
    @Override
    public double getPriority() {
        return CreateReiApi.VIEWER_PLUGIN_PRIORITY;
    }

    @Override
    public void registerEntryRenderers(EntryRendererRegistry registry) {
        registry.register(VanillaEntryTypes.FLUID, FluidEntryRenderer.provider());
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(
            new SequencedAssemblyCategory(),
            new PressingCategory(),
            new MixingCategory(),
            new AutomaticPackingCategory(),
            new PackingCategory(),
            new AutomaticShapelessCategory(),
            new MillingCategory(),
            new SawingCategory(),
            new CrushingCategory(),
            new MysteryConversionCategory(),
            new ItemApplicationCategory(),
            new DeployingCategory(),
            new DrainingCategory(),
            new MechanicalCraftingCategory(),
            new SpoutFillingCategory(),
            new SandpaperPolishingCategory(),
            new FanBlastingCategory(),
            new FanHauntingCategory(),
            new FanSmokingCategory(),
            new FanWashingCategory(),
            new AutomaticBrewingCategory(),
            new BlockCuttingCategory()
        );

        registry.addWorkstations(CreateReiCategories.AUTOMATIC_PACKING,
            EntryStacks.of(AllItems.MECHANICAL_PRESS), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CreateReiCategories.PACKING,
            EntryStacks.of(AllItems.MECHANICAL_PRESS), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CreateReiCategories.PRESSING, EntryStacks.of(AllItems.MECHANICAL_PRESS));
        registry.addWorkstations(CreateReiCategories.AUTOMATIC_SHAPELESS,
            EntryStacks.of(AllItems.MECHANICAL_MIXER), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CreateReiCategories.MIXING,
            EntryStacks.of(AllItems.MECHANICAL_MIXER), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CreateReiCategories.AUTOMATIC_BREWING,
            EntryStacks.of(AllItems.MECHANICAL_MIXER), EntryStacks.of(AllItems.BASIN));
        registry.addWorkstations(CreateReiCategories.MILLING, EntryStacks.of(AllItems.MILLSTONE));
        registry.addWorkstations(CreateReiCategories.SAWING, EntryStacks.of(AllItems.MECHANICAL_SAW));
        registry.addWorkstations(CreateReiCategories.BLOCK_CUTTING, EntryStacks.of(AllItems.MECHANICAL_SAW));
        registry.addWorkstations(CreateReiCategories.CRUSHING, EntryStacks.of(AllItems.CRUSHING_WHEEL));
        registry.addWorkstations(CreateReiCategories.DEPLOYING,
            EntryStacks.of(AllItems.DEPLOYER), EntryStacks.of(AllItems.DEPOT), EntryStacks.of(AllItems.BELT_CONNECTOR));
        registry.addWorkstations(CreateReiCategories.DRAINING, EntryStacks.of(AllItems.ITEM_DRAIN));
        registry.addWorkstations(CreateReiCategories.MECHANICAL_CRAFTING, EntryStacks.of(AllItems.MECHANICAL_CRAFTER));
        registry.addWorkstations(CreateReiCategories.SPOUT_FILLING, EntryStacks.of(AllItems.SPOUT));
        registry.addWorkstations(CreateReiCategories.SANDPAPER_POLISHING,
            EntryStacks.of(AllItems.SAND_PAPER), EntryStacks.of(AllItems.RED_SAND_PAPER));
        for (var fan : java.util.List.of(
            CreateReiCategories.FAN_BLASTING,
            CreateReiCategories.FAN_HAUNTING,
            CreateReiCategories.FAN_SMOKING,
            CreateReiCategories.FAN_WASHING
        )) {
            registry.addWorkstations(fan, EntryStacks.of(AllItems.ENCASED_FAN));
        }

        CreateReiViewer.LOGGER.info("Registered {} REI categories", CreateReiCategories.ALL.size());
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (stage == ReloadStage.END) {
            ResyncClient.onReloadEnded();
            try {
                logCategoryOrder();
            } catch (RuntimeException exception) {
                CreateReiViewer.LOGGER.warn("Could not read the REI category order", exception);
            }
        }
    }

    private static void logCategoryOrder() {
        StringJoiner order = new StringJoiner(", ");
        for (CategoryRegistry.CategoryConfiguration<?> configuration : CategoryRegistry.getInstance()) {
            order.add(configuration.getCategoryIdentifier().getIdentifier().toString());
        }
        CreateReiViewer.LOGGER.info("Category order: {}", order);
    }
}
