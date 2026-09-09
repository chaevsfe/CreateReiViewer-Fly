package dev.chaevsfe.createreiviewer.net;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ResyncPayloads {
    private static boolean registered;

    private ResyncPayloads() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }
        registered = true;
        PayloadTypeRegistry.serverboundPlay().register(ResyncRequestPayload.TYPE, ResyncRequestPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ResyncDisplaysPayload.TYPE, ResyncDisplaysPayload.STREAM_CODEC);
    }
}
