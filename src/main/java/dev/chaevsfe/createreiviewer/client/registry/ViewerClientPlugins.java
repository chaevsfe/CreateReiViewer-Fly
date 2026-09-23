package dev.chaevsfe.createreiviewer.client.registry;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.client.CreateViewerClientPlugin;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategory;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategoryRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ViewerClientPlugins {
    public record Entry(String owner, ViewerCategory category) {
    }

    public record Workstations(String owner, Identifier category, List<ItemStack> stacks) {
    }

    private static List<Entry> entries;
    private static List<Workstations> workstations;

    private ViewerClientPlugins() {
    }

    public static synchronized List<Entry> entries() {
        if (entries != null) {
            return entries;
        }
        Map<Identifier, Entry> byId = new LinkedHashMap<>();
        List<Workstations> addedWorkstations = new ArrayList<>();
        List<EntrypointContainer<CreateViewerClientPlugin>> containers;
        try {
            containers = FabricLoader.getInstance().getEntrypointContainers(CreateViewerClientPlugin.ENTRYPOINT, CreateViewerClientPlugin.class);
        } catch (RuntimeException exception) {
            CreateReiViewer.LOGGER.error("Could not load the {} entrypoints; no add-on categories are drawn", CreateViewerClientPlugin.ENTRYPOINT, exception);
            containers = List.of();
        }
        for (EntrypointContainer<CreateViewerClientPlugin> container : containers) {
            String owner = container.getProvider().getMetadata().getName();
            List<ViewerCategory> added = new ArrayList<>();
            List<Workstations> extra = new ArrayList<>();
            try {
                container.getEntrypoint().registerCategories(new ViewerCategoryRegistry() {
                    @Override
                    public void add(ViewerCategory category) {
                        added.add(category);
                    }

                    @Override
                    public void addWorkstations(Identifier category, ItemStack... stacks) {
                        List<ItemStack> copies = new ArrayList<>(stacks.length);
                        for (ItemStack stack : stacks) {
                            if (!stack.isEmpty()) {
                                copies.add(stack.copy());
                            }
                        }
                        if (!copies.isEmpty()) {
                            extra.add(new Workstations(owner, category, List.copyOf(copies)));
                        }
                    }
                });
            } catch (RuntimeException | LinkageError exception) {
                CreateReiViewer.LOGGER.error("{} failed to register its recipe viewer layouts", owner, exception);
                continue;
            }
            addedWorkstations.addAll(extra);
            for (ViewerCategory category : added) {
                Entry previous = byId.putIfAbsent(category.id(), new Entry(owner, category));
                if (previous != null) {
                    CreateReiViewer.LOGGER.warn("{} registered category {} again; keeping the one from {}", owner, category.id(), previous.owner());
                }
            }
        }
        entries = List.copyOf(byId.values());
        workstations = List.copyOf(addedWorkstations);
        CreateReiViewer.LOGGER.info("Loaded {} add-on category layouts and {} extra workstation sets from {} plugins",
            entries.size(), workstations.size(), containers.size());
        return entries;
    }

    public static synchronized List<Workstations> workstations() {
        entries();
        return workstations;
    }
}
