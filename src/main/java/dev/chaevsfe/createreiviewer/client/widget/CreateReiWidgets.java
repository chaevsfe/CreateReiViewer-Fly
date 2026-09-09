package dev.chaevsfe.createreiviewer.client.widget;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
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

    public static Widget pictureInPicture(Function<Matrix3x2f, PictureInPictureRenderState> factory) {
        return Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) ->
            graphics.guiRenderState.addPicturesInPictureState(factory.apply(new Matrix3x2f(graphics.pose()))));
    }

    public static Slot slot(int x, int y, EntryIngredient entries) {
        return Widgets.createSlot(new Point(x, y)).entries(entries).disableBackground();
    }

    public static Slot inputSlot(int x, int y, EntryIngredient entries) {
        return slot(x, y, entries).markInput();
    }

    public static Slot outputSlot(int x, int y, EntryIngredient entries, float chance) {
        return slot(x, y, withChanceTooltip(entries, chance)).markOutput();
    }

    public static AllGuiTextures slotBackground(float chance) {
        return chance < 1.0f ? AllGuiTextures.JEI_CHANCE_SLOT : AllGuiTextures.JEI_SLOT;
    }

    public static EntryIngredient withChanceTooltip(EntryIngredient entries, float chance) {
        if (chance >= 1.0f) {
            return entries;
        }
        Component line = Component.translatable("create.recipe.processing.chance", chanceArgument(chance))
            .withStyle(ChatFormatting.GOLD);
        List<EntryStack<?>> decorated = new ArrayList<>(entries.size());
        for (EntryStack<?> stack : entries) {
            decorated.add(stack.copy().tooltip(line));
        }
        return EntryIngredient.of(decorated);
    }

    private static Object chanceArgument(float chance) {
        int percent = (int) (chance * 100.0f);
        return percent == 0 ? "<1" : Integer.valueOf(percent);
    }

    public static int inputX(int index, int count) {
        int xOffset = count < 3 ? 12 + (3 - count) * 19 / 2 : 12;
        return xOffset + (index % 3) * 19;
    }

    public static int inputY(int index, int count) {
        int yOffset = count <= 9 ? 51 : 60;
        return yOffset - (index / 3) * 19;
    }

    public static int outputX(int index, int lastIndex, boolean centreLast) {
        if (centreLast && index == lastIndex) {
            return 142;
        }
        return index % 2 == 0 ? 132 : 151;
    }

    public static int outputY(int index, int count) {
        int yBase = count <= 4 ? 51 : 60;
        return -19 * (index / 2) + yBase;
    }
}
