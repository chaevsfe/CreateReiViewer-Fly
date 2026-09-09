package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.CrafterRenderState;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiGridDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MechanicalCraftingCategory extends CreateReiCategory<CreateReiGridDisplay> {
    public MechanicalCraftingCategory() {
        super("create.recipe.mechanical_crafting");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiGridDisplay> getCategoryIdentifier() {
        return CreateReiCategories.MECHANICAL_CRAFTING;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(AllItems.MECHANICAL_CRAFTER);
    }

    @Override
    protected int contentHeight() {
        return 107;
    }

    @Override
    protected void build(CreateReiGridDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 128, 59);
        panel.texture(AllGuiTextures.JEI_SHADOW, 113, 38);
        panel.pip(124, 18, CrafterRenderState::new);

        int width = display.width();
        int height = display.height();
        int xBase = width < 5 ? 7 + 19 * (5 - width) / 2 : 7;
        int yBase = height < 5 ? 7 + 19 * (5 - height) / 2 : 7;

        List<EntryIngredient> inputs = display.inputs();
        int filled = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int index = row * width + column;
                if (index >= inputs.size()) {
                    continue;
                }
                EntryIngredient slot = inputs.get(index);
                if (slot.isEmpty()) {
                    continue;
                }
                filled++;
                panel.slot(xBase + 19 * column, yBase + 19 * row, slot);
            }
        }
        panel.output(133, 80, display.outputs().get(0), 1.0f);
        panel.text(Component.literal(String.valueOf(filled)), 142, 39, -1);
    }
}
