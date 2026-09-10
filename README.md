# Create Fly: Recipe Viewer

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

## Rejoining a running server

REI's own display sync can silently lose part or all of what a server sends. The decode runs
while REI's client-side reload is still clearing and refilling the registries it needs, every
failure is swallowed without a log line, and REI never asks for a re-send — so a client that
reconnects to a server that has been up for a while can end up with empty or half-empty Create
categories and nothing to show why.

This mod works around that for its own displays. Once REI reports that its reload has finished
and its own sync has been applied, the client asks the server to send Create's displays again
and adds back whatever is missing, so the count is the same on every join. The client logs

```
Resynced 1726 Create displays out of 1726 REI holds (...)
```

which is the line to read if a category looks empty. Set `resyncAfterReload` to `false` in
`config/createreiviewer.json` to turn it off. REI's own vanilla displays are still affected;
that part is upstream's to fix.

## Why REI and not JEI

REI is the only recipe viewer that works properly with Create Fly on a dedicated server
today, which is why this mod targets it: REI derives its displays server-side and sends them
during play, so nothing has to travel through the connection handshake.

Reliable Recipe Viewer joins fine, but Fly's own RRV support throws while registering and
shows no Create recipes at all. When both this mod and RRV are installed, the client-side
patch in here works around that so RRV gets Create's categories back. It does nothing
whatsoever when RRV is absent, and RRV's own Create recipe screen is still broken upstream.

## JEI on Create Fly

Installing JEI (or RRV) next to Create Fly can make every client fail to join, and the bug is
Create Fly's, not this mod's. Fly opts sixteen of its recipe serializers into Fabric's recipe
synchronisation whenever JEI or RRV is present, and a sequenced assembly recipe puts a *raw
numeric* recipe-serializer id on the wire for each of its nested steps. That number is
assigned in mod-initialisation order, JEI registers its own serializers at a different point
on the client than on the server, and the client then decodes the step with the wrong
serializer and disconnects with `DecoderException: custom_payload`. Any sequenced assembly
recipe will do it — Fly's own `precision_mechanism` is enough, no add-on required — and the
failure disappears whenever the two sides happen to agree, which is why it looks intermittent.

This mod ships an opt-in workaround: put the nested steps on the wire by name instead of by
id. It changes the wire format, so **it must be enabled on the server and on every client
together**. In `config/createreiviewer.json`:

```json
{"fixSequencedAssemblySync": true}
```

The default is `false`. With the flag off and JEI or RRV installed, the mod logs one warning at
startup naming the file to edit. With the flag on, it logs that the fixed codec is active.

Mismatched sides are the one thing to avoid: with the flag on the server and off a client, that
client is disconnected during the handshake exactly as if the fix were not installed at all
(the server writes serializer names, the client reads a numeric id), and with the flag on a
client and off the server the same thing happens in reverse. Enable it everywhere or nowhere.

The real fix belongs in Create Fly, which should dispatch nested sub-recipes by name.

## Licence

MIT — see `LICENSE`. Create Fly's own recipe-viewer plugins were read as the specification
for what each category should draw; no Create Fly code, class or asset is copied or
redistributed here. See `NOTICE`.
