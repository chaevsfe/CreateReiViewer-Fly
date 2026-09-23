package dev.chaevsfe.createreiviewer.client.jei;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.ViewerRecipe;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategory;
import dev.chaevsfe.createreiviewer.client.registry.ViewerClientPlugins;
import dev.chaevsfe.createreiviewer.registry.ViewerMapping;
import dev.chaevsfe.createreiviewer.registry.ViewerPlugins;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.Internal;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ViewerJeiPlugin implements IModPlugin {
    private static final Identifier UID = CreateReiViewer.id("jei_plugin");

    private final Map<Identifier, IRecipeType<ViewerRecipe>> types = new LinkedHashMap<>();

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    private synchronized Map<Identifier, IRecipeType<ViewerRecipe>> types() {
        if (types.isEmpty()) {
            for (ViewerClientPlugins.Entry entry : ViewerClientPlugins.entries()) {
                if (entry.category().jeiCategory()) {
                    Identifier id = entry.category().id();
                    types.put(id, IRecipeType.create(id, ViewerRecipe.class));
                }
            }
        }
        return types;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        try {
            if (CuttingStepRenderer.registerIfMissing()) {
                CreateReiViewer.LOGGER.info("Registered a JEI sequenced assembly renderer for cutting steps");
            }
        } catch (RuntimeException | LinkageError exception) {
            CreateReiViewer.LOGGER.warn("Could not register a JEI sequenced assembly renderer for cutting steps", exception);
        }
        int added = 0;
        for (ViewerClientPlugins.Entry entry : ViewerClientPlugins.entries()) {
            ViewerCategory category = entry.category();
            if (!category.jeiCategory()) {
                continue;
            }
            try {
                registration.addRecipeCategories(new ViewerJeiCategory(category, types().get(category.id())));
                added++;
            } catch (RuntimeException exception) {
                CreateReiViewer.LOGGER.error("Could not register JEI category {} from {}", category.id(), entry.owner(), exception);
            }
        }
        CreateReiViewer.LOGGER.info("Registered {} add-on JEI categories", added);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (ViewerClientPlugins.Entry entry : ViewerClientPlugins.entries()) {
            ViewerCategory category = entry.category();
            if (category.jeiCategory() && !category.workstations().isEmpty()) {
                registration.addCraftingStation(types().get(category.id()), category.workstations().toArray(ItemStack[]::new));
            }
        }
        for (ViewerClientPlugins.Workstations extra : ViewerClientPlugins.workstations()) {
            Optional<IRecipeType<?>> type = registration.getJeiHelpers().getRecipeType(extra.category());
            if (type.isEmpty()) {
                CreateReiViewer.LOGGER.warn("{} added workstations to {}, which JEI does not know", extra.owner(), extra.category());
                continue;
            }
            try {
                registration.addCraftingStation(type.get(), extra.stacks().toArray(ItemStack[]::new));
                CreateReiViewer.LOGGER.info("{} added {} workstations to JEI recipe type {}", extra.owner(), extra.stacks().size(), extra.category());
            } catch (RuntimeException exception) {
                CreateReiViewer.LOGGER.error("Could not add {}'s workstations to JEI recipe type {}", extra.owner(), extra.category(), exception);
            }
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeMap recipes = Internal.getClientSyncedRecipes();
        Map<Identifier, List<ViewerRecipe>> byCategory = new LinkedHashMap<>();
        Set<Identifier> undrawn = new LinkedHashSet<>();
        Set<Identifier> optedOut = new LinkedHashSet<>();
        for (ViewerClientPlugins.Entry entry : ViewerClientPlugins.entries()) {
            if (!entry.category().jeiCategory()) {
                optedOut.add(entry.category().id());
            }
        }
        for (ViewerMapping<?> mapping : ViewerPlugins.mappings()) {
            if (!types().containsKey(mapping.category())) {
                if (!optedOut.contains(mapping.category())) {
                    undrawn.add(mapping.category());
                }
                continue;
            }
            List<ViewerRecipe> list = byCategory.computeIfAbsent(mapping.category(), id -> new ArrayList<>());
            try {
                for (RecipeHolder<?> holder : holders(recipes, mapping.type())) {
                    ViewerRecipe recipe = mapping.map(holder);
                    if (recipe != null) {
                        list.add(recipe);
                    }
                }
            } catch (RuntimeException exception) {
                CreateReiViewer.LOGGER.error("Could not read the {} recipes of {} for JEI", mapping.category(), mapping.owner(), exception);
            }
        }
        int total = 0;
        for (Map.Entry<Identifier, List<ViewerRecipe>> entry : byCategory.entrySet()) {
            registration.addRecipes(types().get(entry.getKey()), entry.getValue());
            total += entry.getValue().size();
            CreateReiViewer.LOGGER.info("JEI: {} recipes for category {}", entry.getValue().size(), entry.getKey());
        }
        CreateReiViewer.LOGGER.info("JEI: {} add-on recipes across {} categories from {} synced recipes (synced with server: {})",
            total, byCategory.size(), recipes.values().size(), recipes != RecipeMap.EMPTY);
        if (!undrawn.isEmpty()) {
            CreateReiViewer.LOGGER.warn("JEI: no client layout for categories {}; their recipes are not shown", undrawn);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Collection<RecipeHolder<?>> holders(RecipeMap recipes, RecipeType<?> type) {
        return (Collection) recipes.byType((RecipeType) type);
    }
}
