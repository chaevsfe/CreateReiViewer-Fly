package dev.chaevsfe.createreiviewer.client.widget;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.joml.Matrix3x2fStack;

public class TwoItemRenderer implements Renderer {
    private final ItemStack icon;
    private final ItemStack subIcon;

    public TwoItemRenderer(ItemLike icon, ItemLike subIcon) {
        this.icon = new ItemStack(icon);
        this.subIcon = new ItemStack(subIcon);
    }

    @Override
    public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(bounds.x, bounds.y);
        graphics.item(icon, 1, 1);
        pose.translate(9.0f, 9.0f);
        pose.scale(0.5f, 0.5f);
        graphics.item(subIcon, 2, 2);
        pose.popMatrix();
    }
}
