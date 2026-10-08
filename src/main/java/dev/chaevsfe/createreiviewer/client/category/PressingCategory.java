package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.PressRenderState;
import dev.chaevsfe.createreiviewer.client.render.PressDepotRenderState;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiWidgets;
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
        return 85;
    }

    @Override
    protected int contentOverhangTop() {
        return 7;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        List<EntryIngredient> outputs = display.outputs();
        int count = outputs.size();
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 136, (count <= 4 ? 32 : 41) - ((count - 1) / 2) * 19);
        panel.texture(AllGuiTextures.JEI_SHADOW, 81, 68);
        panel.pip(91, -7, PressDepotRenderState::new);
        panel.pip(91, -7, PressRenderState::new);

        panel.slot(CreateReiWidgets.basinInputX(0, 1), CreateReiWidgets.basinInputY(0, 1), display.inputs().get(0));
        boolean centreLast = count % 2 != 0;
        for (int i = 0; i < count; i++) {
            panel.output(
                CreateReiWidgets.basinOutputX(i, count - 1, centreLast),
                CreateReiWidgets.basinOutputY(i, 51),
                outputs.get(i),
                display.chance(i)
            );
        }
    }
}
