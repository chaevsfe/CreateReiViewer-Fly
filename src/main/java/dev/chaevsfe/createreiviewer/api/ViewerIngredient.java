package dev.chaevsfe.createreiviewer.api;

import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

public sealed interface ViewerIngredient permits ViewerIngredient.OfIngredient, ViewerIngredient.OfStacks {
    ViewerIngredient EMPTY = new OfStacks(List.of());

    static ViewerIngredient empty() {
        return EMPTY;
    }

    static ViewerIngredient of(Ingredient ingredient) {
        return new OfIngredient(ingredient, 1);
    }

    static ViewerIngredient of(Ingredient ingredient, int count) {
        return new OfIngredient(ingredient, count);
    }

    static ViewerIngredient of(SizedIngredient ingredient) {
        return new OfIngredient(ingredient.getIngredient(), ingredient.getCount());
    }

    static ViewerIngredient of(ItemLike item) {
        return new OfStacks(List.of(ViewerStack.item(item)));
    }

    static ViewerIngredient of(ItemStack... stacks) {
        return ofItems(List.of(stacks));
    }

    static ViewerIngredient ofItems(List<ItemStack> stacks) {
        List<ViewerStack> list = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            list.add(ViewerStack.item(stack));
        }
        return new OfStacks(list);
    }

    static ViewerIngredient ofStacks(List<? extends ViewerStack> stacks) {
        return new OfStacks(List.copyOf(stacks));
    }

    static ViewerIngredient output(ProcessingOutput output) {
        return of(output.create());
    }

    static ViewerIngredient template(ItemStackTemplate template) {
        return of(template.create());
    }

    static ViewerIngredient fluid(Fluid fluid, long droplets) {
        return new OfStacks(List.of(ViewerStack.fluid(fluid, droplets)));
    }

    static ViewerIngredient fluid(Fluid fluid, long droplets, DataComponentPatch components) {
        return new OfStacks(List.of(ViewerStack.fluid(fluid, droplets, components)));
    }

    static ViewerIngredient fluid(com.zurrtum.create.infrastructure.fluids.FluidStack stack) {
        return new OfStacks(List.of(ViewerStack.fluid(stack)));
    }

    static ViewerIngredient fluid(FluidIngredient ingredient) {
        List<com.zurrtum.create.infrastructure.fluids.FluidStack> matching = ingredient.getMatchingFluidStacks();
        List<ViewerStack> stacks = new ArrayList<>(matching.size());
        for (com.zurrtum.create.infrastructure.fluids.FluidStack stack : matching) {
            int amount = stack.getAmount() > 0 ? stack.getAmount() : ingredient.amount();
            stacks.add(ViewerStack.fluid(stack.getFluid(), amount, stack.getComponentChanges()));
        }
        return new OfStacks(stacks);
    }

    boolean isEmpty();

    record OfIngredient(Ingredient ingredient, int count) implements ViewerIngredient {
        public OfIngredient {
            count = Math.max(1, count);
        }

        @Override
        public boolean isEmpty() {
            return ingredient.isEmpty();
        }
    }

    record OfStacks(List<ViewerStack> stacks) implements ViewerIngredient {
        public OfStacks {
            stacks = List.copyOf(stacks);
        }

        @Override
        public boolean isEmpty() {
            for (ViewerStack stack : stacks) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }
}
