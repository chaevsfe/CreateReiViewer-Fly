package dev.chaevsfe.createreiviewer.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import net.fabricmc.loader.api.FabricLoader;

public final class ViewerConfig {
    public static final String FILE_NAME = CreateReiViewer.MOD_ID + ".json";
    public static final String FIX_SEQUENCED_ASSEMBLY_SYNC = "fixSequencedAssemblySync";
    public static final String RESYNC_AFTER_RELOAD = "resyncAfterReload";
    public static final String FIX_JEI_VANILLA_RECIPE_TAGS = "fixJeiVanillaRecipeTags";

    private static boolean loaded;
    private static boolean fixSequencedAssemblySync;
    private static boolean resyncAfterReload = true;
    private static boolean fixJeiVanillaRecipeTags = true;

    private ViewerConfig() {
    }

    public static synchronized boolean fixSequencedAssemblySync() {
        if (!loaded) {
            load();
        }
        return fixSequencedAssemblySync;
    }

    public static synchronized boolean resyncAfterReload() {
        if (!loaded) {
            load();
        }
        return resyncAfterReload;
    }

    public static synchronized boolean fixJeiVanillaRecipeTags() {
        if (!loaded) {
            load();
        }
        return fixJeiVanillaRecipeTags;
    }

    public static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static void load() {
        loaded = true;
        fixSequencedAssemblySync = false;
        resyncAfterReload = true;
        fixJeiVanillaRecipeTags = true;
        Path file = path();
        if (!Files.isRegularFile(file)) {
            write(file);
            return;
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) {
                CreateReiViewer.LOGGER.warn("{} is not a JSON object, using defaults", file);
                return;
            }
            JsonObject object = root.getAsJsonObject();
            boolean complete = true;
            JsonElement fix = object.get(FIX_SEQUENCED_ASSEMBLY_SYNC);
            if (fix == null) {
                complete = false;
            } else {
                fixSequencedAssemblySync = fix.getAsBoolean();
            }
            JsonElement resync = object.get(RESYNC_AFTER_RELOAD);
            if (resync == null) {
                complete = false;
            } else {
                resyncAfterReload = resync.getAsBoolean();
            }
            JsonElement jeiTags = object.get(FIX_JEI_VANILLA_RECIPE_TAGS);
            if (jeiTags == null) {
                complete = false;
            } else {
                fixJeiVanillaRecipeTags = jeiTags.getAsBoolean();
            }
            if (!complete) {
                write(file);
            }
        } catch (IOException | RuntimeException e) {
            CreateReiViewer.LOGGER.warn("Could not read {}, using defaults: {}", file, e.toString());
        }
    }

    private static void write(Path file) {
        JsonObject object = new JsonObject();
        object.addProperty(FIX_SEQUENCED_ASSEMBLY_SYNC, fixSequencedAssemblySync);
        object.addProperty(RESYNC_AFTER_RELOAD, resyncAfterReload);
        object.addProperty(FIX_JEI_VANILLA_RECIPE_TAGS, fixJeiVanillaRecipeTags);
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                writer.write(object.toString());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            CreateReiViewer.LOGGER.warn("Could not write {}: {}", file, e.toString());
        }
    }
}
