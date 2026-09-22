package dev.chaevsfe.createreiviewer.api;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ViewerRecipe {
    public static final int KEEP_HELD_ITEM = 1;

    private final Identifier category;
    private final List<ViewerIngredient> inputs;
    private final List<ViewerIngredient> catalysts;
    private final List<ViewerIngredient> outputs;
    private final List<Float> chances;
    private final int duration;
    private final HeatCondition heat;
    private final int flags;
    private final Optional<Identifier> location;

    public ViewerRecipe(
        Identifier category,
        List<ViewerIngredient> inputs,
        List<ViewerIngredient> catalysts,
        List<ViewerIngredient> outputs,
        List<Float> chances,
        int duration,
        HeatCondition heat,
        int flags,
        Optional<Identifier> location
    ) {
        this.category = category;
        this.inputs = List.copyOf(inputs);
        this.catalysts = List.copyOf(catalysts);
        this.outputs = List.copyOf(outputs);
        this.chances = padChances(chances, outputs.size());
        this.duration = Math.max(0, duration);
        this.heat = heat == null ? HeatCondition.NONE : heat;
        this.flags = flags;
        this.location = location == null ? Optional.empty() : location;
    }

    public static Builder builder(Identifier category) {
        return new Builder(category);
    }

    private static List<Float> padChances(List<Float> chances, int outputCount) {
        if (chances.size() == outputCount) {
            return List.copyOf(chances);
        }
        List<Float> padded = new ArrayList<>(outputCount);
        for (int i = 0; i < outputCount; i++) {
            padded.add(i < chances.size() ? chances.get(i) : 1.0f);
        }
        return Collections.unmodifiableList(padded);
    }

    public static List<ViewerIngredient> heatCatalysts(HeatCondition heat) {
        List<ViewerIngredient> catalysts = new ArrayList<>(2);
        if (!heat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.NONE)) {
            catalysts.add(ViewerIngredient.of(AllItems.BLAZE_BURNER));
        }
        if (!heat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.KINDLED)) {
            catalysts.add(ViewerIngredient.of(AllItems.BLAZE_CAKE));
        }
        return catalysts;
    }

    public Identifier category() {
        return category;
    }

    public List<ViewerIngredient> inputs() {
        return inputs;
    }

    public ViewerIngredient input(int index) {
        return index >= 0 && index < inputs.size() ? inputs.get(index) : ViewerIngredient.EMPTY;
    }

    public List<ViewerIngredient> catalysts() {
        return catalysts;
    }

    public ViewerIngredient catalyst(int index) {
        return index >= 0 && index < catalysts.size() ? catalysts.get(index) : ViewerIngredient.EMPTY;
    }

    public List<ViewerIngredient> outputs() {
        return outputs;
    }

    public ViewerIngredient output(int index) {
        return index >= 0 && index < outputs.size() ? outputs.get(index) : ViewerIngredient.EMPTY;
    }

    public List<Float> chances() {
        return chances;
    }

    public float chance(int index) {
        return index >= 0 && index < chances.size() ? chances.get(index) : 1.0f;
    }

    public int duration() {
        return duration;
    }

    public HeatCondition heat() {
        return heat;
    }

    public int flags() {
        return flags;
    }

    public boolean keepsHeldItem() {
        return (flags & KEEP_HELD_ITEM) != 0;
    }

    public Optional<Identifier> location() {
        return location;
    }

    public static final class Builder {
        private final Identifier category;
        private final List<ViewerIngredient> inputs = new ArrayList<>();
        private final List<ViewerIngredient> catalysts = new ArrayList<>();
        private final List<ViewerIngredient> outputs = new ArrayList<>();
        private final List<Float> chances = new ArrayList<>();
        private int duration;
        private HeatCondition heat = HeatCondition.NONE;
        private int flags;
        private Optional<Identifier> location = Optional.empty();

        private Builder(Identifier category) {
            this.category = category;
        }

        public Identifier category() {
            return category;
        }

        public Builder input(ViewerIngredient ingredient) {
            inputs.add(ingredient);
            return this;
        }

        public Builder input(Ingredient ingredient) {
            return input(ViewerIngredient.of(ingredient));
        }

        public Builder input(Ingredient ingredient, int count) {
            return input(ViewerIngredient.of(ingredient, count));
        }

        public Builder input(ItemLike item) {
            return input(ViewerIngredient.of(item));
        }

        public Builder input(ItemStack stack) {
            return input(ViewerIngredient.of(stack));
        }

        public Builder inputs(List<ViewerIngredient> ingredients) {
            inputs.addAll(ingredients);
            return this;
        }

        public Builder sizedInputs(List<SizedIngredient> ingredients) {
            for (SizedIngredient ingredient : ingredients) {
                input(ViewerIngredient.of(ingredient));
            }
            return this;
        }

        public Builder fluidInput(FluidIngredient ingredient) {
            return input(ViewerIngredient.fluid(ingredient));
        }

        public Builder fluidInputs(List<FluidIngredient> ingredients) {
            for (FluidIngredient ingredient : ingredients) {
                fluidInput(ingredient);
            }
            return this;
        }

        public Builder catalyst(ViewerIngredient ingredient) {
            catalysts.add(ingredient);
            return this;
        }

        public Builder catalyst(ItemStack stack) {
            return catalyst(ViewerIngredient.of(stack));
        }

        public Builder catalyst(ItemLike item) {
            return catalyst(ViewerIngredient.of(item));
        }

        public Builder catalysts(List<ViewerIngredient> ingredients) {
            catalysts.addAll(ingredients);
            return this;
        }

        public Builder output(ViewerIngredient ingredient, float chance) {
            outputs.add(ingredient);
            chances.add(chance);
            return this;
        }

        public Builder output(ViewerIngredient ingredient) {
            return output(ingredient, 1.0f);
        }

        public Builder output(ItemStack stack) {
            return output(ViewerIngredient.of(stack), 1.0f);
        }

        public Builder output(ItemStack stack, float chance) {
            return output(ViewerIngredient.of(stack), chance);
        }

        public Builder results(List<ProcessingOutput> results) {
            for (ProcessingOutput result : results) {
                output(ViewerIngredient.output(result), result.chance());
            }
            return this;
        }

        public Builder fluidResults(List<FluidStack> results) {
            for (FluidStack result : results) {
                output(ViewerIngredient.fluid(result), 1.0f);
            }
            return this;
        }

        public Builder duration(int ticks) {
            this.duration = ticks;
            return this;
        }

        public Builder heat(HeatCondition condition) {
            this.heat = condition;
            return catalysts(heatCatalysts(condition));
        }

        public Builder flags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder keepHeldItem(boolean keep) {
            return keep ? flags(flags | KEEP_HELD_ITEM) : this;
        }

        public Builder location(Identifier location) {
            this.location = Optional.ofNullable(location);
            return this;
        }

        public Builder location(RecipeHolder<? extends Recipe<?>> holder) {
            this.location = Optional.of(holder.id().identifier());
            return this;
        }

        public ViewerRecipe build() {
            return new ViewerRecipe(category, inputs, catalysts, outputs, chances, duration, heat, flags, location);
        }
    }
}
