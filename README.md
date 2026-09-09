# Create Fly: Recipe Viewer (Unofficial)

Shows Create Fly's machine recipes in [Roughly Enough Items](https://modrinth.com/mod/rei)
on Fabric, Minecraft 26.2.

Create Fly ships recipe-viewer support for JEI and for Reliable Recipe Viewer, but none for
REI, so on a Fly server every mixing, crushing, pressing, milling and sequenced-assembly
recipe is invisible in REI — around 1,700 recipes on Fly alone, and about 4,400 once the
Create add-on ports are installed. This companion mod fills that gap. It is not affiliated
with ZurrTum, the Create team, or the REI authors.

## What it covers

All 22 of Create Fly's recipe categories: Mixing, Compacting, Automated Shapeless Crafting,
Automated Packing, Automated Brewing, Pressing, Milling, Sawing, Crushing, Deploying, Manual
Item Application, Item Draining, Filling by Spout, Sandpaper Polishing, Mechanical Crafting,
Recipe Sequence, Block Cutting, Bulk Blasting, Bulk Smoking, Bulk Washing, Bulk Haunting and
Mysterious Conversion.

Categories Create builds from something other than a recipe file are built the same way here:
crushing wheels also show milling recipes that nothing crushes, bulk blasting leaves out
anything the fire already smokes, deploying gains a sandpaper entry for every polishing
recipe, block cutting is grouped by input, and draining and filling are worked out from the
items and fluids that actually exist.

Recipes that add-ons contribute to Create's own types show up under Create's categories.
Add-ons can also register categories of their own — Create: Diesel Generators (basin
fermenting, bulk fermenting, compression molding, casting, distillation, hammering, wire
cutting) and Create: Power Grid (magnetizing, item boosting) do, and `API.md` documents how.

## Installing

**The mod goes on the server as well as the client.** REI builds Create's displays from the
recipe manager on the server and sends them to the client, so a client-only install shows
nothing. Both sides also need Create Fly, Fabric API, and REI with its own dependencies
(Architectury API and Cloth Config).

## Why REI and not JEI

REI is the only recipe viewer that works properly with Create Fly on a dedicated server
today, which is why this mod targets it: REI derives its displays server-side and sends them
during play, so nothing has to travel through the connection handshake.

JEI is a different story, and the problem is not this mod's. Create Fly opts sixteen of its
recipe serializers into Fabric's recipe synchronisation whenever JEI is installed. Fly on its
own survives that, but as soon as an add-on adds sequenced-assembly recipes the client fails
to decode them and the join breaks: a sequenced assembly recipe puts a numeric recipe-
serializer id on the wire, and that number does not mean the same thing on the client as it
does on the server. It needs fixing in Create Fly, not here.

Reliable Recipe Viewer joins fine, but Fly's own RRV support throws while registering and
shows no Create recipes at all. When both this mod and RRV are installed, the client-side
patch in here works around that so RRV gets Create's categories back. It does nothing
whatsoever when RRV is absent, and RRV's own Create recipe screen is still broken upstream.

## Licence

MIT — see `LICENSE`. Create Fly's own recipe-viewer plugins were read as the specification
for what each category should draw; no Create Fly code, class or asset is copied or
redistributed here. See `NOTICE`.
