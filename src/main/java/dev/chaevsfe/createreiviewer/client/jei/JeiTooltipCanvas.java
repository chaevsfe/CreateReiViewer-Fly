package dev.chaevsfe.createreiviewer.client.jei;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.client.ViewerCanvas;
import dev.chaevsfe.createreiviewer.client.widget.PipFactory;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

final class JeiTooltipCanvas implements ViewerCanvas {
    private final ITooltipBuilder tooltip;
    private final double mouseX;
    private final double mouseY;
    private final int offsetY;

    JeiTooltipCanvas(ITooltipBuilder tooltip, double mouseX, double mouseY, int offsetY) {
        this.tooltip = tooltip;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.offsetY = offsetY;
    }

    @Override
    public void tooltip(int x, int y, int width, int height, Component text) {
        int top = y + offsetY;
        if (mouseX >= x && mouseX < x + width && mouseY >= top && mouseY < top + height) {
            tooltip.add(text);
        }
    }

    @Override
    public void texture(AllGuiTextures texture, int x, int y) {
    }

    @Override
    public void icon(AllIcons icon, int x, int y) {
    }

    @Override
    public void slot(int x, int y, ViewerIngredient ingredient, Component... lines) {
    }

    @Override
    public void bareSlot(int x, int y, ViewerIngredient ingredient, Component... lines) {
    }

    @Override
    public void output(int x, int y, ViewerIngredient ingredient, float chance) {
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean shadow) {
    }

    @Override
    public void pip(int x, int y, PipFactory factory) {
    }

    @Override
    public void blockPip(int x, int y, BlockState state) {
    }

    @Override
    public void itemPip(int x, int y, int size, Supplier<ItemStack> stack) {
    }

    @Override
    public void pipScaled(int x, int y, float scale, PipFactory factory) {
    }
}
