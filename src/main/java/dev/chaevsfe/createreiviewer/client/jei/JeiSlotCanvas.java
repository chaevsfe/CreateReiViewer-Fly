package dev.chaevsfe.createreiviewer.client.jei;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.ViewerStack;
import dev.chaevsfe.createreiviewer.api.client.ViewerCanvas;
import dev.chaevsfe.createreiviewer.api.client.ViewerLayouts;
import dev.chaevsfe.createreiviewer.client.widget.PipFactory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class JeiSlotCanvas implements ViewerCanvas {
    private final IRecipeLayoutBuilder builder;
    private final int offsetY;

    JeiSlotCanvas(IRecipeLayoutBuilder builder, int offsetY) {
        this.builder = builder;
        this.offsetY = offsetY;
    }

    @Override
    public void slot(int x, int y, ViewerIngredient ingredient, Component... tooltip) {
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y + offsetY)
            .setBackground(TextureDrawable.of(AllGuiTextures.JEI_SLOT), -1, -1);
        fill(slot, ingredient);
        tooltip(slot, List.of(tooltip));
    }

    @Override
    public void bareSlot(int x, int y, ViewerIngredient ingredient, Component... tooltip) {
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, x, y + offsetY);
        fill(slot, ingredient);
        tooltip(slot, List.of(tooltip));
    }

    @Override
    public void output(int x, int y, ViewerIngredient ingredient, float chance) {
        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y + offsetY)
            .setBackground(TextureDrawable.of(ViewerLayouts.slotBackground(chance)), -1, -1);
        fill(slot, ingredient);
        if (chance < 1.0f) {
            tooltip(slot, List.of(ViewerLayouts.chanceLine(chance)));
        }
    }

    private static void tooltip(IRecipeSlotBuilder slot, List<Component> lines) {
        if (!lines.isEmpty()) {
            slot.addRichTooltipCallback(new TooltipLines(lines));
        }
    }

    static void fill(IRecipeSlotBuilder slot, ViewerIngredient ingredient) {
        if (ingredient instanceof ViewerIngredient.OfIngredient of) {
            if (of.count() <= 1) {
                slot.add(of.ingredient());
                return;
            }
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack stack : of.ingredient().display().resolveForStacks(slot.getContextMap())) {
                stacks.add(stack.copyWithCount(stack.getCount() * of.count()));
            }
            slot.addItemStacks(stacks);
            return;
        }
        List<ViewerStack> stacks = ((ViewerIngredient.OfStacks) ingredient).stacks();
        long capacity = 0;
        for (ViewerStack stack : stacks) {
            if (stack instanceof ViewerStack.OfFluid fluid && !fluid.isEmpty()) {
                capacity = Math.max(capacity, fluid.amount());
            }
        }
        if (capacity > 0) {
            slot.setFluidRenderer(capacity, false, 16, 16);
        }
        for (ViewerStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            if (stack instanceof ViewerStack.OfItem item) {
                slot.add(item.stack());
            } else if (stack instanceof ViewerStack.OfFluid fluid) {
                slot.add(fluid.fluid(), fluid.amount(), fluid.components());
            }
        }
    }

    @Override
    public void texture(AllGuiTextures texture, int x, int y) {
    }

    @Override
    public void icon(AllIcons icon, int x, int y) {
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean shadow) {
    }

    @Override
    public void tooltip(int x, int y, int width, int height, Component text) {
    }

    @Override
    public void pip(int x, int y, PipFactory factory) {
    }

    @Override
    public void blockPip(int x, int y, BlockState state) {
    }

    @Override
    public void itemPip(int x, int y, int size, Supplier<ItemStack> stack) {
    }

    @Override
    public void pipScaled(int x, int y, float scale, PipFactory factory) {
    }
}
