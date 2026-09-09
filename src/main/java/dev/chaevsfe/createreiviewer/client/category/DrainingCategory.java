package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.DrainRenderState;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.world.item.Items;

public class DrainingCategory extends CreateReiCategory<CreateReiDisplay> {
    public DrainingCategory() {
        super("create.recipe.draining");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.DRAINING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.ITEM_DRAIN, Items.WATER_BUCKET);
    }

    @Override
    protected int contentHeight() {
        return 50;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_SHADOW, 62, 37);
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 73, 4);

        FluidStack fluid = firstFluid(display.outputs().get(0));
        if (fluid != null) {
            panel.pip(75, 23, (pose, x, y) -> new DrainRenderState(pose, fluid.getFluid(), fluid.getPatch(), x, y));
        }
        panel.slot(27, 8, display.inputs().get(0));
        panel.output(132, 8, display.outputs().get(0), 1.0f);
        if (display.outputs().size() > 1) {
            panel.output(132, 27, display.outputs().get(1), 1.0f);
        }
    }

    static FluidStack firstFluid(EntryIngredient ingredient) {
        for (EntryStack<?> stack : ingredient) {
            if (stack.getValue() instanceof FluidStack fluidStack) {
                return fluidStack;
            }
        }
        return null;
    }
}
