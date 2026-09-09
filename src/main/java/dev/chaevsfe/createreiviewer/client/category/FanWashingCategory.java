package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.world.item.Items;

public class FanWashingCategory extends FanCategory {
    public FanWashingCategory() {
        super("create.recipe.fan_washing", Items.WATER_BUCKET, () -> net.minecraft.world.level.material.Fluids.WATER.defaultFluidState().createLegacyBlock(), AllGuiTextures.JEI_SHADOW);
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.FAN_WASHING;
    }
}
