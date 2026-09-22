package dev.chaevsfe.createreiviewer.display;

import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.ViewerRecipe;
import dev.chaevsfe.createreiviewer.api.ViewerStack;
import dev.chaevsfe.createreiviewer.registry.ViewerMapping;
import dev.chaevsfe.createreiviewer.registry.ViewerPlugins;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ViewerReiDisplays {
    private ViewerReiDisplays() {
    }

    public static EntryIngredient entries(ViewerIngredient ingredient) {
        if (ingredient instanceof ViewerIngredient.OfIngredient of) {
            return CreateReiEntries.item(of.ingredient(), of.count());
        }
        List<ViewerStack> stacks = ((ViewerIngredient.OfStacks) ingredient).stacks();
        if (stacks.isEmpty()) {
            return EntryIngredient.empty();
        }
        List<EntryStack<?>> entries = new ArrayList<>(stacks.size());
        for (ViewerStack stack : stacks) {
            entries.add(entry(stack));
        }
        return EntryIngredient.of(entries);
    }

    public static EntryStack<?> entry(ViewerStack stack) {
        if (stack instanceof ViewerStack.OfItem item) {
            return EntryStacks.of(item.stack());
        }
        ViewerStack.OfFluid fluid = (ViewerStack.OfFluid) stack;
        return EntryStacks.of(dev.architectury.fluid.FluidStack.create(fluid.fluid(), fluid.amount(), fluid.components()));
    }

    public static List<EntryIngredient> entries(List<ViewerIngredient> ingredients) {
        List<EntryIngredient> list = new ArrayList<>(ingredients.size());
        for (ViewerIngredient ingredient : ingredients) {
            list.add(entries(ingredient));
        }
        return list;
    }

    public static CreateReiDisplay display(ViewerRecipe recipe) {
        return new CreateReiDisplay(
            recipe.category(),
            entries(recipe.inputs()),
            entries(recipe.catalysts()),
            entries(recipe.outputs()),
            recipe.chances(),
            recipe.duration(),
            recipe.heat().ordinal(),
            recipe.flags(),
            recipe.location()
        );
    }

    public static ViewerRecipe recipe(CreateReiDisplay display, Map<ViewerIngredient, EntryIngredient> originals) {
        HeatCondition[] heats = HeatCondition.values();
        return new ViewerRecipe(
            display.category(),
            ingredients(display.inputs(), originals),
            ingredients(display.catalysts(), originals),
            ingredients(display.outputs(), originals),
            display.chances(),
            display.duration(),
            heats[Math.clamp(display.heat(), 0, heats.length - 1)],
            display.flags(),
            display.getDisplayLocation()
        );
    }

    private static List<ViewerIngredient> ingredients(List<EntryIngredient> entries, Map<ViewerIngredient, EntryIngredient> originals) {
        List<ViewerIngredient> list = new ArrayList<>(entries.size());
        for (EntryIngredient entry : entries) {
            ViewerIngredient ingredient = ingredient(entry);
            originals.put(ingredient, entry);
            list.add(ingredient);
        }
        return list;
    }

    public static ViewerIngredient ingredient(EntryIngredient entries) {
        List<ViewerStack> stacks = new ArrayList<>(entries.size());
        for (EntryStack<?> entry : entries) {
            Object value = entry.getValue();
            if (value instanceof ItemStack stack) {
                stacks.add(ViewerStack.item(stack));
            } else if (value instanceof dev.architectury.fluid.FluidStack fluid) {
                stacks.add(ViewerStack.fluid(fluid.getFluid(), fluid.getAmount(), fluid.getPatch()));
            }
        }
        return new ViewerIngredient.OfStacks(stacks);
    }

    public static void register(ServerDisplayRegistry registry) {
        List<ViewerMapping<?>> mappings = ViewerPlugins.mappings();
        for (ViewerMapping<?> mapping : mappings) {
            fill(registry, mapping);
        }
        if (!mappings.isEmpty()) {
            CreateReiViewer.LOGGER.info("Recipe fillers registered for {} add-on mappings in {} categories",
                mappings.size(), ViewerPlugins.groups().values().stream().mapToInt(List::size).sum());
        }
    }

    private static <T extends Recipe<?>> void fill(ServerDisplayRegistry registry, ViewerMapping<T> mapping) {
        registry.<T, CreateReiDisplay>beginRecipeFiller(mapping.recipeClass())
            .filterType(mapping.type())
            .fillMultiple(holder -> {
                ViewerRecipe recipe = mapping.mapTyped(holder);
                return recipe == null ? List.of() : List.of(display(recipe));
            });
    }

    public static void report(ServerDisplayRegistry registry) {
        for (Map.Entry<String, List<Identifier>> group : ViewerPlugins.groups().entrySet()) {
            int total = 0;
            int empty = 0;
            for (Identifier id : group.getValue()) {
                List<? extends Display> displays = registry.get(CategoryIdentifier.of(id));
                total += displays.size();
                CreateReiViewer.LOGGER.info("Registered {} displays for category {}", displays.size(), id);
                if (displays.isEmpty()) {
                    empty++;
                    CreateReiViewer.LOGGER.warn("Category {} built ZERO displays", id);
                }
            }
            CreateReiViewer.LOGGER.info("{} display total: {} across {} categories, {} empty",
                group.getKey(), total, group.getValue().size(), empty);
        }
    }
}
