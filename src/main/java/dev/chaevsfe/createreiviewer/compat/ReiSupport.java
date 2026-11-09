package dev.chaevsfe.createreiviewer.compat;

import java.util.Optional;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

public final class ReiSupport {
    public static final String OLDEST_SUPPORTED = "26.3.823";
    private static final boolean ENABLED = check();

    private ReiSupport() {
    }

    public static boolean enabled() {
        return ENABLED;
    }

    private static boolean check() {
        Optional<ModContainer> rei = FabricLoader.getInstance().getModContainer(CreateReiViewer.REI_MOD_ID);
        if (rei.isEmpty()) {
            return false;
        }
        Version version = rei.get().getMetadata().getVersion();
        boolean supported;
        try {
            supported = VersionPredicate.parse(">=" + OLDEST_SUPPORTED).test(version);
        } catch (VersionParsingException exception) {
            supported = false;
        }
        if (!supported) {
            CreateReiViewer.LOGGER.warn(
                "Roughly Enough Items {} is older than {}, so this mod's REI pages stay off; everything else in this mod still runs",
                version.getFriendlyString(),
                OLDEST_SUPPORTED
            );
        }
        return supported;
    }
}
