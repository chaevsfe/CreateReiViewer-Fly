package dev.chaevsfe.createreiviewer.client;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.config.ViewerConfig;
import dev.chaevsfe.createreiviewer.registry.ViewerPlugins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class ViewerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PipRenderers.register();
        if (ViewerConfig.fixSequencedAssemblySync()) {
            SerializerIdsClient.register();
        }
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ViewerPlugins.load());
        if (CreateReiViewer.reiLoaded()) {
            new CreateReiViewerClient().onInitializeClient();
        }
    }
}
