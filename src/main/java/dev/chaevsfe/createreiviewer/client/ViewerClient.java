package dev.chaevsfe.createreiviewer.client;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;
import dev.chaevsfe.createreiviewer.registry.ViewerPlugins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class ViewerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerPipRenderers();
        DarkGui.register();
        if (CreateReiViewer.sequencedAssemblySyncFixed()) {
            SerializerIdsClient.register();
        }
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ViewerPlugins.load());
        if (CreateReiViewer.reiSupported()) {
            new CreateReiViewerClient().onInitializeClient();
        }
    }

    private static void registerPipRenderers() {
        try {
            PipRenderers.register();
        } catch (RuntimeException | LinkageError error) {
            CreateReiViewer.LOGGER.warn("Create Fly's picture renderers changed, so machines may not draw on recipe pages", error);
        }
    }
}
