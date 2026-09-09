package dev.chaevsfe.createreiviewer.client;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.config.ViewerConfig;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.net.ResyncDisplays;
import dev.chaevsfe.createreiviewer.net.ResyncDisplaysPayload;
import dev.chaevsfe.createreiviewer.net.ResyncRequestPayload;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.RegistryAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResyncClient {
    private static final int SETTLE_TICKS = 60;
    private static final int FALLBACK_TICKS = 600;

    private static volatile boolean reloadEnded;
    private static boolean joined;
    private static boolean requested;
    private static int ticksSinceJoin;
    private static int settleTicks;

    private static final List<ResyncDisplaysPayload.Entry> PENDING = new ArrayList<>();
    private static int serverTotal;

    private ResyncClient() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ResyncDisplaysPayload.TYPE, (payload, context) -> receive(payload, BasicDisplay.registryAccess()));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            joined = true;
            requested = false;
            reloadEnded = false;
            ticksSinceJoin = 0;
            settleTicks = 0;
            PENDING.clear();
            serverTotal = 0;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            joined = false;
            reloadEnded = false;
            PENDING.clear();
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
    }

    public static void onReloadEnded() {
        reloadEnded = true;
    }

    private static void tick() {
        if (!joined || requested || !ViewerConfig.resyncAfterReload()) {
            return;
        }
        ticksSinceJoin++;
        if (reloading()) {
            settleTicks = 0;
            return;
        }
        if (reloadEnded) {
            settleTicks++;
            if (settleTicks >= SETTLE_TICKS) {
                request("REI finished reloading " + settleTicks + " ticks ago");
            }
            return;
        }
        if (ticksSinceJoin >= FALLBACK_TICKS) {
            request("no REI reload was seen in the " + ticksSinceJoin + " ticks since joining");
        }
    }

    private static boolean reloading() {
        try {
            return PluginManager.areAnyReloading();
        } catch (Throwable throwable) {
            return false;
        }
    }

    private static void request(String reason) {
        requested = true;
        if (!ClientPlayNetworking.canSend(ResyncRequestPayload.TYPE)) {
            CreateReiViewer.LOGGER.info("The server does not answer Create display resync requests, keeping whatever REI synced");
            return;
        }
        int held = countOurs();
        int rei = reiSize();
        CreateReiViewer.LOGGER.info("Asking the server to resync Create displays because {}; holding {} of {} REI displays", reason, held, rei);
        ClientPlayNetworking.send(new ResyncRequestPayload(held, rei));
    }

    private static void receive(ResyncDisplaysPayload payload, RegistryAccess access) {
        serverTotal = payload.serverTotal();
        PENDING.addAll(payload.entries());
        if (!payload.last()) {
            return;
        }
        List<ResyncDisplaysPayload.Entry> entries = List.copyOf(PENDING);
        PENDING.clear();
        apply(entries, access);
    }

    private static void apply(List<ResyncDisplaysPayload.Entry> entries, RegistryAccess access) {
        DisplayRegistry registry;
        try {
            registry = DisplayRegistry.getInstance();
        } catch (Throwable throwable) {
            CreateReiViewer.LOGGER.warn("No display registry, cannot apply the resync", throwable);
            return;
        }
        Map<String, Integer> present = new HashMap<>();
        for (CreateReiDisplay display : ours(registry)) {
            present.merge(ResyncDisplays.key(display), 1, Integer::sum);
        }
        Map<String, Integer> seen = new HashMap<>();
        int added = 0;
        int already = 0;
        int undecodable = 0;
        int rejected = 0;
        for (ResyncDisplaysPayload.Entry entry : entries) {
            CreateReiDisplay display = ResyncDisplays.decode(entry.kind(), entry.data(), access);
            if (display == null) {
                undecodable++;
                continue;
            }
            String key = ResyncDisplays.key(display);
            int used = seen.merge(key, 1, Integer::sum) - 1;
            if (used < present.getOrDefault(key, 0)) {
                already++;
                continue;
            }
            if (registry.add(display, null)) {
                added++;
            } else {
                rejected++;
            }
        }
        if (rejected > 0) {
            CreateReiViewer.LOGGER.warn("REI refused {} resynced Create displays; their categories may not be registered on this client", rejected);
        }
        if (undecodable > 0) {
            CreateReiViewer.LOGGER.warn("{} resynced Create displays could not be decoded", undecodable);
        }
        CreateReiViewer.LOGGER.info(
            "Resynced {} Create displays out of {} REI holds ({} added, {} already present, {} undecodable; REI now holds {} displays in total)",
            countOurs(),
            serverTotal,
            added,
            already,
            undecodable,
            reiSize()
        );
    }

    private static List<CreateReiDisplay> ours(DisplayRegistry registry) {
        List<CreateReiDisplay> ours = new ArrayList<>();
        Map<CategoryIdentifier<?>, List<Display>> all;
        try {
            all = registry.getAll();
        } catch (Throwable throwable) {
            return ours;
        }
        for (List<Display> displays : all.values()) {
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

    private static int countOurs() {
        try {
            return ours(DisplayRegistry.getInstance()).size();
        } catch (Throwable throwable) {
            return 0;
        }
    }

    private static int reiSize() {
        try {
            return DisplayRegistry.getInstance().size();
        } catch (Throwable throwable) {
            return 0;
        }
    }
}
