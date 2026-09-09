package dev.chaevsfe.createreiviewer.client;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.List;

public class CreateReiViewerClient implements ClientModInitializer {
    private static final List<CategoryIdentifier<? extends CreateReiDisplay>> CATEGORIES = CreateReiCategories.ALL;
    private static final int POLL_INTERVAL_TICKS = 20;
    private static final int GIVE_UP_TICKS = 600;

    private int ticksSinceJoin = -1;
    private boolean reported;
    private boolean warned;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ticksSinceJoin = 0;
            reported = false;
            warned = false;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ticksSinceJoin = -1);
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
    }

    private void tick() {
        if (ticksSinceJoin < 0 || reported) {
            return;
        }
        ticksSinceJoin++;
        if (ticksSinceJoin % POLL_INTERVAL_TICKS != 0) {
            return;
        }
        int total = 0;
        StringBuilder detail = new StringBuilder();
        for (CategoryIdentifier<? extends CreateReiDisplay> category : CATEGORIES) {
            int count = countDisplays(category);
            total += count;
            if (!detail.isEmpty()) {
                detail.append(", ");
            }
            detail.append(category.getPath()).append('=').append(count);
        }
        if (total > 0) {
            reported = true;
            CreateReiViewer.LOGGER.info("Client received {} synced displays after {} ticks ({})", total, ticksSinceJoin, detail);
            return;
        }
        if (ticksSinceJoin >= GIVE_UP_TICKS && !warned) {
            warned = true;
            CreateReiViewer.LOGGER.warn(
                "Client received ZERO synced displays {} ticks after joining ({}); the server is missing this mod, or the sync failed",
                ticksSinceJoin,
                detail
            );
        }
    }

    private static int countDisplays(CategoryIdentifier<? extends CreateReiDisplay> category) {
        try {
            return DisplayRegistry.getInstance().get(category).size();
        } catch (Exception exception) {
            return 0;
        }
    }
}
