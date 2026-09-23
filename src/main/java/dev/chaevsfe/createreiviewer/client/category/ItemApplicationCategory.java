package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiWidgets;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemApplicationCategory extends CreateReiCategory<CreateReiDisplay> {
    private static final int TARGET_SIZE = 27;

    public ItemApplicationCategory() {
        super("create.recipe.item_application");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.ITEM_APPLICATION;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(AllItems.BRASS_HAND);
    }

    @Override
    protected int contentHeight() {
        return 63;
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 79, 15);

        EntryIngredient target = display.inputs().get(1);
        ItemStack fallback = targetStack(target);

        panel.slot(51, 5, DeployingCategory.held(display));
        Slot targetSlot = panel.slot(27, 38, target);

        if (!fallback.isEmpty()) {
            panel.texture(AllGuiTextures.JEI_SHADOW, 67, 52);
            panel.itemPip(79, 34, TARGET_SIZE, () -> currentStack(targetSlot, fallback));
        }

        List<EntryIngredient> outputs = display.outputs();
        if (outputs.size() == 1) {
            panel.output(132, 38, outputs.get(0), display.chance(0));
            return;
        }
        boolean centreLast = outputs.size() % 2 != 0;
        for (int i = 0; i < outputs.size(); i++) {
            panel.output(
                CreateReiWidgets.basinOutputX(i, outputs.size() - 1, centreLast),
                38 - 19 * (i / 2),
                outputs.get(i),
                display.chance(i)
            );
        }
    }

    private static ItemStack currentStack(Slot slot, ItemStack fallback) {
        EntryStack<?> current = slot.getCurrentEntry();
        if (current != null && current.getValue() instanceof ItemStack itemStack && !itemStack.isEmpty()) {
            return itemStack;
        }
        return fallback;
    }

    private static ItemStack targetStack(EntryIngredient target) {
        for (EntryStack<?> stack : target) {
            if (stack.getValue() instanceof ItemStack itemStack && !itemStack.isEmpty()) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }
}
