package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.render.MixingBasinRenderState;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.Items;

public class AutomaticBrewingCategory extends BasinCategory {
    public AutomaticBrewingCategory() {
        super("create.recipe.automatic_brewing");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.AUTOMATIC_BREWING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_MIXER, Items.BREWING_STAND);
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        basinBackground(panel, HeatCondition.HEATED, 1);
        panel.pip(91, -5, MixingBasinRenderState::new);
        panel.slot(21, 51, display.inputs().get(0));
        panel.slot(40, 51, display.inputs().get(1));
        panel.output(142, 51, display.outputs().get(0), 1.0f);
        panel.bareSlot(134, 81, EntryIngredients.of(AllItems.BLAZE_BURNER));
    }
}
