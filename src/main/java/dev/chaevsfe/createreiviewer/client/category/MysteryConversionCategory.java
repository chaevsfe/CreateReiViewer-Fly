package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class MysteryConversionCategory extends CreateReiCategory<CreateReiDisplay> {
    public MysteryConversionCategory() {
        super("create.recipe.mystery_conversion");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.MYSTERY_CONVERSION;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(AllItems.PECULIAR_BELL);
    }

    @Override
    protected int contentHeight() {
        return 50;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_LONG_ARROW, 52, 20);
        panel.texture(AllGuiTextures.JEI_QUESTION_MARK, 77, 5);
        panel.slot(27, 17, display.inputs().get(0));
        panel.output(132, 17, display.outputs().get(0), 1.0f);
    }
}
