package dev.chaevsfe.createreiviewer.client;

import dev.architectury.event.EventResult;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.net.ResyncRequestPayload;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.visibility.DisplayVisibilityPredicate;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

public final class FlyPageGate implements DisplayVisibilityPredicate {
    private static final String FLY_ID = "create";
    private static final Map<CategoryIdentifier<?>, CategoryIdentifier<?>> OURS_BY_FLY = oursByFly();

    private static boolean warned;
    private static boolean warnedFallback;

    private final DisplayRegistry registry;

    private FlyPageGate(DisplayRegistry registry) {
        this.registry = registry;
    }

    private static Map<CategoryIdentifier<?>, CategoryIdentifier<?>> oursByFly() {
        Map<CategoryIdentifier<?>, CategoryIdentifier<?>> ours = new HashMap<>();
        for (CategoryIdentifier<?> category : CreateReiCategories.ALL) {
            ours.put(CategoryIdentifier.of(FLY_ID, category.getPath()), category);
        }
        return ours;
    }

    public static boolean serverHasOurPages() {
        return ClientPlayNetworking.canSend(ResyncRequestPayload.TYPE);
    }

    public static void register(DisplayRegistry registry) {
        try {
            registry.registerVisibilityPredicate(new FlyPageGate(registry));
        } catch (RuntimeException | LinkageError error) {
            warn("REI refused the page gate (" + error + ")");
        }
    }

    public static void checkFlyCategories() {
        if (!flyHasReiPlugin()) {
            return;
        }
        int missing = 0;
        CategoryRegistry categories = CategoryRegistry.getInstance();
        for (CategoryIdentifier<?> fly : OURS_BY_FLY.keySet()) {
            if (categories.tryGet(fly).isEmpty()) {
                missing++;
            }
        }
        if (missing > 0) {
            warn(missing + " of Create Fly's " + OURS_BY_FLY.size() + " REI categories are not where this mod expects them");
        }
    }

    public static boolean warnIfFallingBack() {
        if (!serverHasOurPages()) {
            return false;
        }
        DisplayRegistry registry = DisplayRegistry.getInstance();
        int falling = 0;
        for (Map.Entry<CategoryIdentifier<?>, CategoryIdentifier<?>> entry : OURS_BY_FLY.entrySet()) {
            if (registry.get(entry.getValue()).isEmpty() && !registry.get(entry.getKey()).isEmpty()) {
                falling++;
            }
        }
        if (falling == 0) {
            return false;
        }
        if (!warnedFallback) {
            warnedFallback = true;
            CreateReiViewer.LOGGER.warn("{} of Create's {} REI categories have none of this mod's pages, so REI shows Create Fly's own there", falling, OURS_BY_FLY.size());
        }
        return true;
    }

    private static boolean flyHasReiPlugin() {
        return FabricLoader.getInstance().getEntrypointContainers("rei_client", REIClientPlugin.class).stream()
            .anyMatch(container -> FLY_ID.equals(container.getProvider().getMetadata().getId()));
    }

    private static void warn(String reason) {
        if (!warned) {
            warned = true;
            CreateReiViewer.LOGGER.warn("{}, so Create's REI tabs may appear twice", reason);
        }
    }

    @Override
    public EventResult handleDisplay(DisplayCategory<?> category, Display display) {
        CategoryIdentifier<?> ours = OURS_BY_FLY.get(display.getCategoryIdentifier());
        if (ours != null && serverHasOurPages() && !registry.get(ours).isEmpty()) {
            return EventResult.interruptFalse();
        }
        return EventResult.pass();
    }
}
