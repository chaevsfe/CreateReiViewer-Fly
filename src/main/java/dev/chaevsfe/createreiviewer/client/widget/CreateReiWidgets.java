package dev.chaevsfe.createreiviewer.client.widget;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class CreateReiWidgets {
    private CreateReiWidgets() {
    }

    public static Widget texture(AllGuiTextures texture, int x, int y) {
        return Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> texture.render(graphics, x, y));
    }

    public static Widget icon(AllIcons icon, int x, int y) {
        return Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> icon.render(graphics, x, y));
    }

    public static Widget pictureInPicture(Function<Matrix3x2f, PictureInPictureRenderState> factory) {
        return Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) ->
            graphics.guiRenderState.addPicturesInPictureState(factory.apply(new Matrix3x2f(graphics.pose()))));
    }

    public static Widget pictureInPictureScaled(int x, int y, float scale, PipFactory factory) {
        return Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            org.joml.Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x, y);
            pose.scale(scale, scale);
            pose.translate(-x, -y);
            graphics.guiRenderState.addPicturesInPictureState(factory.create(new Matrix3x2f(pose), x, y));
            pose.popMatrix();
        });
    }

    public static Slot slot(int x, int y, EntryIngredient entries) {
        return Widgets.createSlot(new Point(x, y)).entries(entries).disableBackground();
    }

    public static Slot inputSlot(int x, int y, EntryIngredient entries) {
        return slot(x, y, entries).markInput();
    }

    public static Slot outputSlot(int x, int y, EntryIngredient entries, float chance) {
        return slot(x, y, withTooltip(entries, chanceLine(chance))).markOutput();
    }

    public static Slot junkSlot(int x, int y, EntryIngredient entries, float chance) {
        List<Component> lines = new ArrayList<>(2);
        lines.add(Component.translatable("create.recipe.assembly.junk").withStyle(ChatFormatting.GOLD));
        Component chanceLine = chanceLine(chance);
        if (chanceLine != null) {
            lines.add(chanceLine);
        }
        return slot(x, y, withTooltips(entries, lines)).markOutput();
    }

    public static EntryIngredient keepHeld(EntryIngredient entries) {
        return withTooltips(entries, List.of(
            Component.translatable("create.recipe.deploying.not_consumed").withStyle(ChatFormatting.GOLD)
        ));
    }

    public static AllGuiTextures slotBackground(float chance) {
        return chance < 1.0f ? AllGuiTextures.JEI_CHANCE_SLOT : AllGuiTextures.JEI_SLOT;
    }

    private static Component chanceLine(float chance) {
        if (chance >= 1.0f) {
            return null;
        }
        return Component.translatable("create.recipe.processing.chance", chanceArgument(chance)).withStyle(ChatFormatting.GOLD);
    }

    private static EntryIngredient withTooltip(EntryIngredient entries, Component line) {
        return line == null ? entries : withTooltips(entries, List.of(line));
    }

    private static EntryIngredient withTooltips(EntryIngredient entries, List<Component> lines) {
        if (lines.isEmpty()) {
            return entries;
        }
        List<EntryStack<?>> decorated = new ArrayList<>(entries.size());
        for (EntryStack<?> stack : entries) {
            EntryStack<?> copy = stack.copy();
            for (Component line : lines) {
                copy = copy.tooltip(line);
            }
            decorated.add(copy);
        }
        return EntryIngredient.of(decorated);
    }

    private static Object chanceArgument(float chance) {
        int percent = (int) (chance * 100.0f);
        return percent == 0 ? "<1" : Integer.valueOf(percent);
    }

    public static int basinInputX(int index, int count) {
        int xOffset = count < 3 ? 12 + (3 - count) * 19 / 2 : 12;
        return xOffset + (index % 3) * 19;
    }

    public static int basinInputY(int index, int count) {
        int yOffset = count <= 9 ? 51 : 60;
        return yOffset - (index / 3) * 19;
    }

    public static int basinOutputX(int index, int lastIndex, boolean centreLast) {
        if (centreLast && index == lastIndex) {
            return 142;
        }
        return index % 2 == 0 ? 132 : 151;
    }

    public static int basinOutputY(int index, int yBase) {
        return yBase - 19 * (index / 2);
    }
}
