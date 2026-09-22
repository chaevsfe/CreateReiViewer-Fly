package dev.chaevsfe.createreiviewer.api;

import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.recipe.SizedIngredient;
import com.zurrtum.create.foundation.fluid.FluidIngredient;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplays;
import dev.chaevsfe.createreiviewer.display.CreateReiEntries;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Deprecated
public final class CreateReiDisplayBuilder {
    private final Identifier category;
    private final List<EntryIngredient> inputs = new ArrayList<>();
    private final List<EntryIngredient> catalysts = new ArrayList<>();
    private final List<EntryIngredient> outputs = new ArrayList<>();
    private final List<Float> chances = new ArrayList<>();
    private int duration;
    private int heat = CreateReiDisplay.HEAT_NONE;
    private int flags;
    private Optional<Identifier> location = Optional.empty();

    private CreateReiDisplayBuilder(Identifier category) {
        this.category = category;
    }

    public static CreateReiDisplayBuilder of(CategoryIdentifier<CreateReiDisplay> category) {
        return new CreateReiDisplayBuilder(CreateReiApi.categoryId(category));
    }

    public CreateReiDisplayBuilder input(EntryIngredient entries) {
        inputs.add(entries);
        return this;
    }

    public CreateReiDisplayBuilder input(Ingredient ingredient) {
        return input(EntryIngredients.ofIngredient(ingredient));
    }

    public CreateReiDisplayBuilder inputs(List<EntryIngredient> entries) {
        inputs.addAll(entries);
        return this;
    }

    public CreateReiDisplayBuilder sizedInputs(List<SizedIngredient> ingredients) {
        return inputs(CreateReiEntries.items(ingredients));
    }

    public CreateReiDisplayBuilder fluidInputs(List<FluidIngredient> ingredients) {
        return inputs(CreateReiEntries.fluids(ingredients));
    }

    public CreateReiDisplayBuilder catalyst(EntryIngredient entries) {
        catalysts.add(entries);
        return this;
    }

    public CreateReiDisplayBuilder catalysts(List<EntryIngredient> entries) {
        catalysts.addAll(entries);
        return this;
    }

    public CreateReiDisplayBuilder output(EntryIngredient entries, float chance) {
        outputs.add(entries);
        chances.add(chance);
        return this;
    }

    public CreateReiDisplayBuilder output(EntryIngredient entries) {
        return output(entries, 1.0f);
    }

    public CreateReiDisplayBuilder results(List<ProcessingOutput> results) {
        for (ProcessingOutput result : results) {
            output(CreateReiEntries.output(result), result.chance());
        }
        return this;
    }

    public CreateReiDisplayBuilder fluidResults(List<FluidStack> results) {
        for (FluidStack result : results) {
            output(CreateReiEntries.fluid(result), 1.0f);
        }
        return this;
    }

    public CreateReiDisplayBuilder duration(int ticks) {
        this.duration = ticks;
        return this;
    }

    public CreateReiDisplayBuilder heat(HeatCondition condition) {
        this.heat = condition.ordinal();
        return catalysts(CreateReiDisplays.heatCatalysts(condition));
    }

    public CreateReiDisplayBuilder flags(int flags) {
        this.flags = flags;
        return this;
    }

    public CreateReiDisplayBuilder keepHeldItem(boolean keep) {
        return keep ? flags(flags | CreateReiDisplays.KEEP_HELD_ITEM) : this;
    }

    public CreateReiDisplayBuilder location(Identifier location) {
        this.location = Optional.ofNullable(location);
        return this;
    }

    public CreateReiDisplayBuilder location(RecipeHolder<? extends Recipe<?>> holder) {
        this.location = CreateReiDisplays.locationOf(holder);
        return this;
    }

    public CreateReiDisplay build() {
        return new CreateReiDisplay(category, inputs, catalysts, outputs, chances, duration, heat, flags, location);
    }
}
