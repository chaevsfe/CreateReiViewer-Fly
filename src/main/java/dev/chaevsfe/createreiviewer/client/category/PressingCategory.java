package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.PressRenderState;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class PressingCategory extends CreateReiCategory<CreateReiDisplay> {
    public PressingCategory() {
        super("create.recipe.pressing");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.PRESSING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_PRESS, AllItems.IRON_SHEET);
    }

    @Override
    protected int contentHeight() {
        return 70;
    }

    @Override
    protected int contentOverhangTop() {
        return 16;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_SHADOW, 61, 41);
        panel.texture(AllGuiTextures.JEI_LONG_ARROW, 52, 54);
        panel.pip(73, -16, PressRenderState::new);

        panel.slot(27, 51, display.inputs().get(0));
        List<EntryIngredient> outputs = display.outputs();
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(131 + 19 * i, 51, outputs.get(i), display.chance(i));
        }
    }
}
