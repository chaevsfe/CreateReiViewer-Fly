package dev.chaevsfe.createreiviewer.client.widget;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.client.ViewerCanvas;
import dev.chaevsfe.createreiviewer.display.ViewerReiDisplays;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ReiCanvas implements ViewerCanvas {
    private final Panel panel;
    private final Map<ViewerIngredient, EntryIngredient> originals;

    public ReiCanvas(Panel panel, Map<ViewerIngredient, EntryIngredient> originals) {
        this.panel = panel;
        this.originals = originals;
    }

    private EntryIngredient entries(ViewerIngredient ingredient, Component... tooltip) {
        EntryIngredient original = originals.get(ingredient);
        EntryIngredient entries = original != null ? original : ViewerReiDisplays.entries(ingredient);
        return tooltip.length == 0 ? entries : CreateReiWidgets.withTooltips(entries, List.of(tooltip));
    }

    @Override
    public void texture(AllGuiTextures texture, int x, int y) {
        panel.texture(texture, x, y);
    }

    @Override
    public void icon(AllIcons icon, int x, int y) {
        panel.icon(icon, x, y);
    }

    @Override
    public void slot(int x, int y, ViewerIngredient ingredient, Component... tooltip) {
        panel.slot(x, y, entries(ingredient, tooltip));
    }

    @Override
    public void bareSlot(int x, int y, ViewerIngredient ingredient, Component... tooltip) {
        panel.bareSlot(x, y, entries(ingredient, tooltip));
    }

    @Override
    public void output(int x, int y, ViewerIngredient ingredient, float chance) {
        panel.output(x, y, entries(ingredient), chance);
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean shadow) {
        panel.text(text, x, y, color, shadow);
    }

    @Override
    public void tooltip(int x, int y, int width, int height, Component text) {
        panel.tooltip(x, y, width, height, text);
    }

    @Override
    public void pip(int x, int y, PipFactory factory) {
        panel.pip(x, y, factory);
    }

    @Override
    public void blockPip(int x, int y, BlockState state) {
        panel.blockPip(x, y, state);
    }

    @Override
    public void itemPip(int x, int y, int size, Supplier<ItemStack> stack) {
        panel.itemPip(x, y, size, stack);
    }

    @Override
    public void pipScaled(int x, int y, float scale, PipFactory factory) {
        panel.pipScaled(x, y, scale, factory);
    }
}
