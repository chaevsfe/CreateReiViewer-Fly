package dev.chaevsfe.createreiviewer.client.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.util.List;

public final class IconDrawable implements IDrawable {
    private final List<ItemStack> icon;

    public IconDrawable(List<ItemStack> icon) {
        this.icon = List.copyOf(icon);
    }

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int x, int y) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        graphics.item(icon.getFirst(), 1, 1);
        if (icon.size() > 1) {
            pose.translate(9.0f, 9.0f);
            pose.scale(0.5f, 0.5f);
            graphics.item(icon.get(1), 2, 2);
        }
        pose.popMatrix();
    }
}
