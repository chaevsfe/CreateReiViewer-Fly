package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.world.item.Items;

public class FanSmokingCategory extends FanCategory {
    public FanSmokingCategory() {
        super("create.recipe.fan_smoking", Items.CAMPFIRE, () -> net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState(), AllGuiTextures.JEI_LIGHT);
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.FAN_SMOKING;
    }
}
