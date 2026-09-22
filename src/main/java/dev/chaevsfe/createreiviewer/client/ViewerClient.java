package dev.chaevsfe.createreiviewer.client;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import net.fabricmc.api.ClientModInitializer;

public class ViewerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (CreateReiViewer.reiLoaded()) {
            new CreateReiViewerClient().onInitializeClient();
        }
    }
}
