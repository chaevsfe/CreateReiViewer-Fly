package dev.chaevsfe.createreiviewer.api.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ViewerCategory {
    public static final int DEFAULT_WIDTH = 177;

    private final Identifier id;
    private final Component title;
    private final List<ItemStack> icon;
    private final int width;
    private final int height;
    private final int overhangTop;
    private final List<ItemStack> workstations;
    private final ViewerLayout layout;
    private final boolean reiCategory;

    private ViewerCategory(Builder builder) {
        this.id = builder.id;
        this.title = Objects.requireNonNull(builder.title, "title");
        this.icon = List.copyOf(builder.icon);
        this.width = builder.width;
        this.height = builder.height;
        this.overhangTop = builder.overhangTop;
        this.workstations = List.copyOf(builder.workstations);
        this.layout = Objects.requireNonNull(builder.layout, "layout");
        this.reiCategory = builder.reiCategory;
        if (icon.isEmpty()) {
            throw new IllegalStateException("Category " + id + " has no icon");
        }
        if (height <= 0) {
            throw new IllegalStateException("Category " + id + " has no height");
        }
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    public Identifier id() {
        return id;
    }

    public Component title() {
        return title;
    }

    public List<ItemStack> icon() {
        return icon;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int overhangTop() {
        return overhangTop;
    }

    public List<ItemStack> workstations() {
        return workstations;
    }

    public ViewerLayout layout() {
        return layout;
    }

    public boolean reiCategory() {
        return reiCategory;
    }

    public static final class Builder {
        private final Identifier id;
        private Component title;
        private final List<ItemStack> icon = new ArrayList<>(2);
        private int width = DEFAULT_WIDTH;
        private int height;
        private int overhangTop;
        private final List<ItemStack> workstations = new ArrayList<>();
        private ViewerLayout layout;
        private boolean reiCategory = true;

        private Builder(Identifier id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder title(String translationKey) {
            return title(Component.translatable(translationKey));
        }

        public Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }

        public Builder icon(ItemLike item, ItemLike subItem) {
            return icon(new ItemStack(item), new ItemStack(subItem));
        }

        public Builder icon(ItemStack stack) {
            icon.clear();
            icon.add(stack.copy());
            return this;
        }

        public Builder icon(ItemStack stack, ItemStack subStack) {
            icon.clear();
            icon.add(stack.copy());
            icon.add(subStack.copy());
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder overhangTop(int overhangTop) {
            this.overhangTop = Math.max(0, overhangTop);
            return this;
        }

        public Builder workstations(ItemLike... items) {
            for (ItemLike item : items) {
                workstations.add(new ItemStack(item));
            }
            return this;
        }

        public Builder workstations(ItemStack... stacks) {
            for (ItemStack stack : stacks) {
                workstations.add(stack.copy());
            }
            return this;
        }

        public Builder layout(ViewerLayout layout) {
            this.layout = layout;
            return this;
        }

        public Builder reiCategory(boolean reiCategory) {
            this.reiCategory = reiCategory;
            return this;
        }

        public ViewerCategory build() {
            return new ViewerCategory(this);
        }
    }
}
