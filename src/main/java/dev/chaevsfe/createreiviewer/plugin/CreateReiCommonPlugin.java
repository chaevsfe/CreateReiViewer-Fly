package dev.chaevsfe.createreiviewer.plugin;

import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.zurrtum.create.content.fluids.transfer.EmptyingRecipe;
import com.zurrtum.create.content.fluids.transfer.FillingRecipe;
import com.zurrtum.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.zurrtum.create.content.kinetics.crusher.CrushingRecipe;
import com.zurrtum.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.zurrtum.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.zurrtum.create.content.kinetics.fan.processing.HauntingRecipe;
import com.zurrtum.create.content.kinetics.fan.processing.SplashingRecipe;
import com.zurrtum.create.content.kinetics.millstone.MillingRecipe;
import com.zurrtum.create.content.kinetics.mixer.CompactingRecipe;
import com.zurrtum.create.content.kinetics.mixer.MixingRecipe;
import com.zurrtum.create.content.kinetics.mixer.PotionRecipe;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.zurrtum.create.content.kinetics.press.PressingRecipe;
import com.zurrtum.create.content.kinetics.saw.CuttingRecipe;
import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyRecipe;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDerived;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplays;
import dev.chaevsfe.createreiviewer.display.CreateReiGridDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiSequenceDisplay;
import io.netty.buffer.Unpooled;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.List;

public class CreateReiCommonPlugin implements REICommonPlugin {
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(CreateReiDisplay.serializerId(), CreateReiDisplay.SERIALIZER);
        registry.register(CreateReiGridDisplay.serializerId(), CreateReiGridDisplay.SERIALIZER);
        registry.register(CreateReiSequenceDisplay.serializerId(), CreateReiSequenceDisplay.SERIALIZER);
        CreateReiViewer.LOGGER.info("Registered 3 display serializers");
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        CreateReiDerived derived = new CreateReiDerived();

        registry.<MixingRecipe, CreateReiDisplay>beginRecipeFiller(MixingRecipe.class)
            .filterType(AllRecipeTypes.MIXING)
            .fill(CreateReiDisplays::mixing);
        registry.<CompactingRecipe, CreateReiDisplay>beginRecipeFiller(CompactingRecipe.class)
            .filterType(AllRecipeTypes.COMPACTING)
            .fill(CreateReiDisplays::packing);
        registry.<PotionRecipe, CreateReiDisplay>beginRecipeFiller(PotionRecipe.class)
            .filterType(AllRecipeTypes.POTION)
            .fill(CreateReiDisplays::potion);
        registry.<PressingRecipe, CreateReiDisplay>beginRecipeFiller(PressingRecipe.class)
            .filterType(AllRecipeTypes.PRESSING)
            .fill(CreateReiDisplays::pressing);
        registry.<MillingRecipe, CreateReiDisplay>beginRecipeFiller(MillingRecipe.class)
            .filterType(AllRecipeTypes.MILLING)
            .fill(CreateReiDisplays::milling);
        registry.<CuttingRecipe, CreateReiDisplay>beginRecipeFiller(CuttingRecipe.class)
            .filterType(AllRecipeTypes.CUTTING)
            .fill(CreateReiDisplays::sawing);
        registry.<CrushingRecipe, CreateReiDisplay>beginRecipeFiller(CrushingRecipe.class)
            .filterType(AllRecipeTypes.CRUSHING)
            .fill(CreateReiDisplays::crushing);
        registry.<MillingRecipe, CreateReiDisplay>beginRecipeFiller(MillingRecipe.class)
            .filterType(AllRecipeTypes.MILLING)
            .filter(holder -> !derived.millingIsAlsoCrushing(holder))
            .fill(CreateReiDisplays::crushing);
        registry.<SplashingRecipe, CreateReiDisplay>beginRecipeFiller(SplashingRecipe.class)
            .filterType(AllRecipeTypes.SPLASHING)
            .fill(CreateReiDisplays::fanWashing);
        registry.<HauntingRecipe, CreateReiDisplay>beginRecipeFiller(HauntingRecipe.class)
            .filterType(AllRecipeTypes.HAUNTING)
            .fill(CreateReiDisplays::fanHaunting);
        registry.<SmokingRecipe, CreateReiDisplay>beginRecipeFiller(SmokingRecipe.class)
            .filterType(RecipeType.SMOKING)
            .filter(AllRecipeTypes.CAN_BE_AUTOMATED::test)
            .fill(CreateReiDisplays::fanSmoking);
        registry.<BlastingRecipe, CreateReiDisplay>beginRecipeFiller(BlastingRecipe.class)
            .filterType(RecipeType.BLASTING)
            .filter(holder -> derived.fanBlastingApplies(holder, false))
            .fill(CreateReiDisplays::fanBlasting);
        registry.<SmeltingRecipe, CreateReiDisplay>beginRecipeFiller(SmeltingRecipe.class)
            .filterType(RecipeType.SMELTING)
            .filter(holder -> derived.fanBlastingApplies(holder, true))
            .fill(CreateReiDisplays::fanBlasting);
        registry.<SandPaperPolishingRecipe, CreateReiDisplay>beginRecipeFiller(SandPaperPolishingRecipe.class)
            .filterType(AllRecipeTypes.SANDPAPER_POLISHING)
            .fill(CreateReiDisplays::sandpaperPolishing);
        registry.<SandPaperPolishingRecipe, CreateReiDisplay>beginRecipeFiller(SandPaperPolishingRecipe.class)
            .filterType(AllRecipeTypes.SANDPAPER_POLISHING)
            .fill(derived::deployerFromSandpaper);
        registry.<ManualApplicationRecipe, CreateReiDisplay>beginRecipeFiller(ManualApplicationRecipe.class)
            .filterType(AllRecipeTypes.ITEM_APPLICATION)
            .fill(CreateReiDisplays::itemApplication);
        registry.<ManualApplicationRecipe, CreateReiDisplay>beginRecipeFiller(ManualApplicationRecipe.class)
            .filterType(AllRecipeTypes.ITEM_APPLICATION)
            .fill(CreateReiDisplays::deploying);
        registry.<DeployerApplicationRecipe, CreateReiDisplay>beginRecipeFiller(DeployerApplicationRecipe.class)
            .filterType(AllRecipeTypes.DEPLOYING)
            .fill(CreateReiDisplays::deploying);
        registry.<EmptyingRecipe, CreateReiDisplay>beginRecipeFiller(EmptyingRecipe.class)
            .filterType(AllRecipeTypes.EMPTYING)
            .fill(CreateReiDisplays::draining);
        registry.<FillingRecipe, CreateReiDisplay>beginRecipeFiller(FillingRecipe.class)
            .filterType(AllRecipeTypes.FILLING)
            .fill(CreateReiDisplays::spoutFilling);
        registry.<MechanicalCraftingRecipe, CreateReiGridDisplay>beginRecipeFiller(MechanicalCraftingRecipe.class)
            .filterType(AllRecipeTypes.MECHANICAL_CRAFTING)
            .fill(CreateReiDisplays::mechanicalCrafting);
        registry.<SequencedAssemblyRecipe, CreateReiSequenceDisplay>beginRecipeFiller(SequencedAssemblyRecipe.class)
            .filterType(AllRecipeTypes.SEQUENCED_ASSEMBLY)
            .fill(CreateReiDisplays::sequencedAssembly);
        registry.<CraftingRecipe, CreateReiDisplay>beginRecipeFiller(CraftingRecipe.class)
            .filterType(RecipeType.CRAFTING)
            .filter(CreateReiCommonPlugin::isAutomaticPacking)
            .fill(CreateReiDisplays::automaticPacking);
        registry.<ShapelessRecipe, CreateReiDisplay>beginRecipeFiller(ShapelessRecipe.class)
            .filterType(RecipeType.CRAFTING)
            .filter(CreateReiCommonPlugin::isAutomaticShapeless)
            .fill(CreateReiDisplays::automaticShapeless);
        registry.<StonecutterRecipe, CreateReiDisplay>beginRecipeFiller(StonecutterRecipe.class)
            .filterType(RecipeType.STONECUTTING)
            .fillMultiple(derived::blockCutting);

        List<CreateReiDisplay> mysteries = derived.mysteryConversions();
        for (CreateReiDisplay display : mysteries) {
            registry.add(display);
        }
        List<CreateReiDisplay> transfers = derived.fluidTransfers();
        int derivedDraining = 0;
        for (CreateReiDisplay display : transfers) {
            registry.add(display);
            if (display.category().equals(CreateReiDisplays.identifierOf(CreateReiCategories.DRAINING))) {
                derivedDraining++;
            }
        }
        CreateReiViewer.LOGGER.info(
            "Derived without recipe files: {} mystery conversions, {} drainings, {} spout fillings",
            mysteries.size(),
            derivedDraining,
            transfers.size() - derivedDraining
        );
        CreateReiViewer.LOGGER.info("Recipe fillers registered for {} categories", CreateReiCategories.ALL.size());
    }

    private static boolean isAutomaticPacking(net.minecraft.world.item.crafting.RecipeHolder<CraftingRecipe> holder) {
        CraftingRecipe recipe = holder.value();
        if (!MechanicalPressBlockEntity.canCompress(recipe) || AllRecipeTypes.shouldIgnoreInAutomation(holder)) {
            return false;
        }
        return recipe instanceof ShapelessRecipe || recipe instanceof net.minecraft.world.item.crafting.ShapedRecipe;
    }

    private static boolean isAutomaticShapeless(net.minecraft.world.item.crafting.RecipeHolder<ShapelessRecipe> holder) {
        ShapelessRecipe recipe = holder.value();
        return !MechanicalPressBlockEntity.canCompress(recipe)
            && !AllRecipeTypes.shouldIgnoreInAutomation(holder)
            && recipe.ingredients.size() != 1;
    }

    @Override
    public void postStage(PluginManager<REICommonPlugin> manager, ReloadStage stage) {
        if (stage != ReloadStage.END || FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
            return;
        }
        ServerDisplayRegistry registry = manager.get(ServerDisplayRegistry.class);
        if (registry == null) {
            CreateReiViewer.LOGGER.warn("No ServerDisplayRegistry after reload; no Create displays were built");
            return;
        }
        int total = 0;
        int empty = 0;
        for (CategoryIdentifier<? extends CreateReiDisplay> category : CreateReiCategories.ALL) {
            List<? extends Display> displays = registry.get(category);
            total += displays.size();
            CreateReiViewer.LOGGER.info("Registered {} displays for category {}", displays.size(), category.getIdentifier());
            if (displays.isEmpty()) {
                empty++;
                CreateReiViewer.LOGGER.warn("Category {} built ZERO displays", category.getIdentifier());
            }
            reportWireRoundTrip(category, displays);
        }
        CreateReiViewer.LOGGER.info("Create display total: {} across {} categories, {} empty",
            total, CreateReiCategories.ALL.size(), empty);
    }

    private static void reportWireRoundTrip(CategoryIdentifier<?> category, List<? extends Display> displays) {
        if (displays.isEmpty()) {
            return;
        }
        int checked = 0;
        int mismatched = 0;
        for (Display display : displays) {
            try {
                byte[] first = encode(display);
                RegistryFriendlyByteBuf in = buffer();
                in.writeBytes(first);
                Display decoded = decode(display, in);
                byte[] second = encode(decoded);
                checked++;
                if (!java.util.Arrays.equals(first, second)) {
                    mismatched++;
                    CreateReiViewer.LOGGER.warn("Wire round trip changed display {}", locationOf(display));
                }
            } catch (Exception exception) {
                mismatched++;
                CreateReiViewer.LOGGER.warn("Wire round trip failed for display {}", locationOf(display), exception);
            }
        }
        CreateReiViewer.LOGGER.info("Wire round trip for category {}: {} checked, {} mismatched",
            category.getIdentifier(), checked, mismatched);
    }

    private static String locationOf(Display display) {
        return display.getDisplayLocation().map(Object::toString).orElse("<unknown>");
    }

    private static byte[] encode(Display display) {
        RegistryFriendlyByteBuf buf = buffer();
        if (display instanceof CreateReiGridDisplay grid) {
            CreateReiGridDisplay.STREAM_CODEC.encode(buf, grid);
        } else if (display instanceof CreateReiSequenceDisplay sequence) {
            CreateReiSequenceDisplay.STREAM_CODEC.encode(buf, sequence);
        } else {
            CreateReiDisplay.STREAM_CODEC.encode(buf, (CreateReiDisplay) display);
        }
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

    private static Display decode(Display like, RegistryFriendlyByteBuf buf) {
        if (like instanceof CreateReiGridDisplay) {
            return CreateReiGridDisplay.STREAM_CODEC.decode(buf);
        }
        if (like instanceof CreateReiSequenceDisplay) {
            return CreateReiSequenceDisplay.STREAM_CODEC.decode(buf);
        }
        return CreateReiDisplay.STREAM_CODEC.decode(buf);
    }

    private static RegistryFriendlyByteBuf buffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), me.shedaniel.rei.api.common.display.basic.BasicDisplay.registryAccess());
    }
}
