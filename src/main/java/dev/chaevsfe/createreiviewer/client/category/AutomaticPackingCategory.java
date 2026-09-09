package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.PressBasinRenderState;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class AutomaticPackingCategory extends CreateReiCategory<CreateReiDisplay> {
    public AutomaticPackingCategory() {
        super("create.recipe.automatic_packing");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.AUTOMATIC_PACKING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_PRESS, AllItems.BASIN);
    }

    @Override
    protected int contentHeight() {
        return 85;
    }

    @Override
    protected int contentOverhangTop() {
        return 5;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 136, 32);
        panel.texture(AllGuiTextures.JEI_SHADOW, 81, 68);
        panel.pip(91, -5, PressBasinRenderState::new);

        List<EntryIngredient> inputs = display.inputs();
        int count = inputs.size();
        int columns = count == 4 ? 2 : 3;
        int xBase = columns == 2 ? 27 : 18;
        for (int i = 0; i < count; i++) {
            panel.slot(xBase + 19 * (i % columns), 51 - 19 * (i / columns), inputs.get(i));
        }
        panel.output(142, 51, display.outputs().get(0), 1.0f);
    }
}
