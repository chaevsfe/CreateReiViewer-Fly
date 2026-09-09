package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.CrushWheelRenderState;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class CrushingCategory extends CreateReiCategory<CreateReiDisplay> {
    public CrushingCategory() {
        super("create.recipe.crushing");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.CRUSHING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.CRUSHING_WHEEL, AllItems.CRUSHED_GOLD);
    }

    @Override
    protected int contentHeight() {
        return 100;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 72, 7);
        panel.pip(42, 24, CrushWheelRenderState::new);

        panel.slot(51, 3, display.inputs().get(0));
        List<EntryIngredient> outputs = display.outputs();
        int count = outputs.size();
        int xBase = (179 - 19 * count) / 2 + 3;
        for (int i = 0; i < count; i++) {
            panel.output(xBase + 19 * i, 83, outputs.get(i), display.chance(i));
        }
    }
}
