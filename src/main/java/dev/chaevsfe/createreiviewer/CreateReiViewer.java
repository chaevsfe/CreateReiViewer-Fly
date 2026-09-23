package dev.chaevsfe.createreiviewer;

import dev.chaevsfe.createreiviewer.compat.jei.SequencedAssemblySyncFix;
import dev.chaevsfe.createreiviewer.config.ViewerConfig;
import dev.chaevsfe.createreiviewer.net.ResyncPayloads;
import dev.chaevsfe.createreiviewer.net.ResyncServer;
import dev.chaevsfe.createreiviewer.net.SerializerIdsServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateReiViewer implements ModInitializer {
    public static final String MOD_ID = "createreiviewer";
    public static final Logger LOGGER = LoggerFactory.getLogger("Create Fly Recipe Viewer");
    public static final String REI_MOD_ID = "roughlyenoughitems";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Create Fly Recipe Viewer loaded");
        if (reiLoaded()) {
            ResyncPayloads.register();
            ResyncServer.register();
        }
        SerializerIdsServer.register();
        boolean syncFix = ViewerConfig.fixSequencedAssemblySync();
        LOGGER.info(
            "{} is {} in {}{}: {}",
            ViewerConfig.FIX_SEQUENCED_ASSEMBLY_SYNC,
            syncFix,
            ViewerConfig.path(),
            ViewerConfig.raisedOldSyncDefault() ? " (raised from the old default false)" : "",
            syncFix
                ? "Create sequenced assembly steps are decoded with the server's recipe serializer ids whenever the server sends them; recipes stay in Create Fly's wire format, so the server and the clients need not use the same value"
                : "Create sequenced assembly steps are decoded with this side's own recipe serializer ids, as in Create Fly"
        );
        if (syncFix) {
            SequencedAssemblySyncFix.apply();
        } else if (recipeViewerNeedingTheFix() != null) {
            LOGGER.warn(
                "{} is installed and {} is false in {}: this client disconnects while decoding Create sequenced assembly recipes from a server that numbers its recipe serializers differently. Set {} to true in that file to decode them with the server's ids.",
                recipeViewerNeedingTheFix(),
                ViewerConfig.FIX_SEQUENCED_ASSEMBLY_SYNC,
                ViewerConfig.path(),
                ViewerConfig.FIX_SEQUENCED_ASSEMBLY_SYNC
            );
        }
    }

    public static boolean reiLoaded() {
        return FabricLoader.getInstance().isModLoaded(REI_MOD_ID);
    }

    private static String recipeViewerNeedingTheFix() {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("jei")) {
            return "Just Enough Items";
        }
        if (loader.isModLoaded("rrv")) {
            return "Reliable Recipe Viewer";
        }
        return null;
    }
}
