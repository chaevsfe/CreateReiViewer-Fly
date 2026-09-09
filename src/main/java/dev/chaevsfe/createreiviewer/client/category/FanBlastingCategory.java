package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.world.item.Items;

public class FanBlastingCategory extends FanCategory {
    public FanBlastingCategory() {
        super("create.recipe.fan_blasting", Items.LAVA_BUCKET, () -> net.minecraft.world.level.material.Fluids.LAVA.defaultFluidState().createLegacyBlock(), AllGuiTextures.JEI_LIGHT);
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.FAN_BLASTING;
    }
}
