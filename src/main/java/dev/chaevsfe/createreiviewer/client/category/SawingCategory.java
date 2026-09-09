package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.SawRenderState;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.world.item.Items;

import java.util.List;

public class SawingCategory extends CreateReiCategory<CreateReiDisplay> {
    public SawingCategory() {
        super("create.recipe.sawing");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.SAWING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_SAW, Items.OAK_LOG);
    }

    @Override
    protected int contentHeight() {
        return 70;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 70, 6);
        panel.texture(AllGuiTextures.JEI_SHADOW, 55, 55);
        panel.pip(64, 31, SawRenderState::new);

        panel.slot(44, 5, display.inputs().get(0));
        List<EntryIngredient> outputs = display.outputs();
        if (outputs.size() == 1) {
            panel.output(118, 48, outputs.get(0), display.chance(0));
            return;
        }
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(i % 2 == 0 ? 118 : 137, 48 - 19 * (i / 2), outputs.get(i), display.chance(i));
        }
    }
}
