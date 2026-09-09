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

public class BlockCuttingCategory extends CreateReiCategory<CreateReiDisplay> {
    public BlockCuttingCategory() {
        super("create.recipe.block_cutting");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.BLOCK_CUTTING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_SAW, Items.STONE_BRICK_STAIRS);
    }

    @Override
    protected int contentHeight() {
        return 70;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 31, 6);
        panel.texture(AllGuiTextures.JEI_SHADOW, 16, 50);
        panel.pip(25, 26, SawRenderState::new);

        panel.slot(5, 5, display.inputs().get(0));
        List<EntryIngredient> outputs = display.outputs();
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(78 + 19 * (i % 5), 48 - 19 * (i / 5), outputs.get(i), 1.0f);
        }
    }
}
