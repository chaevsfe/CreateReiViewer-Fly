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

public final class FlyPageGate implements DisplayVisibilityPredicate {
    private static final String FLY_ID = "create";

    private static boolean warned;

    private FlyPageGate() {
    }

    public static boolean serverHasOurPages() {
        return ClientPlayNetworking.canSend(ResyncRequestPayload.TYPE);
    }

    public static void register(DisplayRegistry registry) {
        try {
            registry.registerVisibilityPredicate(new FlyPageGate());
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
        for (CategoryIdentifier<?> category : CreateReiCategories.ALL) {
            if (categories.tryGet(CategoryIdentifier.of(FLY_ID, category.getPath())).isEmpty()) {
                missing++;
            }
        }
        if (missing > 0) {
            warn(missing + " of Create Fly's " + CreateReiCategories.ALL.size() + " REI categories are not where this mod expects them");
        }
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
        if (FLY_ID.equals(display.getCategoryIdentifier().getNamespace()) && serverHasOurPages()) {
            return EventResult.interruptFalse();
        }
        return EventResult.pass();
    }
}
