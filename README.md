# Create Fly: Recipe Viewer

Shows Create Fly's machine recipes in [Roughly Enough Items](https://modrinth.com/mod/rei) and [Just Enough Items](https://www.curseforge.com/minecraft/mc-mods/jei)
on Fabric, Minecraft 26.2.

Create Fly recipe-viewer support: mixing, crushing, pressing, milling and sequenced-assembly
recipe is is visible now in REI/JEI.

It is not affiliated with ZurrTum, the Create team, or the REI and JEI authors.

## What it covers

All 22 of Create Fly's recipe categories: Mixing, Compacting, Automated Shapeless Crafting,
Automated Packing, Automated Brewing, Pressing, Milling, Sawing, Crushing, Deploying, Manual
Item Application, Item Draining, Filling by Spout, Sandpaper Polishing, Mechanical Crafting,
Recipe Sequence, Block Cutting, Bulk Blasting, Bulk Smoking, Bulk Washing, Bulk Haunting and
Mysterious Conversion.

Recipes that add-ons contribute to Create's own types show up under Create's categories.
Add-ons can also register categories of their own. 


## Installing
### Download

- [Modrinth](https://modrinth.com/project/vMOBLOfr)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/create-fly-recipe-viewer/preview)
- [GitHub releases](https://github.com/chaevsfe/CreateReiViewer-Fly/releases)

**Required on server and client**
| Component | Version |
| --- | --- |
| Minecraft | `26.2` |
| Mod loader | Fabric Loader `0.19.3` or newer |
| Create Fly | `6.0.9-1` or newer |
| Fabric API | required |
| REI/JEI | optional i guess |
| Java | `25` or newer |
| Environments | Client and server |

## Dark GUI

An optional dark look for Create's own screens: filters, toolboxes, schedules, stock keepers,
factory gauges, schematics and the rest.

Turn it on in Options > Resource Packs: move "Create Fly: Dark GUI" from Available to Selected.
It is off by default, and switching it on or off needs no restart.

The pack ships no Create art. It is built on your machine, when resources load, from the
textures inside your own copy of Create Fly, and the dark titles and labels those screens draw
in code are lightened while it is on; titles on Create's gold header bars stay dark. A resource
pack you rank above it that replaces a Create GUI texture keeps that texture. REI and JEI keep
their own themes; only the slots and shadows of Create's recipe categories inside them turn dark,
while the recipe arrows and the heat plates keep their original look. Pair it with REI's dark
theme; with REI's light theme Create's slots and shadows come out dark inside REI's light window.

## JEI
Required on both server and client.

## Support

Please report problems on the issue tracker. Include the mod version, and the client or server log.
