package dev.chaevsfe.createreiviewer.client.jei;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.gui.render.ManualBlockRenderState;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.client.ViewerCanvas;
import dev.chaevsfe.createreiviewer.client.widget.PipFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.util.function.Supplier;

final class JeiDrawCanvas implements ViewerCanvas {
    private final GuiGraphicsExtractor graphics;
    private final int offsetY;

    JeiDrawCanvas(GuiGraphicsExtractor graphics, int offsetY) {
        this.graphics = graphics;
        this.offsetY = offsetY;
    }

    @Override
    public void texture(AllGuiTextures texture, int x, int y) {
        texture.render(graphics, x, y + offsetY);
    }

    @Override
    public void icon(AllIcons icon, int x, int y) {
        icon.render(graphics, x, y + offsetY);
    }

    @Override
    public void slot(int x, int y, ViewerIngredient ingredient, Component... tooltip) {
    }

    @Override
    public void bareSlot(int x, int y, ViewerIngredient ingredient, Component... tooltip) {
    }

    @Override
    public void output(int x, int y, ViewerIngredient ingredient, float chance) {
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean shadow) {
        graphics.text(Minecraft.getInstance().font, text, x, y + offsetY, color, shadow);
    }

    @Override
    public void tooltip(int x, int y, int width, int height, Component text) {
    }

    @Override
    public void pip(int x, int y, PipFactory factory) {
        graphics.guiRenderState.addPicturesInPictureState(factory.create(new Matrix3x2f(graphics.pose()), x, y + offsetY));
    }

    @Override
    public void blockPip(int x, int y, BlockState state) {
        graphics.guiRenderState.addPicturesInPictureState(new ManualBlockRenderState(new Matrix3x2f(graphics.pose()), state, x, y + offsetY));
    }

    @Override
    public void itemPip(int x, int y, int size, Supplier<ItemStack> stack) {
        float scale = size / 16.0f;
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y + offsetY);
        pose.scale(scale, scale);
        graphics.item(stack.get(), 0, 0);
        pose.popMatrix();
    }

    @Override
    public void pipScaled(int x, int y, float scale, PipFactory factory) {
        int py = y + offsetY;
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, py);
        pose.scale(scale, scale);
        pose.translate(-x, -py);
        graphics.guiRenderState.addPicturesInPictureState(factory.create(new Matrix3x2f(pose), x, py));
        pose.popMatrix();
    }
}
