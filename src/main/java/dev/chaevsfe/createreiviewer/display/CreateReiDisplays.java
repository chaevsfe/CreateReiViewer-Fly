package dev.chaevsfe.createreiviewer.display;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.kinetics.mixer.MixingRecipe;
import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CreateReiDisplays {
    private CreateReiDisplays() {
    }

    public static CreateReiDisplay mixing(RecipeHolder<MixingRecipe> holder) {
        MixingRecipe recipe = holder.value();

        List<EntryIngredient> inputs = new ArrayList<>();
        inputs.addAll(CreateReiEntries.items(recipe.ingredients()));
        inputs.addAll(CreateReiEntries.fluids(recipe.fluidIngredients()));

        List<EntryIngredient> outputs = new ArrayList<>();
        List<Float> chances = new ArrayList<>();
        for (ProcessingOutput output : recipe.results()) {
            outputs.add(CreateReiEntries.output(output));
            chances.add(output.chance());
        }
        for (com.zurrtum.create.infrastructure.fluids.FluidStack fluid : recipe.fluidResults()) {
            outputs.add(CreateReiEntries.fluid(fluid));
            chances.add(1.0f);
        }

        return new CreateReiDisplay(
            identifierOf(CreateReiCategories.MIXING),
            inputs,
            heatCatalysts(recipe.heat()),
            outputs,
            chances,
            recipe.time(),
            recipe.heat().ordinal(),
            locationOf(holder)
        );
    }

    public static List<EntryIngredient> heatCatalysts(HeatCondition heat) {
        List<EntryIngredient> catalysts = new ArrayList<>(2);
        if (!heat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.NONE)) {
            catalysts.add(EntryIngredients.of(AllItems.BLAZE_BURNER));
        }
        if (!heat.testBlazeBurner(BlazeBurnerBlock.HeatLevel.KINDLED)) {
            catalysts.add(EntryIngredients.of(AllItems.BLAZE_CAKE));
        }
        return catalysts;
    }

    public static Identifier identifierOf(CategoryIdentifier<?> category) {
        return Identifier.fromNamespaceAndPath(category.getNamespace(), category.getPath());
    }

    public static Optional<Identifier> locationOf(RecipeHolder<? extends Recipe<?>> holder) {
        return Optional.of(holder.id().identifier());
    }
}
