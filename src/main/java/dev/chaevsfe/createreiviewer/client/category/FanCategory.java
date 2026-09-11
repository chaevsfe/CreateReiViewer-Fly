package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.FanRenderState;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

public abstract class FanCategory extends CreateReiCategory<CreateReiDisplay> {
    private final ItemLike subIcon;
    private final Supplier<BlockState> atmosphere;
    private final AllGuiTextures glow;

    protected FanCategory(String titleKey, ItemLike subIcon, Supplier<BlockState> atmosphere, AllGuiTextures glow) {
        super(titleKey);
        this.subIcon = subIcon;
        this.atmosphere = atmosphere;
        this.glow = glow;
    }

    @Override
    public Renderer getIcon() {
        return subIcon == null ? new OneItemRenderer(AllItems.PROPELLER) : new TwoItemRenderer(AllItems.PROPELLER, subIcon);
    }

    @Override
    protected int contentHeight() {
        return 72;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        List<EntryIngredient> outputs = display.outputs();
        int count = outputs.size();
        int shift = 1 - Math.min(3, count);

        panel.texture(AllGuiTextures.JEI_SHADOW, 46, 27);
        panel.texture(glow, 65, 39);
        panel.texture(AllGuiTextures.JEI_LONG_ARROW, 54, 51);
        BlockState state = atmosphere.get();
        panel.pip(56, 4, (pose, x, y) -> new FanRenderState(pose, x, y, state));

        if (count == 1) {
            panel.slot(21, 48, display.inputs().get(0));
            panel.output(141, 48, outputs.get(0), display.chance(0));
            return;
        }
        panel.slot(21 + 5 * shift, 48, display.inputs().get(0));
        int xBase = 141 + 9 * shift;
        int yBase = count > 9 ? 57 : 48;
        for (int i = 0; i < count; i++) {
            panel.output(xBase + 19 * (i % 3), yBase - 19 * (i / 3), outputs.get(i), display.chance(i));
        }
    }
}
