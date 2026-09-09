package dev.chaevsfe.createreiviewer.display;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.fluids.transfer.EmptyingRecipe;
import com.zurrtum.create.content.fluids.transfer.FillingRecipe;
import com.zurrtum.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.zurrtum.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.zurrtum.create.content.kinetics.mixer.CompactingRecipe;
import com.zurrtum.create.content.kinetics.mixer.MixingRecipe;
import com.zurrtum.create.content.kinetics.mixer.PotionRecipe;
import com.zurrtum.create.content.kinetics.press.PressingRecipe;
import com.zurrtum.create.content.kinetics.saw.CuttingRecipe;
import com.zurrtum.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.zurrtum.create.foundation.recipe.CreateSingleStackRollableRecipe;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CreateReiDisplays {
    public static final int KEEP_HELD_ITEM = 1;

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
        for (FluidStack fluid : recipe.fluidResults()) {
            outputs.add(CreateReiEntries.fluid(fluid));
            chances.add(1.0f);
        }

        return display(CreateReiCategories.MIXING, inputs, heatCatalysts(recipe.heat()), outputs, chances,
            recipe.time(), recipe.heat().ordinal(), 0, holder);
    }

    public static CreateReiDisplay packing(RecipeHolder<CompactingRecipe> holder) {
        CompactingRecipe recipe = holder.value();
        List<EntryIngredient> inputs = new ArrayList<>();
        inputs.addAll(CreateReiEntries.items(recipe.ingredients()));
        inputs.addAll(CreateReiEntries.fluids(recipe.fluidIngredients()));
        return display(CreateReiCategories.PACKING, inputs, heatCatalysts(recipe.heat()),
            outputsOf(recipe.results()), chancesOf(recipe.results()), 0, recipe.heat().ordinal(), 0, holder);
    }

    public static CreateReiDisplay automaticPacking(RecipeHolder<CraftingRecipe> holder) {
        return crafting(CreateReiCategories.AUTOMATIC_PACKING, holder);
    }

    public static CreateReiDisplay automaticShapeless(RecipeHolder<ShapelessRecipe> holder) {
        ShapelessRecipe recipe = holder.value();
        return display(CreateReiCategories.AUTOMATIC_SHAPELESS, condense(recipe.ingredients), List.of(),
            List.of(CreateReiEntries.template(recipe.result)), List.of(1.0f), 0, CreateReiDisplay.HEAT_NONE, 0, holder);
    }

    public static CreateReiDisplay potion(RecipeHolder<PotionRecipe> holder) {
        PotionRecipe recipe = holder.value();
        List<EntryIngredient> inputs = List.of(
            EntryIngredients.ofIngredient(recipe.ingredient()),
            CreateReiEntries.fluid(recipe.fluidIngredient())
        );
        return display(CreateReiCategories.AUTOMATIC_BREWING, inputs, List.of(),
            List.of(CreateReiEntries.fluid(recipe.result())), List.of(1.0f), 0, HeatCondition.HEATED.ordinal(), 0, holder);
    }

    public static CreateReiDisplay pressing(RecipeHolder<PressingRecipe> holder) {
        PressingRecipe recipe = holder.value();
        return rollable(CreateReiCategories.PRESSING, recipe.ingredient(), recipe.results(), 0, holder);
    }

    public static CreateReiDisplay milling(RecipeHolder<? extends CreateSingleStackRollableRecipe> holder) {
        return rollable(CreateReiCategories.MILLING, holder.value().ingredient(), holder.value().results(), 0, holder);
    }

    public static CreateReiDisplay sawing(RecipeHolder<CuttingRecipe> holder) {
        CuttingRecipe recipe = holder.value();
        return rollable(CreateReiCategories.SAWING, recipe.ingredient(), recipe.results(), recipe.time(), holder);
    }

    public static CreateReiDisplay crushing(RecipeHolder<? extends CreateSingleStackRollableRecipe> holder) {
        return rollable(CreateReiCategories.CRUSHING, holder.value().ingredient(), holder.value().results(), 0, holder);
    }

    public static CreateReiDisplay fanWashing(RecipeHolder<? extends CreateSingleStackRollableRecipe> holder) {
        return rollable(CreateReiCategories.FAN_WASHING, holder.value().ingredient(), holder.value().results(), 0, holder);
    }

    public static CreateReiDisplay fanHaunting(RecipeHolder<? extends CreateSingleStackRollableRecipe> holder) {
        return rollable(CreateReiCategories.FAN_HAUNTING, holder.value().ingredient(), holder.value().results(), 0, holder);
    }

    public static CreateReiDisplay fanSmoking(RecipeHolder<? extends SingleItemRecipe> holder) {
        return singleItem(CreateReiCategories.FAN_SMOKING, holder);
    }

    public static CreateReiDisplay fanBlasting(RecipeHolder<? extends SingleItemRecipe> holder) {
        return singleItem(CreateReiCategories.FAN_BLASTING, holder);
    }

    public static CreateReiDisplay sandpaperPolishing(RecipeHolder<SandPaperPolishingRecipe> holder) {
        SandPaperPolishingRecipe recipe = holder.value();
        return display(CreateReiCategories.SANDPAPER_POLISHING, List.of(EntryIngredients.ofIngredient(recipe.ingredient())),
            List.of(), List.of(CreateReiEntries.template(recipe.result())), List.of(1.0f), 0,
            CreateReiDisplay.HEAT_NONE, 0, holder);
    }

    public static CreateReiDisplay itemApplication(RecipeHolder<? extends ItemApplicationRecipe> holder) {
        return application(CreateReiCategories.ITEM_APPLICATION, holder);
    }

    public static CreateReiDisplay deploying(RecipeHolder<? extends ItemApplicationRecipe> holder) {
        return application(CreateReiCategories.DEPLOYING, holder);
    }

    public static CreateReiDisplay draining(RecipeHolder<EmptyingRecipe> holder) {
        EmptyingRecipe recipe = holder.value();
        return display(CreateReiCategories.DRAINING, List.of(EntryIngredients.ofIngredient(recipe.ingredient())),
            List.of(), List.of(CreateReiEntries.fluid(recipe.fluidResult()), CreateReiEntries.template(recipe.result())),
            List.of(1.0f, 1.0f), 0, CreateReiDisplay.HEAT_NONE, 0, holder);
    }

    public static CreateReiDisplay spoutFilling(RecipeHolder<FillingRecipe> holder) {
        FillingRecipe recipe = holder.value();
        List<EntryIngredient> inputs = List.of(
            EntryIngredients.ofIngredient(recipe.ingredient()),
            CreateReiEntries.fluid(recipe.fluidIngredient())
        );
        return display(CreateReiCategories.SPOUT_FILLING, inputs, List.of(),
            List.of(CreateReiEntries.template(recipe.result())), List.of(1.0f), 0, CreateReiDisplay.HEAT_NONE, 0, holder);
    }

    public static CreateReiGridDisplay mechanicalCrafting(RecipeHolder<MechanicalCraftingRecipe> holder) {
        MechanicalCraftingRecipe recipe = holder.value();
        List<Optional<Ingredient>> pattern = recipe.raw().ingredients();
        List<EntryIngredient> inputs = new ArrayList<>(pattern.size());
        for (Optional<Ingredient> slot : pattern) {
            inputs.add(slot.map(EntryIngredients::ofIngredient).orElse(EntryIngredient.empty()));
        }
        return new CreateReiGridDisplay(
            inputs,
            List.of(CreateReiEntries.template(recipe.result())),
            recipe.raw().width(),
            recipe.raw().height(),
            locationOf(holder)
        );
    }

    public static CreateReiSequenceDisplay sequencedAssembly(RecipeHolder<SequencedAssemblyRecipe> holder) {
        SequencedAssemblyRecipe recipe = holder.value();
        List<Recipe<?>> sequence = recipe.sequence();
        int loops = Math.max(1, recipe.loops());
        int steps = sequence.size() / loops;

        List<Identifier> stepTypes = new ArrayList<>(steps);
        List<EntryIngredient> stepEntries = new ArrayList<>(steps);
        for (int i = 0; i < steps; i++) {
            Recipe<?> step = sequence.get(i);
            stepTypes.add(typeIdOf(step));
            stepEntries.add(stepEntry(step));
        }

        List<EntryIngredient> outputs = new ArrayList<>(2);
        List<Float> chances = new ArrayList<>(2);
        outputs.add(CreateReiEntries.output(recipe.result()));
        chances.add(recipe.result().chance());
        if (!recipe.junks().isEmpty()) {
            outputs.add(CreateReiEntries.outputs(recipe.junks()));
            chances.add(1.0f - recipe.result().chance());
        }

        return new CreateReiSequenceDisplay(
            List.of(EntryIngredients.ofIngredient(recipe.ingredient())),
            outputs,
            chances,
            stepTypes,
            stepEntries,
            loops,
            locationOf(holder)
        );
    }

    public static CreateReiDisplay mysteryConversion(String path, net.minecraft.world.item.Item input, net.minecraft.world.item.Item output) {
        return new CreateReiDisplay(
            identifierOf(CreateReiCategories.MYSTERY_CONVERSION),
            List.of(CreateReiEntries.item(input)),
            List.of(),
            List.of(CreateReiEntries.item(output)),
            List.of(1.0f),
            0,
            CreateReiDisplay.HEAT_NONE,
            0,
            Optional.of(Identifier.fromNamespaceAndPath("create", path))
        );
    }

    public static CreateReiDisplay blockCutting(Identifier location, Ingredient input, List<List<ItemStackTemplate>> buckets) {
        List<EntryIngredient> outputs = new ArrayList<>(buckets.size());
        List<Float> chances = new ArrayList<>(buckets.size());
        for (List<ItemStackTemplate> bucket : buckets) {
            outputs.add(CreateReiEntries.templates(bucket));
            chances.add(1.0f);
        }
        return new CreateReiDisplay(
            identifierOf(CreateReiCategories.BLOCK_CUTTING),
            List.of(EntryIngredients.ofIngredient(input)),
            List.of(),
            outputs,
            chances,
            0,
            CreateReiDisplay.HEAT_NONE,
            0,
            Optional.of(location)
        );
    }

    public static CreateReiDisplay crafting(CategoryIdentifier<CreateReiDisplay> category, RecipeHolder<CraftingRecipe> holder) {
        CraftingRecipe recipe = holder.value();
        List<Ingredient> ingredients;
        ItemStackTemplate result;
        if (recipe instanceof ShapedRecipe shaped) {
            ingredients = new ArrayList<>();
            for (Optional<Ingredient> slot : shaped.getIngredients()) {
                slot.ifPresent(ingredients::add);
            }
            result = shaped.result;
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            ingredients = shapeless.ingredients;
            result = shapeless.result;
        } else {
            return null;
        }
        return display(category, CreateReiEntries.ingredients(ingredients), List.of(),
            List.of(CreateReiEntries.template(result)), List.of(1.0f), 0, CreateReiDisplay.HEAT_NONE, 0, holder);
    }

    private static CreateReiDisplay application(CategoryIdentifier<CreateReiDisplay> category, RecipeHolder<? extends ItemApplicationRecipe> holder) {
        ItemApplicationRecipe recipe = holder.value();
        List<EntryIngredient> inputs = List.of(
            EntryIngredients.ofIngredient(recipe.ingredient()),
            EntryIngredients.ofIngredient(recipe.target())
        );
        return display(category, inputs, List.of(), outputsOf(recipe.results()), chancesOf(recipe.results()),
            0, CreateReiDisplay.HEAT_NONE, recipe.keepHeldItem() ? KEEP_HELD_ITEM : 0, holder);
    }

    private static CreateReiDisplay singleItem(CategoryIdentifier<CreateReiDisplay> category, RecipeHolder<? extends SingleItemRecipe> holder) {
        SingleItemRecipe recipe = holder.value();
        return display(category, List.of(EntryIngredients.ofIngredient(recipe.input())), List.of(),
            List.of(CreateReiEntries.template(recipe.result())), List.of(1.0f), 0, CreateReiDisplay.HEAT_NONE, 0, holder);
    }

    private static CreateReiDisplay rollable(
        CategoryIdentifier<CreateReiDisplay> category,
        Ingredient ingredient,
        List<ProcessingOutput> results,
        int duration,
        RecipeHolder<? extends Recipe<?>> holder
    ) {
        return display(category, List.of(EntryIngredients.ofIngredient(ingredient)), List.of(),
            outputsOf(results), chancesOf(results), duration, CreateReiDisplay.HEAT_NONE, 0, holder);
    }

    private static CreateReiDisplay display(
        CategoryIdentifier<CreateReiDisplay> category,
        List<EntryIngredient> inputs,
        List<EntryIngredient> catalysts,
        List<EntryIngredient> outputs,
        List<Float> chances,
        int duration,
        int heat,
        int flags,
        RecipeHolder<? extends Recipe<?>> holder
    ) {
        return new CreateReiDisplay(identifierOf(category), inputs, catalysts, outputs, chances,
            duration, heat, flags, locationOf(holder));
    }

    private static List<EntryIngredient> outputsOf(List<ProcessingOutput> results) {
        List<EntryIngredient> outputs = new ArrayList<>(results.size());
        for (ProcessingOutput output : results) {
            outputs.add(CreateReiEntries.output(output));
        }
        return outputs;
    }

    private static List<Float> chancesOf(List<ProcessingOutput> results) {
        List<Float> chances = new ArrayList<>(results.size());
        for (ProcessingOutput output : results) {
            chances.add(output.chance());
        }
        return chances;
    }

    private static List<EntryIngredient> condense(List<Ingredient> ingredients) {
        Map<Ingredient, Integer> counts = new LinkedHashMap<>();
        for (Ingredient ingredient : ingredients) {
            counts.merge(ingredient, 1, Integer::sum);
        }
        List<EntryIngredient> condensed = new ArrayList<>(counts.size());
        for (Map.Entry<Ingredient, Integer> entry : counts.entrySet()) {
            condensed.add(CreateReiEntries.item(entry.getKey(), entry.getValue()));
        }
        return condensed;
    }

    private static EntryIngredient stepEntry(Recipe<?> step) {
        if (step instanceof ItemApplicationRecipe application) {
            return EntryIngredients.ofIngredient(application.ingredient());
        }
        if (step instanceof FillingRecipe filling) {
            return CreateReiEntries.fluid(filling.fluidIngredient());
        }
        return EntryIngredient.empty();
    }

    private static Identifier typeIdOf(Recipe<?> recipe) {
        Identifier id = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        return id == null ? Identifier.fromNamespaceAndPath("minecraft", "empty") : id;
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
