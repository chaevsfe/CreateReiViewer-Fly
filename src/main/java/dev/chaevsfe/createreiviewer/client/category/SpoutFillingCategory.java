package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.SpoutRenderState;
import dev.architectury.fluid.FluidStack;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.world.item.Items;

import java.util.concurrent.atomic.AtomicInteger;

public class SpoutFillingCategory extends CreateReiCategory<CreateReiDisplay> {
    private static final int SPOUT_VARIANTS = 3;

    private final AtomicInteger spoutId = new AtomicInteger();

    public SpoutFillingCategory() {
        super("create.recipe.spout_filling");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.SPOUT_FILLING;
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.SPOUT, Items.WATER_BUCKET);
    }

    @Override
    protected int contentHeight() {
        return 70;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_SHADOW, 62, 57);
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 126, 29);

        FluidStack fluid = DrainingCategory.firstFluid(display.inputs().get(1));
        if (fluid != null) {
            int id = spoutId.getAndIncrement();
            if (id >= SPOUT_VARIANTS) {
                spoutId.set(0);
                id = 0;
            }
            int variant = id;
            panel.pip(75, 1, (pose, x, y) -> new SpoutRenderState(variant, pose, fluid.getFluid(), fluid.getPatch(), x, y, 0));
        }
        panel.slot(27, 51, display.inputs().get(0));
        panel.slot(27, 32, display.inputs().get(1));
        panel.output(132, 51, display.outputs().get(0), 1.0f);
    }
}
