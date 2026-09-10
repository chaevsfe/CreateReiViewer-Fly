# Create Fly: Recipe Viewer

Shows Create Fly's machine recipes in [Roughly Enough Items](https://modrinth.com/mod/rei)
on Fabric, Minecraft 26.2.

Create Fly recipe-viewer support for REI: mixing, crushing, pressing, milling and sequenced-assembly
recipe is is visible now in REI.

It is not affiliated with ZurrTum, the Create team, or the REI authors.

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
| REI | optional i guess |
| Java | `25` or newer |
| Environments | Client and server |

## JEI

Installing JEI (or RRV) next to Create Fly can make every client fail to join

Workaround: Change the wire format, In `config/createreiviewer.json`:

```json
{"fixSequencedAssemblySync": true}
```

## Support

Please report problems on the issue tracker. Include the mod version, and the client or server log.
