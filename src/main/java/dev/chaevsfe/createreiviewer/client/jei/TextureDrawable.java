package dev.chaevsfe.createreiviewer.client.jei;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.EnumMap;
import java.util.Map;

public final class TextureDrawable implements IDrawable {
    private static final Map<AllGuiTextures, TextureDrawable> CACHE = new EnumMap<>(AllGuiTextures.class);

    private final AllGuiTextures texture;

    private TextureDrawable(AllGuiTextures texture) {
        this.texture = texture;
    }

    public static synchronized TextureDrawable of(AllGuiTextures texture) {
        return CACHE.computeIfAbsent(texture, TextureDrawable::new);
    }

    @Override
    public int getWidth() {
        return texture.getWidth();
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int x, int y) {
        texture.render(graphics, x, y);
    }
}
