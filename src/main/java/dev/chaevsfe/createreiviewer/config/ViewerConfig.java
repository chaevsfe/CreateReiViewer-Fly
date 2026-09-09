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

    private static boolean loaded;
    private static boolean fixSequencedAssemblySync;

    private ViewerConfig() {
    }

    public static synchronized boolean fixSequencedAssemblySync() {
        if (!loaded) {
            load();
        }
        return fixSequencedAssemblySync;
    }

    public static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static void load() {
        loaded = true;
        fixSequencedAssemblySync = false;
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
            JsonElement value = object.get(FIX_SEQUENCED_ASSEMBLY_SYNC);
            if (value == null) {
                write(file);
                return;
            }
            fixSequencedAssemblySync = value.getAsBoolean();
        } catch (IOException | RuntimeException e) {
            CreateReiViewer.LOGGER.warn("Could not read {}, using defaults: {}", file, e.toString());
        }
    }

    private static void write(Path file) {
        JsonObject object = new JsonObject();
        object.addProperty(FIX_SEQUENCED_ASSEMBLY_SYNC, fixSequencedAssemblySync);
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
