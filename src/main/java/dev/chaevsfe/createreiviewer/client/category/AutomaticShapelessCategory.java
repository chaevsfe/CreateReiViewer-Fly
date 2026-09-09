package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.MixingBasinRenderState;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiWidgets;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.world.item.Items;

import java.util.List;

public class AutomaticShapelessCategory extends CreateReiCategory<CreateReiDisplay> {
    public AutomaticShapelessCategory() {
        super("create.recipe.automatic_shapeless");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.AUTOMATIC_SHAPELESS;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_MIXER, Items.CRAFTING_TABLE);
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
        panel.pip(91, -5, MixingBasinRenderState::new);

        List<EntryIngredient> inputs = display.inputs();
        int count = inputs.size();
        for (int i = 0; i < count; i++) {
            panel.slot(CreateReiWidgets.basinInputX(i, count), CreateReiWidgets.basinInputY(i, count), inputs.get(i));
        }
        panel.output(142, 51, display.outputs().get(0), 1.0f);
    }
}
