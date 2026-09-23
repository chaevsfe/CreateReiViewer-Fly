package dev.chaevsfe.createreiviewer.client;

import dev.chaevsfe.createreiviewer.compat.jei.SequencedAssemblySyncFix;
import dev.chaevsfe.createreiviewer.net.SerializerIdsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;

public final class SerializerIdsClient {
    private SerializerIdsClient() {
    }

    public static void register() {
        ClientConfigurationConnectionEvents.INIT.register((handler, client) -> SequencedAssemblySyncFix.useServerIds(null));
        ClientConfigurationNetworking.registerGlobalReceiver(
            SerializerIdsPayload.TYPE,
            (payload, context) -> SequencedAssemblySyncFix.useServerIds(payload.ids())
        );
    }
}
