# Create Fly: Recipe Viewer — addon API

Frozen at `0.1.0-fly.26.2-alpha.1`. Anything documented here is what an addon mod may
call; anything else in the jar is internal and may change without notice.

An addon supplies **displays** (server side) and **categories** (client side) for its own
recipe types. It never registers a display serializer: the viewer's generic
`CreateReiDisplay` serializer already carries every field an addon needs, and the viewer
registers it. The category identifier lives inside the display, so an addon's categories
use the addon's own namespace (`createdieselgenerators:hammering`, `powergrid:magnetizing`)
while still travelling over the viewer's one serializer.

## 1. Depend on the viewer without depending on the viewer

Both plugin entrypoints must be declared in `fabric.mod.json` and both must be inert when
the viewer or REI is missing.

```json
"entrypoints": {
  "rei_common": ["com.example.compat.rei.ExampleReiCommonPlugin"],
  "rei_client": ["com.example.compat.rei.ExampleReiClientPlugin"]
},
"suggests": { "createreiviewer": "*", "roughlyenoughitems": "*" }
```

Fabric only constructs an entrypoint when something asks for its key, and only REI asks
for `rei_common` / `rei_client`, so REI's absence already makes the classes unreachable.
The viewer's absence does not, so each plugin method guards:

```java
public class ExampleReiCommonPlugin implements REICommonPlugin {
    @Override public void registerDisplays(ServerDisplayRegistry registry) {
        if (!ExampleReiSupport.available()) return;   // FabricLoader.isModLoaded checks
        ExampleReiDisplays.register(registry);        // first class that touches the viewer
    }
}
```

**The guard class must not name a viewer type anywhere** — not in a field, a method
descriptor, or a lambda body. The bytecode verifier resolves types it must check
assignability for at link time, and a lambda's synthetic body method is verified with the
class that declares it (PROJECT.md § Lessons). Keep the guard free of viewer types and put
everything else in a separate class reached by a plain `invokestatic`; that reference is
resolved only when the call actually executes, which is after the guard has passed.

Build files take the viewer as `compileOnly` off the neighbouring repo, the way B&B takes
Steam 'n' Rails:

```kotlin
compileOnly(files(fileTree("../../create-rei/CreateReiViewer-Fly/build/libs") {
    include("CreateReiViewer-*.jar"); exclude("*-sources.jar")
}.files.maxByOrNull { it.lastModified() } ?: error("No viewer jar")))
compileOnly("maven.modrinth:rei:26.2.820+fabric")
compileOnly("maven.modrinth:architectury-api:21.0.7+fabric")
compileOnly("me.shedaniel.cloth:basic-math:0.6.1")
```

## 2. Server side — `dev.chaevsfe.createreiviewer.api`

### `CreateReiApi`

| member | meaning |
| --- | --- |
| `isAvailable()` | viewer **and** REI both loaded. Not usable from a guard class (it is itself a viewer class); write your own `FabricLoader` check there. |
| `category(String namespace, String path)` | a `CategoryIdentifier<CreateReiDisplay>` in your namespace |
| `categoryId(CategoryIdentifier<?>)` | its plain `Identifier` |
| `fill(registry, recipeClass, recipeType, mapper)` | one REI recipe filler: every loaded recipe of that type becomes one display |
| `fill(registry, recipeClass, recipeType, filter, mapper)` | the same with a `Predicate<RecipeHolder<T>>` |
| `report(manager, stage, label, categories, logger)` | from `postStage`: logs `Registered N displays for category …` per category and one total, and WARNs on an empty category. Dedicated server only. |

### `CreateReiDisplayBuilder`

`CreateReiDisplayBuilder.of(category)` then any of

- inputs: `input(EntryIngredient)`, `input(Ingredient)`, `inputs(List)`,
  `sizedInputs(List<SizedIngredient>)`, `fluidInputs(List<FluidIngredient>)`
- catalysts: `catalyst(EntryIngredient)`, `catalysts(List)`
- outputs: `output(EntryIngredient[, chance])`, `results(List<ProcessingOutput>)`,
  `fluidResults(List<FluidStack>)`
- `duration(int ticks)`, `heat(HeatCondition)`, `flags(int)`, `keepHeldItem(boolean)`
- `location(Identifier)` or `location(RecipeHolder)` — REI's display id, normally the recipe id
- `build()`

`heat(...)` both records the heat level and **appends** the blaze burner / blaze cake
catalysts, so call it after any catalyst of your own if you want yours at index 0.

Fluids are Create Fly droplets throughout: 1 bucket = 81000. Pass a Create `FluidStack` or
`FluidIngredient` straight in — `CreateReiEntries` converts to the architectury fluid entry
REI renders, which uses the same unit, so a recipe's number needs no scaling.

### `CreateReiDisplay`, `CreateReiEntries`, `CreateReiDisplays`

Also public and stable: `CreateReiDisplay`'s accessors (`inputs()`, `catalysts()`,
`outputs()`, `chance(i)`, `duration()`, `heat()`, `flags()`), `CreateReiEntries`'s entry
conversions, and `CreateReiDisplays.KEEP_HELD_ITEM` / `heatCatalysts(HeatCondition)` /
`identifierOf` / `locationOf`.

## 3. Client side — `dev.chaevsfe.createreiviewer.client`

### `CreateReiCategory<D>`

Extend it and implement three things: `getCategoryIdentifier()`, `contentHeight()` and
`build(display, panel)`; override `contentOverhangTop()` when the machine drawing sticks
out above the panel, and `getIcon()` with `OneItemRenderer` / `TwoItemRenderer`. The
constructor takes a translation key — reuse your own mod's `<id>.recipe.<name>` key so the
viewer ships no lang file. Content is 177 px wide; `build` receives a `Panel` whose origin
is already the content's top-left, so a JEI category's own slot coordinates transfer
unchanged.

### `Panel`

`slot(x, y, entries)` (JEI slot background + input), `bareSlot`, `output(x, y, entries,
chance)` (chance background + chance tooltip), `junk`, `texture(AllGuiTextures, x, y)`,
`icon(AllIcons, x, y)`, `text(Component, x, y, colour)`, `pip(x, y, factory)`,
`pipScaled(x, y, scale, factory)`, and `blockPip(x, y, BlockState)`.

`blockPip` is the one an addon usually wants for its own machines: it queues Fly's
`ManualBlockRenderState`, a 27x27 single-block render at scale 20. **Do not invent a new
`PictureInPictureRenderState`** — Fly registers the renderers for its own states and there
is no hook to add another, so anything outside Fly's set will not draw. Fly's set is
MixingBasin, PressBasin, BasinBlazeBurner, Millstone, CrushWheel, Press, Saw, Deployer,
Drain, Spout, Fan, Crafter, SandPaper and ManualBlock.

### `CreateReiLayout`

Static layout helpers matching Create's own arithmetic:
`heatOf(display)`, `inputGrid(panel, inputs, x, y)` (3-wide, centred when fewer than three,
growing upward), `outputGrid(panel, display, centreX, y)` with `outputX` / `outputY`,
`heatSlots(panel, display, x, y[, fromIndex])`, `heatBar(panel, heat, x, y)`,
`blazeBurner(panel, heat, x, y)`, `shadow(panel, heat, x, noHeatY, heatY)`,
`catalyst(display, index)`, `heldItem(display)` (adds the "not consumed" tooltip when the
display carries `KEEP_HELD_ITEM`), and `blockOf(entries)` (first `BlockItem`'s default
state, for `blockPip`).

### `BasinCategory`

Extend it for anything that is a basin with a machine on top: it fixes the height and
overhang and gives you `basinBackground(panel, heat, outputCount)`, `basinInputs`,
`basinOutputs(panel, display, yBase)`, `heatSlots(panel, display)` and `heatOf(display)`.

### `CreateReiWidgets`

`texture`, `icon`, `pictureInPicture`, `pictureInPictureScaled`, `slot`, `inputSlot`,
`outputSlot`, `junkSlot`, `keepHeld`, `slotBackground`, and the basin slot arithmetic
(`basinInputX/Y`, `basinOutputX/Y`).

## 4. Worked example

`create-diesel-generators/Create-Diesel-Generators-Fly` (seven categories, four of them
with fluids and heat) and `create-power-grid/Create-Power-Grid-Fly` (two) are both written
against nothing but this document.
