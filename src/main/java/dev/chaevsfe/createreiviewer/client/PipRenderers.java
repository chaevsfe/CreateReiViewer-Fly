package dev.chaevsfe.createreiviewer.client;

import com.zurrtum.create.client.foundation.gui.render.BasinBlazeBurnerRenderer;
import com.zurrtum.create.client.foundation.gui.render.SandPaperRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;

public final class PipRenderers {
    private PipRenderers() {
    }

    public static void register() {
        PictureInPictureRendererRegistry.register(context -> new BasinBlazeBurnerRenderer(context.bufferSource()));
        PictureInPictureRendererRegistry.register(context -> new SandPaperRenderer(context.bufferSource()));
    }
}
