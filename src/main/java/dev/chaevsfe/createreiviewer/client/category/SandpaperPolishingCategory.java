package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.SandPaperRenderState;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.world.item.ItemStack;

public class SandpaperPolishingCategory extends CreateReiCategory<CreateReiDisplay> {
    public SandpaperPolishingCategory() {
        super("create.recipe.sandpaper_polishing");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.SANDPAPER_POLISHING;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(AllItems.SAND_PAPER);
    }

    @Override
    protected int contentHeight() {
        return 55;
    }

    @Override
    protected int contentOverhangTop() {
        return 2;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_SHADOW, 61, 21);
        panel.texture(AllGuiTextures.JEI_LONG_ARROW, 52, 32);

        ItemStack polished = firstStack(display);
        if (!polished.isEmpty()) {
            panel.pip(74, -2, (pose, x, y) -> new SandPaperRenderState(pose, polished, x, y));
        }
        panel.slot(27, 29, display.inputs().get(0));
        panel.output(132, 29, display.outputs().get(0), display.chance(0));
    }

    private static ItemStack firstStack(CreateReiDisplay display) {
        for (EntryStack<?> stack : display.inputs().get(0)) {
            if (stack.getValue() instanceof ItemStack itemStack && !itemStack.isEmpty()) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }
}
