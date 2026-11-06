package dev.chaevsfe.createreiviewer.client.widget;

import com.zurrtum.create.AllDataComponents;
import com.zurrtum.create.AllFluids;
import com.zurrtum.create.client.AllFluidConfigs;
import com.zurrtum.create.content.fluids.potion.PotionFluidHandler;
import com.zurrtum.create.infrastructure.component.BottleType;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.entry.renderer.EntryRendererProvider;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;

public final class FluidEntryRenderer implements EntryRenderer<FluidStack> {
    private static final int OPAQUE = 0xFF000000;

    private final EntryRenderer<FluidStack> delegate;

    private FluidEntryRenderer(EntryRenderer<FluidStack> delegate) {
        this.delegate = delegate;
    }

    public static EntryRendererProvider<FluidStack> provider() {
        return new EntryRendererProvider<>() {
            private EntryRenderer<FluidStack> previous;
            private FluidEntryRenderer wrapper;

            @Override
            public EntryRenderer<FluidStack> provide(EntryStack<FluidStack> entry, EntryRenderer<FluidStack> last) {
                if (wrapper == null || previous != last) {
                    previous = last;
                    wrapper = new FluidEntryRenderer(last);
                }
                return wrapper;
            }
        };
    }

    @Override
    public void render(EntryStack<FluidStack> entry, GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        FluidStack stack = entry.getValue();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Fluid fluid = stack.getFluid();
        FluidState fluidState = fluid.defaultFluidState();
        FluidModel model = minecraft.getModelManager().getFluidStateModelSet().get(fluidState);
        TextureAtlasSprite sprite = model.stillMaterial().sprite();
        if (sprite == null) {
            return;
        }
        ClientLevel level = minecraft.level;
        BlockPos pos = minecraft.player == null ? BlockPos.ZERO : minecraft.player.blockPosition();
        int tint = AllFluidConfigs.getTint(level, pos, fluidState.createLegacyBlock(), model, fluid, stack.getPatch()) | OPAQUE;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, bounds.x, bounds.y, bounds.width, bounds.height, tint);
    }

    @Override
    public Tooltip getTooltip(EntryStack<FluidStack> entry, TooltipContext context) {
        Tooltip tooltip = delegate.getTooltip(entry, context);
        FluidStack stack = entry.getValue();
        if (tooltip != null && stack != null && stack.getFluid() == AllFluids.POTION) {
            describePotion(tooltip, stack.getComponents(), context.vanillaContext().tickRate());
        }
        return tooltip;
    }

    private static void describePotion(Tooltip tooltip, DataComponentMap components, float tickRate) {
        PotionContents potion = components.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        BottleType bottle = components.getOrDefault(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, BottleType.REGULAR);
        Item bottleItem = PotionFluidHandler.itemFromBottleType(bottle);
        float durationScale = components.getOrDefault(
            DataComponents.POTION_DURATION_SCALE,
            bottleItem.components().getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0f)
        );

        List<Tooltip.Entry> description = new ArrayList<>();
        description.add(Tooltip.entry(potion.getName(bottleItem.getDescriptionId() + ".effect.")));
        PotionContents.addPotionTooltip(potion.getAllEffects(), line -> description.add(Tooltip.entry(line)), durationScale, tickRate);

        List<Tooltip.Entry> entries = tooltip.entries();
        if (!entries.isEmpty() && entries.getFirst().isText()) {
            entries.removeFirst();
        }
        entries.addAll(0, description);
    }
}
