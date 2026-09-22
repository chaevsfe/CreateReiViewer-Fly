package dev.chaevsfe.createreiviewer.registry;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.CreateViewerPlugin;
import dev.chaevsfe.createreiviewer.api.ViewerRecipeMapper;
import dev.chaevsfe.createreiviewer.api.ViewerRecipeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public final class ViewerPlugins {
    private static List<ViewerMapping<?>> mappings;
    private static Map<String, List<Identifier>> groups;

    private ViewerPlugins() {
    }

    public static synchronized List<ViewerMapping<?>> mappings() {
        load();
        return mappings;
    }

    public static synchronized Map<String, List<Identifier>> groups() {
        load();
        return groups;
    }

    public static synchronized void load() {
        if (mappings != null) {
            return;
        }
        List<ViewerMapping<?>> loaded = new ArrayList<>();
        Map<String, Set<Identifier>> byOwner = new LinkedHashMap<>();
        Set<RecipeSerializer<?>> serializers = new LinkedHashSet<>();
        List<EntrypointContainer<CreateViewerPlugin>> containers;
        try {
            containers = FabricLoader.getInstance().getEntrypointContainers(CreateViewerPlugin.ENTRYPOINT, CreateViewerPlugin.class);
        } catch (RuntimeException exception) {
            CreateReiViewer.LOGGER.error("Could not load the {} entrypoints; no add-on categories", CreateViewerPlugin.ENTRYPOINT, exception);
            containers = List.of();
        }
        for (EntrypointContainer<CreateViewerPlugin> container : containers) {
            String owner = container.getProvider().getMetadata().getName();
            Registry registry = new Registry(owner, loaded, byOwner.computeIfAbsent(owner, key -> new LinkedHashSet<>()), serializers);
            try {
                container.getEntrypoint().registerRecipes(registry);
            } catch (RuntimeException | LinkageError exception) {
                CreateReiViewer.LOGGER.error("{} failed to register its recipe viewer categories", owner, exception);
            }
        }
        Map<String, List<Identifier>> grouped = new LinkedHashMap<>();
        byOwner.forEach((owner, ids) -> {
            if (!ids.isEmpty()) {
                grouped.put(owner, List.copyOf(ids));
            }
        });
        mappings = List.copyOf(loaded);
        groups = Map.copyOf(grouped);
        CreateReiViewer.LOGGER.info("Loaded {} add-on recipe mappings into {} categories from {} plugins",
            mappings.size(), grouped.values().stream().mapToInt(List::size).sum(), containers.size());
        synchronizeSerializers(serializers);
    }

    private static void synchronizeSerializers(Set<RecipeSerializer<?>> serializers) {
        if (serializers.isEmpty()) {
            return;
        }
        FabricLoader loader = FabricLoader.getInstance();
        boolean server = loader.getEnvironmentType() == EnvType.SERVER;
        if (!server && !loader.isModLoaded("jei") && !loader.isModLoaded("rrv")) {
            CreateReiViewer.LOGGER.info("Not synchronizing {} add-on recipe serializers: this client has neither JEI nor RRV", serializers.size());
            return;
        }
        int synced = 0;
        for (RecipeSerializer<?> serializer : serializers) {
            try {
                RecipeSynchronization.synchronizeRecipeSerializer(serializer);
                synced++;
            } catch (RuntimeException exception) {
                CreateReiViewer.LOGGER.warn("Could not synchronize recipe serializer {}", serializer, exception);
            }
        }
        CreateReiViewer.LOGGER.info("Synchronizing {} add-on recipe serializers for JEI clients", synced);
    }

    private record Registry(
        String owner,
        List<ViewerMapping<?>> mappings,
        Set<Identifier> categories,
        Set<RecipeSerializer<?>> serializers
    ) implements ViewerRecipeRegistry {
        @Override
        public <T extends Recipe<?>> void add(
            Identifier category,
            RecipeType<? super T> type,
            Class<T> recipeClass,
            Predicate<RecipeHolder<T>> filter,
            ViewerRecipeMapper<T> mapper
        ) {
            mappings.add(new ViewerMapping<>(
                owner,
                Objects.requireNonNull(category, "category"),
                Objects.requireNonNull(type, "type"),
                Objects.requireNonNull(recipeClass, "recipeClass"),
                Objects.requireNonNull(filter, "filter"),
                Objects.requireNonNull(mapper, "mapper")
            ));
            categories.add(category);
        }

        @Override
        public void synchronize(RecipeSerializer<?>... list) {
            for (RecipeSerializer<?> serializer : list) {
                if (serializer != null) {
                    serializers.add(serializer);
                }
            }
        }
    }
}
