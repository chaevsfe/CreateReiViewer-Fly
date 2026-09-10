package dev.chaevsfe.createreiviewer.client.widget;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.gui.render.ManualBlockRenderState;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class Panel {
    private final int ox;
    private final int oy;
    private final List<Widget> widgets = new ArrayList<>();

    public Panel(int ox, int oy) {
        this.ox = ox;
        this.oy = oy;
    }

    public void add(Widget widget) {
        widgets.add(widget);
    }

    public void texture(AllGuiTextures texture, int x, int y) {
        widgets.add(CreateReiWidgets.texture(texture, ox + x, oy + y));
    }

    public void icon(AllIcons icon, int x, int y) {
        widgets.add(CreateReiWidgets.icon(icon, ox + x, oy + y));
    }

    public void slot(int x, int y, EntryIngredient entries) {
        texture(AllGuiTextures.JEI_SLOT, x - 1, y - 1);
        widgets.add(CreateReiWidgets.inputSlot(ox + x, oy + y, entries));
    }

    public void bareSlot(int x, int y, EntryIngredient entries) {
        widgets.add(CreateReiWidgets.inputSlot(ox + x, oy + y, entries));
    }

    public void output(int x, int y, EntryIngredient entries, float chance) {
        texture(CreateReiWidgets.slotBackground(chance), x - 1, y - 1);
        widgets.add(CreateReiWidgets.outputSlot(ox + x, oy + y, entries, chance));
    }

    public void junk(int x, int y, EntryIngredient entries, float chance) {
        texture(AllGuiTextures.JEI_CHANCE_SLOT, x - 1, y - 1);
        widgets.add(CreateReiWidgets.junkSlot(ox + x, oy + y, entries, chance));
    }

    public void text(Component text, int x, int y, int color) {
        int tx = ox + x;
        int ty = oy + y;
        widgets.add(Widgets.createDrawableWidget(
            (graphics, mouseX, mouseY, delta) -> graphics.text(Minecraft.getInstance().font, text, tx, ty, color, false)));
    }

    public void tooltip(int x, int y, int width, int height, Component text) {
        widgets.add(Widgets.createTooltip(new Rectangle(ox + x, oy + y, width, height), text));
    }

    public void pip(int x, int y, PipFactory factory) {
        int px = ox + x;
        int py = oy + y;
        widgets.add(CreateReiWidgets.pictureInPicture(pose -> factory.create(pose, px, py)));
    }

    public void blockPip(int x, int y, BlockState state) {
        int px = ox + x;
        int py = oy + y;
        widgets.add(CreateReiWidgets.pictureInPicture(pose -> new ManualBlockRenderState(pose, state, px, py)));
    }

    public void pipScaled(int x, int y, float scale, PipFactory factory) {
        widgets.add(CreateReiWidgets.pictureInPictureScaled(ox + x, oy + y, scale, factory));
    }

    public List<Widget> widgets() {
        return widgets;
    }
}
