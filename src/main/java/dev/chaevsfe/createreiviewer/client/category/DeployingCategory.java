package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.DeployerRenderState;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiWidgets;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplays;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class DeployingCategory extends CreateReiCategory<CreateReiDisplay> {
    public DeployingCategory() {
        super("create.recipe.deploying");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.DEPLOYING;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(AllItems.DEPLOYER);
    }

    @Override
    protected int contentHeight() {
        return 70;
    }

    @Override
    protected int contentOverhangTop() {
        return 10;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        List<EntryIngredient> outputs = display.outputs();
        panel.texture(AllGuiTextures.JEI_SHADOW, 62, 57);
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 126, outputs.size() > 2 ? 10 : 29);
        panel.pip(75, -10, DeployerRenderState::new);

        panel.slot(51, 5, held(display));
        panel.slot(27, 51, display.inputs().get(1));
        if (outputs.size() == 1) {
            panel.output(132, 51, outputs.get(0), display.chance(0));
            return;
        }
        boolean centreLast = outputs.size() % 2 != 0;
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(
                CreateReiWidgets.basinOutputX(i, outputs.size() - 1, centreLast),
                51 - 19 * (i / 2),
                outputs.get(i),
                display.chance(i)
            );
        }
    }

    static EntryIngredient held(CreateReiDisplay display) {
        EntryIngredient held = display.inputs().get(0);
        if ((display.flags() & CreateReiDisplays.KEEP_HELD_ITEM) == 0) {
            return held;
        }
        return CreateReiWidgets.keepHeld(held);
    }
}
