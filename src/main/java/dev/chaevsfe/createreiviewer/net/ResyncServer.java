package dev.chaevsfe.createreiviewer.net;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ResyncServer {
    private static final long MIN_INTERVAL_MS = 30_000L;
    private static final int MAX_BYTES_PER_PACKET = 400_000;

    private static final Map<UUID, Long> LAST_SENT = new ConcurrentHashMap<>();

    private ResyncServer() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ResyncRequestPayload.TYPE, (payload, context) -> answer(payload, context.player()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> LAST_SENT.remove(handler.getPlayer().getUUID()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> LAST_SENT.remove(handler.getPlayer().getUUID()));
    }

    private static void answer(ResyncRequestPayload request, ServerPlayer player) {
        UUID uuid = player.getUUID();
        long now = System.currentTimeMillis();
        Long last = LAST_SENT.get(uuid);
        if (last != null && now - last < MIN_INTERVAL_MS) {
            CreateReiViewer.LOGGER.info(
                "Ignoring a Create display resync request from {}, the last one was {} ms ago",
                player.getPlainTextName(),
                now - last
            );
            return;
        }
        LAST_SENT.put(uuid, now);
        if (!ServerPlayNetworking.canSend(player, ResyncDisplaysPayload.TYPE)) {
            CreateReiViewer.LOGGER.warn("{} asked for a Create display resync but cannot receive one", player.getPlainTextName());
            return;
        }
        List<CreateReiDisplay> displays = collect();
        send(player, displays);
        CreateReiViewer.LOGGER.info(
            "Resent {} Create displays to {}, which held {} of them out of {} displays in its own REI",
            displays.size(),
            player.getPlainTextName(),
            request.createHeld(),
            request.reiHeld()
        );
    }

    private static List<CreateReiDisplay> collect() {
        ServerDisplayRegistry registry;
        try {
            registry = ServerDisplayRegistry.getInstance();
        } catch (Throwable throwable) {
            CreateReiViewer.LOGGER.warn("No server display registry, cannot resync", throwable);
            return List.of();
        }
        Map<CategoryIdentifier<?>, List<Display>> all;
        try {
            all = registry.getAll();
        } catch (Throwable throwable) {
            CreateReiViewer.LOGGER.warn("Could not read the server display registry, cannot resync", throwable);
            return List.of();
        }
        List<CategoryIdentifier<?>> categories = new ArrayList<>(all.keySet());
        categories.sort(Comparator.comparing(category -> category.getIdentifier().toString()));
        List<CreateReiDisplay> ours = new ArrayList<>();
        for (CategoryIdentifier<?> category : categories) {
            List<Display> displays = all.get(category);
            if (displays == null) {
                continue;
            }
            for (Display display : List.copyOf(displays)) {
                if (display instanceof CreateReiDisplay ours0) {
                    ours.add(ours0);
                }
            }
        }
        return ours;
    }

    private static void send(ServerPlayer player, List<CreateReiDisplay> displays) {
        RegistryAccess access = BasicDisplay.registryAccess();
        int total = displays.size();
        List<ResyncDisplaysPayload.Entry> batch = new ArrayList<>();
        int bytes = 0;
        int failed = 0;
        for (CreateReiDisplay display : displays) {
            int kind = ResyncDisplays.kindOf(display);
            byte[] data;
            try {
                data = ResyncDisplays.encode(display, kind, access);
            } catch (Throwable throwable) {
                failed++;
                CreateReiViewer.LOGGER.warn("Could not encode display {} for a resync", display.getDisplayLocation().orElse(null), throwable);
                continue;
            }
            if (!batch.isEmpty() && bytes + data.length > MAX_BYTES_PER_PACKET) {
                ServerPlayNetworking.send(player, new ResyncDisplaysPayload(total, false, List.copyOf(batch)));
                batch = new ArrayList<>();
                bytes = 0;
            }
            batch.add(new ResyncDisplaysPayload.Entry(kind, data));
            bytes += data.length + 8;
        }
        ServerPlayNetworking.send(player, new ResyncDisplaysPayload(total, true, List.copyOf(batch)));
        if (failed > 0) {
            CreateReiViewer.LOGGER.warn("{} of {} Create displays could not be encoded for the resync", failed, total);
        }
    }
}
