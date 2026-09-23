package dev.chaevsfe.createreiviewer.client.widget;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.BasinBlazeBurnerRenderState;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplays;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class CreateReiLayout {
    private CreateReiLayout() {
    }

    public static HeatCondition heatOf(CreateReiDisplay display) {
        return HeatCondition.values()[display.heat()];
    }

    public static int gridOffsetX(int count) {
        return count < 3 ? (3 - count) * 19 / 2 : 0;
    }

    public static void inputGrid(Panel panel, List<EntryIngredient> inputs, int x, int y) {
        int count = inputs.size();
        int offset = gridOffsetX(count);
        for (int i = 0; i < count; i++) {
            panel.slot(x + offset + (i % 3) * 19, y - (i / 3) * 19, inputs.get(i));
        }
    }

    public static int outputX(int index, int count, int centreX) {
        return centreX - (count % 2 != 0 && index == count - 1 ? 0 : index % 2 == 0 ? 10 : -9);
    }

    public static int outputY(int index, int y) {
        return y - 19 * (index / 2);
    }

    public static void outputGrid(Panel panel, CreateReiDisplay display, int centreX, int y) {
        List<EntryIngredient> outputs = display.outputs();
        int count = outputs.size();
        for (int i = 0; i < count; i++) {
            panel.output(outputX(i, count, centreX), outputY(i, y), outputs.get(i), display.chance(i));
        }
    }

    public static void heatSlots(Panel panel, CreateReiDisplay display, int x, int y) {
        heatSlots(panel, display, x, y, 0);
    }

    public static void heatSlots(Panel panel, CreateReiDisplay display, int x, int y, int fromIndex) {
        List<EntryIngredient> catalysts = display.catalysts();
        int drawn = 0;
        for (int i = fromIndex; i < catalysts.size() && drawn < 2; i++, drawn++) {
            panel.bareSlot(drawn == 0 ? x : x + 19, y, catalysts.get(i));
        }
    }

    public static EntryIngredient catalyst(CreateReiDisplay display, int index) {
        List<EntryIngredient> catalysts = display.catalysts();
        return index >= 0 && index < catalysts.size() ? catalysts.get(index) : EntryIngredient.empty();
    }

    public static void heatBar(Panel panel, HeatCondition heat, int x, int y) {
        panel.texture(heat == HeatCondition.NONE ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR, x, y);
        panel.text(CreateReiWidgets.heatLabel(heat), x + 5, y + 6, heat.getColor(), true);
    }

    public static void blazeBurner(Panel panel, HeatCondition heat, int x, int y) {
        if (heat == HeatCondition.NONE) {
            return;
        }
        panel.pip(x, y, (pose, px, py) -> new BasinBlazeBurnerRenderState(pose, px, py, heat.visualizeAsBlazeBurner()));
    }

    public static void shadow(Panel panel, HeatCondition heat, int x, int noHeatY, int heatY) {
        if (heat == HeatCondition.NONE) {
            panel.texture(AllGuiTextures.JEI_SHADOW, x, noHeatY);
        } else {
            panel.texture(AllGuiTextures.JEI_LIGHT, x, heatY);
        }
    }

    public static EntryIngredient heldItem(CreateReiDisplay display) {
        EntryIngredient held = display.inputs().get(0);
        if ((display.flags() & CreateReiDisplays.KEEP_HELD_ITEM) == 0) {
            return held;
        }
        return CreateReiWidgets.keepHeld(held);
    }

    public static BlockState blockOf(EntryIngredient entries) {
        for (EntryStack<?> stack : entries) {
            if (stack.getValue() instanceof ItemStack itemStack && itemStack.getItem() instanceof BlockItem blockItem) {
                return blockItem.getBlock().defaultBlockState();
            }
        }
        return null;
    }
}
