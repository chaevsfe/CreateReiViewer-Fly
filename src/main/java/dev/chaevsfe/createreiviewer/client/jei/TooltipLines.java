package dev.chaevsfe.createreiviewer.client.jei;

import com.mojang.datafixers.util.Either;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

public record TooltipLines(List<Component> lines) implements IRecipeSlotRichTooltipCallback {
    @Override
    public void onRichTooltip(IRecipeSlotView view, ITooltipBuilder tooltip) {
        List<Either<FormattedText, TooltipComponent>> existing = tooltip.getLines();
        int at = Math.min(1, existing.size());
        for (Component line : lines) {
            existing.add(at++, Either.left(line));
        }
    }
}
