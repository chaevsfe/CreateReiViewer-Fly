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

**The mod goes on the server as well as the client.** Both sides also need Create Fly, Fabric API, and REI.

## JEI

Installing JEI (or RRV) next to Create Fly can make every client fail to join

Workaround: put the nested steps on the wire by name instead of by
id. It changes the wire format, In `config/createreiviewer.json`:

```json
{"fixSequencedAssemblySync": true}
```
**Must be enabled on the server and client**

## Licence

MIT — see `LICENSE`
