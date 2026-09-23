package dev.chaevsfe.createreiviewer.display;

import com.zurrtum.create.AllItemTags;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.content.fluids.potion.PotionFluidHandler;
import com.zurrtum.create.content.fluids.transfer.EmptyingRecipe;
import com.zurrtum.create.content.fluids.transfer.FillingRecipe;
import com.zurrtum.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.zurrtum.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.foundation.fluid.FluidHelper;
import com.zurrtum.create.foundation.fluid.FluidStackIngredient;
import com.zurrtum.create.infrastructure.fluids.FluidItemInventory;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import dev.architectury.utils.GameInstance;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.util.context.ContextMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CreateReiDerived {
    private static final int BUCKET = 81000;
    private static final int POTION_AMOUNT = 27000;
    private static final int BLOCK_CUTTING_SLOTS = 15;

    private RecipeMap recipes;
    private Level level;
    private ContextMap context;
    private List<ItemStack> crushingInputs;
    private Map<Ingredient, BlockCuttingGroup> blockCutting;

    public boolean millingIsAlsoCrushing(RecipeHolder<? extends Recipe<?>> holder) {
        ItemStack stack = firstStack(ingredientOf(holder.value()));
        if (stack.isEmpty()) {
            return false;
        }
        for (ItemStack crushable : crushingInputs()) {
            if (ItemStack.isSameItemSameComponents(crushable, stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean fanBlastingApplies(RecipeHolder<? extends SingleItemRecipe> holder, boolean checkBlasting) {
        if (!AllRecipeTypes.CAN_BE_AUTOMATED.test(holder)) {
            return false;
        }
        ItemStack stack = firstStack(holder.value().input());
        if (stack.isEmpty()) {
            return false;
        }
        SingleRecipeInput input = new SingleRecipeInput(stack);
        if (checkBlasting && coveredBy(RecipeType.BLASTING, input)) {
            return false;
        }
        return !coveredBy(RecipeType.SMOKING, input);
    }

    public CreateReiDisplay deployerFromSandpaper(RecipeHolder<SandPaperPolishingRecipe> holder) {
        SandPaperPolishingRecipe recipe = holder.value();
        ItemStackTemplate result = recipe.result();
        DeployerApplicationRecipe synthetic = new DeployerApplicationRecipe(
            List.of(new ProcessingOutput(result.item(), result.count(), result.components(), 1.0f)),
            true,
            recipe.ingredient(),
            sandpaper()
        );
        ResourceKey<Recipe<?>> id = ResourceKey.create(
            Registries.RECIPE,
            holder.id().identifier().withSuffix("_using_deployer")
        );
        return CreateReiDisplays.deploying(new RecipeHolder<>(id, synthetic));
    }

    public List<CreateReiDisplay> blockCutting(RecipeHolder<StonecutterRecipe> holder) {
        BlockCuttingGroup group = blockCuttingGroups().get(holder.value().input());
        if (group == null || !group.owner.equals(holder.id())) {
            return List.of();
        }
        return List.of(CreateReiDisplays.blockCutting(group.owner.identifier(), holder.value().input(), group.buckets()));
    }

    public List<CreateReiDisplay> mysteryConversions() {
        return List.of(
            CreateReiDisplays.mysteryConversion("to_blaze_burner", AllItems.EMPTY_BLAZE_BURNER, AllItems.BLAZE_BURNER),
            CreateReiDisplays.mysteryConversion("to_haunted_bell", AllItems.PECULIAR_BELL, AllItems.HAUNTED_BELL)
        );
    }

    public List<CreateReiDisplay> fluidTransfers() {
        List<CreateReiDisplay> displays = new ArrayList<>();
        List<FluidStack> fluids = sourceFluids();
        int index = 0;
        for (ItemStack stack : itemEntries()) {
            if (PotionFluidHandler.isPotionItem(stack)) {
                displays.add(CreateReiDisplays.draining(new RecipeHolder<>(
                    syntheticKey("jei/draining_" + index++),
                    new EmptyingRecipe(
                        new ItemStackTemplate(Items.GLASS_BOTTLE),
                        PotionFluidHandler.getFluidFromPotionItem(stack),
                        exactIngredient(stack)
                    )
                )));
                PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                displays.add(CreateReiDisplays.spoutFilling(new RecipeHolder<>(
                    syntheticKey("jei/filling_" + index++),
                    new FillingRecipe(
                        ItemStackTemplate.fromNonEmptyStack(stack),
                        Ingredient.of(Items.GLASS_BOTTLE),
                        PotionFluidHandler.getFluidIngredientFromPotion(
                            contents,
                            PotionFluidHandler.bottleTypeFromItem(stack.getItem()),
                            POTION_AMOUNT
                        )
                    )
                )));
                continue;
            }
            displays.addAll(containerTransfers(stack, fluids));
        }
        return displays;
    }

    private List<CreateReiDisplay> containerTransfers(ItemStack stack, List<FluidStack> fluids) {
        List<CreateReiDisplay> displays = new ArrayList<>();
        try (FluidItemInventory inventory = FluidHelper.getFluidInventory(stack.copy())) {
            if (inventory == null) {
                return displays;
            }
            FluidStack held = inventory.extractAny(BUCKET);
            if (!held.isEmpty()) {
                displays.add(CreateReiDisplays.draining(new RecipeHolder<>(
                    transferKey(stack.getItem(), held, "_to_"),
                    new EmptyingRecipe(
                        ItemStackTemplate.fromNonEmptyStack(inventory.getContainer()),
                        held,
                        exactIngredient(stack)
                    )
                )));
                return displays;
            }
            if (inventory.size() != 1 || !inventory.getStack(0).isEmpty()) {
                return displays;
            }
            for (FluidStack fluid : fluids) {
                try (FluidItemInventory target = FluidHelper.getFluidInventory(stack.copy())) {
                    if (target == null || target.insert(fluid, BUCKET) <= 0) {
                        continue;
                    }
                    ItemStack filled = target.getContainer();
                    if (filled.isEmpty() || !filled.is(stack.getItem())) {
                        continue;
                    }
                    displays.add(CreateReiDisplays.spoutFilling(new RecipeHolder<>(
                        transferKey(stack.getItem(), fluid, "_from_"),
                        new FillingRecipe(
                            ItemStackTemplate.fromNonEmptyStack(filled),
                            exactIngredient(stack),
                            new FluidStackIngredient(fluid.getFluid(), fluid.getComponentChanges(), BUCKET)
                        )
                    )));
                }
            }
        }
        return displays;
    }

    private boolean coveredBy(RecipeType<? extends Recipe<SingleRecipeInput>> type, SingleRecipeInput input) {
        for (RecipeHolder<? extends Recipe<SingleRecipeInput>> holder : recipeMap().byType(type)) {
            if (holder.value().matches(input, level()) && AllRecipeTypes.CAN_BE_AUTOMATED.test(holder)) {
                return true;
            }
        }
        return false;
    }

    private List<ItemStack> crushingInputs() {
        if (crushingInputs == null) {
            crushingInputs = new ArrayList<>();
            for (RecipeHolder<?> holder : recipeMap().byType(AllRecipeTypes.CRUSHING)) {
                ItemStack stack = firstStack(ingredientOf(holder.value()));
                if (!stack.isEmpty()) {
                    crushingInputs.add(stack);
                }
            }
        }
        return crushingInputs;
    }

    private Map<Ingredient, BlockCuttingGroup> blockCuttingGroups() {
        if (blockCutting == null) {
            blockCutting = new LinkedHashMap<>();
            for (RecipeHolder<StonecutterRecipe> holder : recipeMap().byType(RecipeType.STONECUTTING)) {
                if (AllRecipeTypes.shouldIgnoreInAutomation(holder)) {
                    continue;
                }
                blockCutting
                    .computeIfAbsent(holder.value().input(), key -> new BlockCuttingGroup(holder.id()))
                    .results.add(holder.value().result());
            }
        }
        return blockCutting;
    }

    private List<ItemStack> itemEntries() {
        List<ItemStack> stacks = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = item.getDefaultInstance();
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    private List<FluidStack> sourceFluids() {
        List<FluidStack> fluids = new ArrayList<>();
        for (net.minecraft.world.level.material.Fluid fluid : BuiltInRegistries.FLUID) {
            if (fluid.defaultFluidState().isSource()) {
                fluids.add(new FluidStack(fluid, BUCKET));
            }
        }
        return fluids;
    }

    private static Ingredient exactIngredient(ItemStack stack) {
        return stack.getComponentsPatch().isEmpty() ? Ingredient.of(stack.getItem()) : DefaultCustomIngredients.components(stack);
    }

    private static ResourceKey<Recipe<?>> syntheticKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("create", path));
    }

    private static ResourceKey<Recipe<?>> transferKey(Item item, FluidStack fluid, String infix) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        Identifier fluidId = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
        String path = itemId.getNamespace() + "_" + itemId.getPath() + infix + fluidId.getNamespace() + "_" + fluidId.getPath();
        return syntheticKey(path);
    }

    private static Ingredient sandpaper() {
        List<Holder<Item>> holders = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(AllItemTags.SANDPAPER)) {
            holders.add(holder);
        }
        if (holders.isEmpty()) {
            return Ingredient.of(AllItems.SAND_PAPER);
        }
        return Ingredient.of(HolderSet.direct(holders));
    }

    private static Ingredient ingredientOf(Recipe<?> recipe) {
        if (recipe instanceof com.zurrtum.create.foundation.recipe.CreateSingleStackRollableRecipe rollable) {
            return rollable.ingredient();
        }
        return Ingredient.of(Items.AIR);
    }

    private ItemStack firstStack(Ingredient ingredient) {
        return ingredient.display().resolveForFirstStack(context());
    }

    private RecipeMap recipeMap() {
        if (recipes == null) {
            recipes = RecipeMap.create(server().getRecipeManager().getRecipes());
        }
        return recipes;
    }

    private Level level() {
        if (level == null) {
            level = server().overworld();
        }
        return level;
    }

    private ContextMap context() {
        if (context == null) {
            context = SlotDisplayContext.fromLevel(level());
        }
        return context;
    }

    private static MinecraftServer server() {
        MinecraftServer server = GameInstance.getServer();
        if (server == null) {
            CreateReiViewer.LOGGER.warn("No server while deriving Create displays; derived categories will be empty");
            throw new IllegalStateException("no server");
        }
        return server;
    }

    private static final class BlockCuttingGroup {
        private final ResourceKey<Recipe<?>> owner;
        private final List<ItemStackTemplate> results = new ArrayList<>();

        private BlockCuttingGroup(ResourceKey<Recipe<?>> owner) {
            this.owner = owner;
        }

        private List<List<ItemStackTemplate>> buckets() {
            if (results.size() <= BLOCK_CUTTING_SLOTS) {
                List<List<ItemStackTemplate>> buckets = new ArrayList<>(results.size());
                for (ItemStackTemplate result : results) {
                    buckets.add(List.of(result));
                }
                return buckets;
            }
            List<List<ItemStackTemplate>> buckets = new ArrayList<>(BLOCK_CUTTING_SLOTS);
            for (int i = 0; i < BLOCK_CUTTING_SLOTS; i++) {
                buckets.add(new ArrayList<>(2));
            }
            for (int i = 0; i < results.size(); i++) {
                buckets.get(i % BLOCK_CUTTING_SLOTS).add(results.get(i));
            }
            return buckets;
        }
    }
}
