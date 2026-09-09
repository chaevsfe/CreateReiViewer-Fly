package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.render.PressBasinRenderState;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class PackingCategory extends BasinCategory {
    public PackingCategory() {
        super("create.recipe.packing");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.PACKING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_PRESS, AllItems.BASIN);
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        basinBackground(panel, heatOf(display), display.outputs().size());
        panel.pip(91, -5, PressBasinRenderState::new);
        basinInputs(panel, display);
        basinOutputs(panel, display, 51);
        heatSlots(panel, display);
    }
}
