package dev.chaevsfe.createreiviewer.client.registry;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.client.CreateViewerClientPlugin;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ViewerClientPlugins {
    public record Entry(String owner, ViewerCategory category) {
    }

    private static List<Entry> entries;

    private ViewerClientPlugins() {
    }

    public static synchronized List<Entry> entries() {
        if (entries != null) {
            return entries;
        }
        Map<Identifier, Entry> byId = new LinkedHashMap<>();
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
            try {
                container.getEntrypoint().registerCategories(added::add);
            } catch (RuntimeException | LinkageError exception) {
                CreateReiViewer.LOGGER.error("{} failed to register its recipe viewer layouts", owner, exception);
                continue;
            }
            for (ViewerCategory category : added) {
                Entry previous = byId.putIfAbsent(category.id(), new Entry(owner, category));
                if (previous != null) {
                    CreateReiViewer.LOGGER.warn("{} registered category {} again; keeping the one from {}", owner, category.id(), previous.owner());
                }
            }
        }
        entries = List.copyOf(byId.values());
        CreateReiViewer.LOGGER.info("Loaded {} add-on category layouts from {} plugins", entries.size(), containers.size());
        return entries;
    }
}
