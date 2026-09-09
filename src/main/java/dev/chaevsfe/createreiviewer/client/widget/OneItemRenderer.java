package dev.chaevsfe.createreiviewer.client.widget;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class OneItemRenderer implements Renderer {
    private final ItemStack icon;

    public OneItemRenderer(ItemLike icon) {
        this.icon = new ItemStack(icon);
    }

    @Override
    public void render(GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        graphics.item(icon, bounds.x + 1, bounds.y + 1);
    }
}
