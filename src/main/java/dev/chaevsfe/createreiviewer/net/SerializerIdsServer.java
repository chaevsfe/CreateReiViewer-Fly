package dev.chaevsfe.createreiviewer.net;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;

public final class SerializerIdsServer {
    private SerializerIdsServer() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundConfiguration().register(SerializerIdsPayload.TYPE, SerializerIdsPayload.STREAM_CODEC);
        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, SerializerIdsPayload.TYPE)) {
                ServerConfigurationNetworking.send(handler, SerializerIdsPayload.of(server.registryAccess()));
            }
        });
    }
}
