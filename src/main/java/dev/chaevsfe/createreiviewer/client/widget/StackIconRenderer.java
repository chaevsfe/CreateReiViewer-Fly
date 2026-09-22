package dev.chaevsfe.createreiviewer.client.widget;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class StackIconRenderer implements Renderer {
    private final List<ItemStack> icon;

    public StackIconRenderer(List<ItemStack> icon) {
        this.icon = List.copyOf(icon);
    }

    @Override
    public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        if (icon.size() < 2) {
            graphics.item(icon.getFirst(), bounds.x + 1, bounds.y + 1);
            return;
        }
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(bounds.x, bounds.y);
        graphics.item(icon.get(0), 1, 1);
        pose.translate(9.0f, 9.0f);
        pose.scale(0.5f, 0.5f);
        graphics.item(icon.get(1), 2, 2);
        pose.popMatrix();
    }
}
