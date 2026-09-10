package dev.chaevsfe.createreiviewer.client.widget;

import com.zurrtum.create.client.AllFluidConfigs;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

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
        return delegate.getTooltip(entry, context);
    }
}
