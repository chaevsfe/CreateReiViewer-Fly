package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.MillstoneRenderState;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class MillingCategory extends CreateReiCategory<CreateReiDisplay> {
    public MillingCategory() {
        super("create.recipe.milling");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.MILLING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MILLSTONE, AllItems.WHEAT_FLOUR);
    }

    @Override
    protected int contentHeight() {
        return 53;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_ARROW, 85, 32);
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 43, 4);
        panel.texture(AllGuiTextures.JEI_SHADOW, 32, 40);
        panel.pip(42, 19, MillstoneRenderState::new);

        panel.slot(15, 9, display.inputs().get(0));
        List<EntryIngredient> outputs = display.outputs();
        if (outputs.size() == 1) {
            panel.output(139, 27, outputs.get(0), display.chance(0));
            return;
        }
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(i % 2 == 0 ? 133 : 152, 27 - 19 * (i / 2), outputs.get(i), display.chance(i));
        }
    }
}
