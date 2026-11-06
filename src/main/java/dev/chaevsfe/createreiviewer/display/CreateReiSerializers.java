package dev.chaevsfe.createreiviewer.display;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.resources.Identifier;

public final class CreateReiSerializers {
    public static final int COUNT = 3;

    private CreateReiSerializers() {
    }

    public static int register(String site) {
        DisplaySerializerRegistry registry;
        try {
            registry = DisplaySerializerRegistry.getInstance();
        } catch (Throwable throwable) {
            CreateReiViewer.LOGGER.warn("Display serializer registry unavailable at {}", site, throwable);
            return 0;
        }
        return register(site, registry);
    }

    public static int register(String site, DisplaySerializerRegistry registry) {
        int added = 0;
        added += put(registry, CreateReiDisplay.serializerId(), CreateReiDisplay.SERIALIZER);
        added += put(registry, CreateReiGridDisplay.serializerId(), CreateReiGridDisplay.SERIALIZER);
        added += put(registry, CreateReiSequenceDisplay.serializerId(), CreateReiSequenceDisplay.SERIALIZER);
        CreateReiViewer.LOGGER.info("Registered {} display serializers at {}, {} newly added", COUNT, site, added);
        registerFlyEarly(registry);
        return added;
    }

    private static void registerFlyEarly(DisplaySerializerRegistry registry) {
        for (EntrypointContainer<REICommonPlugin> container : FabricLoader.getInstance().getEntrypointContainers("rei_common", REICommonPlugin.class)) {
            if (!"create".equals(container.getProvider().getMetadata().getId())) {
                continue;
            }
            try {
                container.getEntrypoint().registerDisplaySerializer(registry);
            } catch (RuntimeException | LinkageError error) {
                CreateReiViewer.LOGGER.warn("Could not register Create Fly's display serializers early, so its pages may miss rows on a first join", error);
            }
        }
    }

    private static <D extends Display> int put(DisplaySerializerRegistry registry, Identifier id, DisplaySerializer<D> serializer) {
        if (registry.get(id) == serializer) {
            return 0;
        }
        registry.register(id, serializer);
        return 1;
    }
}
