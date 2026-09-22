package dev.chaevsfe.createreiviewer.api.client;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.BasinBlazeBurnerRenderState;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.ViewerRecipe;
import dev.chaevsfe.createreiviewer.api.ViewerStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class ViewerLayouts {
    public static final int BASIN_HEIGHT = 103;
    public static final int BASIN_OVERHANG_TOP = 5;

    private ViewerLayouts() {
    }

    public static Component heatLabel(HeatCondition heat) {
        return CreateLang.translateDirect(heat.getTranslationKey());
    }

    public static Component keepHeldLine() {
        return Component.translatable("create.recipe.deploying.not_consumed").withStyle(ChatFormatting.GOLD);
    }

    public static Component chanceLine(float chance) {
        int percent = (int) (chance * 100.0f);
        Object argument = percent == 0 ? "<1" : Integer.valueOf(percent);
        return Component.translatable("create.recipe.processing.chance", argument).withStyle(ChatFormatting.GOLD);
    }

    public static AllGuiTextures slotBackground(float chance) {
        return chance < 1.0f ? AllGuiTextures.JEI_CHANCE_SLOT : AllGuiTextures.JEI_SLOT;
    }

    public static int gridOffsetX(int count) {
        return count < 3 ? (3 - count) * 19 / 2 : 0;
    }

    public static void inputGrid(ViewerCanvas canvas, List<ViewerIngredient> inputs, int x, int y) {
        int count = inputs.size();
        int offset = gridOffsetX(count);
        for (int i = 0; i < count; i++) {
            canvas.slot(x + offset + (i % 3) * 19, y - (i / 3) * 19, inputs.get(i));
        }
    }

    public static int outputX(int index, int count, int centreX) {
        return centreX - (count % 2 != 0 && index == count - 1 ? 0 : index % 2 == 0 ? 10 : -9);
    }

    public static int outputY(int index, int y) {
        return y - 19 * (index / 2);
    }

    public static void outputGrid(ViewerCanvas canvas, ViewerRecipe recipe, int centreX, int y) {
        List<ViewerIngredient> outputs = recipe.outputs();
        int count = outputs.size();
        for (int i = 0; i < count; i++) {
            canvas.output(outputX(i, count, centreX), outputY(i, y), outputs.get(i), recipe.chance(i));
        }
    }

    public static void heatSlots(ViewerCanvas canvas, ViewerRecipe recipe, int x, int y) {
        heatSlots(canvas, recipe, x, y, 0);
    }

    public static void heatSlots(ViewerCanvas canvas, ViewerRecipe recipe, int x, int y, int fromIndex) {
        List<ViewerIngredient> catalysts = recipe.catalysts();
        int drawn = 0;
        for (int i = fromIndex; i < catalysts.size() && drawn < 2; i++, drawn++) {
            canvas.bareSlot(drawn == 0 ? x : x + 19, y, catalysts.get(i));
        }
    }

    public static void heatBar(ViewerCanvas canvas, HeatCondition heat, int x, int y) {
        canvas.texture(heat == HeatCondition.NONE ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR, x, y);
        canvas.text(heatLabel(heat), x + 5, y + 6, heat.getColor());
    }

    public static void blazeBurner(ViewerCanvas canvas, HeatCondition heat, int x, int y) {
        if (heat == HeatCondition.NONE) {
            return;
        }
        canvas.pip(x, y, (pose, px, py) -> new BasinBlazeBurnerRenderState(pose, px, py, heat.visualizeAsBlazeBurner()));
    }

    public static void shadow(ViewerCanvas canvas, HeatCondition heat, int x, int noHeatY, int heatY) {
        if (heat == HeatCondition.NONE) {
            canvas.texture(AllGuiTextures.JEI_SHADOW, x, noHeatY);
        } else {
            canvas.texture(AllGuiTextures.JEI_LIGHT, x, heatY);
        }
    }

    public static void heldItem(ViewerCanvas canvas, ViewerRecipe recipe, int x, int y) {
        if (recipe.keepsHeldItem()) {
            canvas.slot(x, y, recipe.input(0), keepHeldLine());
        } else {
            canvas.slot(x, y, recipe.input(0));
        }
    }

    public static BlockState blockOf(ViewerIngredient ingredient) {
        if (ingredient instanceof ViewerIngredient.OfStacks stacks) {
            for (ViewerStack stack : stacks.stacks()) {
                if (stack instanceof ViewerStack.OfItem item && item.stack().getItem() instanceof BlockItem blockItem) {
                    return blockItem.getBlock().defaultBlockState();
                }
            }
        }
        return null;
    }

    public static ViewerStack.OfFluid firstFluid(ViewerIngredient ingredient) {
        if (ingredient instanceof ViewerIngredient.OfStacks stacks) {
            for (ViewerStack stack : stacks.stacks()) {
                if (stack instanceof ViewerStack.OfFluid fluid) {
                    return fluid;
                }
            }
        }
        return null;
    }

    public static void basinBackground(ViewerCanvas canvas, HeatCondition heat, int outputCount) {
        int arrowY = (outputCount <= 4 ? 32 : 41) - ((outputCount - 1) / 2) * 19;
        canvas.texture(AllGuiTextures.JEI_DOWN_ARROW, 136, arrowY);
        if (heat == HeatCondition.NONE) {
            canvas.texture(AllGuiTextures.JEI_NO_HEAT_BAR, 4, 80);
            canvas.texture(AllGuiTextures.JEI_SHADOW, 81, 68);
        } else {
            canvas.texture(AllGuiTextures.JEI_HEAT_BAR, 4, 80);
            canvas.texture(AllGuiTextures.JEI_LIGHT, 81, 88);
            canvas.pip(91, 69, (pose, x, y) -> new BasinBlazeBurnerRenderState(pose, x, y, heat.visualizeAsBlazeBurner()));
        }
        canvas.text(heatLabel(heat), 9, 86, heat.getColor());
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

    public static void basinInputs(ViewerCanvas canvas, ViewerRecipe recipe) {
        List<ViewerIngredient> inputs = recipe.inputs();
        int count = inputs.size();
        for (int i = 0; i < count; i++) {
            canvas.slot(basinInputX(i, count), basinInputY(i, count), inputs.get(i));
        }
    }

    public static void basinOutputs(ViewerCanvas canvas, ViewerRecipe recipe, int yBase) {
        List<ViewerIngredient> outputs = recipe.outputs();
        int count = outputs.size();
        boolean centreLast = count % 2 != 0;
        for (int i = 0; i < count; i++) {
            canvas.output(basinOutputX(i, count - 1, centreLast), basinOutputY(i, yBase), outputs.get(i), recipe.chance(i));
        }
    }

    public static void basinHeatSlots(ViewerCanvas canvas, ViewerRecipe recipe) {
        List<ViewerIngredient> catalysts = recipe.catalysts();
        for (int i = 0; i < catalysts.size() && i < 2; i++) {
            canvas.bareSlot(i == 0 ? 134 : 153, 81, catalysts.get(i));
        }
    }
}
