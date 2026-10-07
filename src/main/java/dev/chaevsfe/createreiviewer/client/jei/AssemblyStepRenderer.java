package dev.chaevsfe.createreiviewer.client.jei;

import com.zurrtum.create.client.compat.jei.category.SequencedAssemblyCategory;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.client.registry.ViewerClientPlugins;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

final class AssemblyStepRenderer extends SequencedAssemblyCategory.SequencedRenderer<Recipe<?>> {
    private final ViewerClientPlugins.AssemblyStep step;

    private AssemblyStepRenderer(ViewerClientPlugins.AssemblyStep step) {
        this.step = step;
    }

    static int registerMissing() {
        int added = 0;
        for (ViewerClientPlugins.AssemblyStep step : ViewerClientPlugins.assemblySteps().values()) {
            Optional<RecipeType<?>> type = BuiltInRegistries.RECIPE_TYPE.getOptional(step.type());
            if (type.isEmpty()) {
                CreateReiViewer.LOGGER.warn("{} drew sequenced assembly step {}, which is no recipe type", step.owner(), step.type());
                continue;
            }
            if (SequencedAssemblyCategory.RENDER.containsKey(type.get())) {
                continue;
            }
            SequencedAssemblyCategory.RENDER.put(type.get(), new AssemblyStepRenderer(step));
            added++;
        }
        return added;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int index, int x, int y, Optional<IRecipeSlotView> slot) {
        try {
            step.drawing().draw(new JeiDrawCanvas(graphics, 0), index, x, y);
        } catch (RuntimeException | LinkageError exception) {
            CreateReiViewer.LOGGER.warn("{} could not draw sequenced assembly step {}", step.owner(), step.type(), exception);
        }
    }

    @Override
    public IRecipeSlotBuilder addSlot(IRecipeLayoutBuilder builder, int x, int y, Recipe<?> recipe) {
        return null;
    }
}
