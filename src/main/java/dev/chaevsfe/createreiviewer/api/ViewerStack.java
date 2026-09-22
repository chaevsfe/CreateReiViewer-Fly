package dev.chaevsfe.createreiviewer.api;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

public sealed interface ViewerStack permits ViewerStack.OfItem, ViewerStack.OfFluid {
    static ViewerStack item(ItemStack stack) {
        return new OfItem(stack.copy());
    }

    static ViewerStack item(ItemLike item) {
        return new OfItem(new ItemStack(item));
    }

    static ViewerStack fluid(Fluid fluid, long droplets) {
        return new OfFluid(fluid, droplets, DataComponentPatch.EMPTY);
    }

    static ViewerStack fluid(Fluid fluid, long droplets, DataComponentPatch components) {
        return new OfFluid(fluid, droplets, components);
    }

    static ViewerStack fluid(com.zurrtum.create.infrastructure.fluids.FluidStack stack) {
        return new OfFluid(stack.getFluid(), stack.getAmount(), stack.getComponentChanges());
    }

    boolean isEmpty();

    record OfItem(ItemStack stack) implements ViewerStack {
        @Override
        public boolean isEmpty() {
            return stack.isEmpty();
        }
    }

    record OfFluid(Fluid fluid, long amount, DataComponentPatch components) implements ViewerStack {
        public OfFluid {
            components = components == null ? DataComponentPatch.EMPTY : components;
        }

        @Override
        public boolean isEmpty() {
            return amount <= 0 || fluid.defaultFluidState().isEmpty();
        }
    }
}
