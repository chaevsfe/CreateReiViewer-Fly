package dev.chaevsfe.createreiviewer.display;

import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class CreateReiEntries {
    private CreateReiEntries() {
    }

    public static EntryIngredient item(Ingredient ingredient, int count) {
        EntryIngredient base = EntryIngredients.ofIngredient(ingredient);
        if (count <= 1) {
            return base;
        }
        return base.map(stack -> withCount(stack, count));
    }

    public static EntryIngredient item(SizedIngredient ingredient) {
        return item(ingredient.getIngredient(), ingredient.getCount());
    }

    public static List<EntryIngredient> items(List<SizedIngredient> ingredients) {
        List<EntryIngredient> list = new ArrayList<>(ingredients.size());
        for (SizedIngredient ingredient : ingredients) {
            list.add(item(ingredient));
        }
        return list;
    }

    public static EntryIngredient output(ProcessingOutput output) {
        return EntryIngredient.of(EntryStacks.of(output.create()));
    }

    public static EntryIngredient fluid(com.zurrtum.create.infrastructure.fluids.FluidStack stack) {
        return EntryIngredient.of(EntryStacks.of(toArchitectury(stack)));
    }

    public static EntryIngredient fluid(FluidIngredient ingredient) {
        List<com.zurrtum.create.infrastructure.fluids.FluidStack> matching = ingredient.getMatchingFluidStacks();
        if (matching.isEmpty()) {
            return EntryIngredient.empty();
        }
        List<EntryStack<?>> stacks = new ArrayList<>(matching.size());
        for (com.zurrtum.create.infrastructure.fluids.FluidStack stack : matching) {
            stacks.add(EntryStacks.of(toArchitectury(stack, ingredient.amount())));
        }
        return EntryIngredient.of(stacks);
    }

    public static List<EntryIngredient> fluids(List<FluidIngredient> ingredients) {
        List<EntryIngredient> list = new ArrayList<>(ingredients.size());
        for (FluidIngredient ingredient : ingredients) {
            list.add(fluid(ingredient));
        }
        return list;
    }

    private static dev.architectury.fluid.FluidStack toArchitectury(com.zurrtum.create.infrastructure.fluids.FluidStack stack) {
        return toArchitectury(stack, stack.getAmount());
    }

    private static dev.architectury.fluid.FluidStack toArchitectury(
        com.zurrtum.create.infrastructure.fluids.FluidStack stack,
        int fallbackAmount
    ) {
        int amount = stack.getAmount() > 0 ? stack.getAmount() : fallbackAmount;
        return dev.architectury.fluid.FluidStack.create(stack.getFluid(), amount, stack.getComponentChanges());
    }

    private static EntryStack<?> withCount(EntryStack<?> stack, int count) {
        Object value = stack.getValue();
        if (value instanceof ItemStack itemStack && !itemStack.isEmpty()) {
            ItemStack copy = itemStack.copy();
            copy.setCount(count);
            return EntryStacks.of(copy);
        }
        return stack;
    }
}
