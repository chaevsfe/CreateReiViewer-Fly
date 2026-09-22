package dev.chaevsfe.createreiviewer.api.client;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.client.widget.PipFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public interface ViewerCanvas {
    void texture(AllGuiTextures texture, int x, int y);

    void icon(AllIcons icon, int x, int y);

    void slot(int x, int y, ViewerIngredient ingredient, Component... tooltip);

    void bareSlot(int x, int y, ViewerIngredient ingredient, Component... tooltip);

    void output(int x, int y, ViewerIngredient ingredient, float chance);

    default void output(int x, int y, ViewerIngredient ingredient) {
        output(x, y, ingredient, 1.0f);
    }

    void text(Component text, int x, int y, int color, boolean shadow);

    default void text(Component text, int x, int y, int color) {
        text(text, x, y, color, false);
    }

    void tooltip(int x, int y, int width, int height, Component text);

    void pip(int x, int y, PipFactory factory);

    void blockPip(int x, int y, BlockState state);

    void itemPip(int x, int y, int size, Supplier<ItemStack> stack);

    void pipScaled(int x, int y, float scale, PipFactory factory);
}
