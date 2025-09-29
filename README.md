# Scrap Tech Workshop (1.20.1)

![Scrap Tech Workshop Versions](http://cf.way2muchnoise.eu/versions/Minecraft_1344278_all.svg)

[![Download on CurseForge](http://cf.way2muchnoise.eu/title/1344278.svg)][mod_page]
[![CurseForge Downloads](http://cf.way2muchnoise.eu/full_1344278_downloads.svg)][mod_page]

[![Download on Modrinth](https://img.shields.io/badge/dynamic/json?labelColor=black&color=grey&label=&query=title&url=https://api.modrinth.com/v2/project/Radao0um&style=flat&logo=modrinth)][modrinth_page]
[![Modrinth Downloads](https://img.shields.io/badge/dynamic/json?labelColor=black&color=grey&label=&suffix=%20downloads&query=downloads&url=https://api.modrinth.com/v2/project/Radao0um&style=flat&logo=modrinth)][modrinth_page]

[![Report an Issue](https://img.shields.io/badge/Report_Issue-grey?style=flat&logo=github)][issues]

[![Forge the Future: ModJam 2025](https://img.shields.io/badge/ModJam-Forge_the_Future_2025-orange?style=flat)][modjam]

[![Wiki & Docs](https://img.shields.io/badge/Wiki-Docs_lightly_blue?style=flat&logo=read-the-docs)][wiki]
[![Support me on Ko-fi](https://img.shields.io/badge/Support_me_on_Ko--fi-!?labelColor=black&style=flat&logo=ko-fi)][ko-fi]

![Scrap Tech Workshop][logo]

⚠️ **Beta**: Use on copies / backups of your worlds. Balancing and internals can still shift during
the ModJam.

Scrap Tech Workshop is a light post‑apocalyptic style tech mod about squeezing value out of
leftovers instead of minting shiny new alloys.
Hunt down scattered scrap, salvage worn parts, build improvised power cells and assemble a capable
multitool before establishing your first recycler-powered corner of industry. ♻️

Play loop: explore → scavenge → assemble → recycle → expand. Fast to start, space to grow.

## ModJam Entry 🧪

Created for the **“Forge the Future: Minecraft ModJam 2025”**.

During the jam: numbers may shift, visuals may iterate, and feedback (balance, crashes, weird edge
cases, QoL) is very welcome.

[Read about the event][modjam]

## Core Features (current) ✨

* Scrap Piles – size 1–4, different variants (Mixed / Metal / Tech). Right‑click harvest or break;
  can auto‑merge; can (optionally) decay.
* Recycler – processes items into scrap outputs; comparator (analog) signal; state driven.
* Scrap Multitool – single energy tool acting as pick / axe / shovel depending on block; battery
  powered.
* Energy Cells – removable / rechargeable style items; visual state reflects charge.
* Auto‑Merge & Auto‑Pickup (configurable) for smoother scavenging.
* Fortune / Silk Touch support on scrap piles (config dependent).
* Redstone: comparator output from recycler for automation.
* Multi‑loader: Forge + Fabric builds available (NeoForge planned after jam).

Not listed = not in yet (modules, world structures, cross‑mod recipes will come later).

## Quick Start 🛠️

1. Scavenge Scrap:
    * Break stone and early world debris (if configured) or raid chests.
    * Harvest natural / generated scrap piles (right‑click or mine). Fortune helps.
2. Craft a basic Energy Cell (early improvised battery) 🔋
3. Craft the Scrap Multitool and insert the battery (Shift + Right‑click to open if needed).
4. Gather more junk and craft the Recycler.
5. Feed junk into the Recycler → get structured scrap components → unlock better crafting routes.
6. Automate with hoppers / comparators once stable.

From there you expand by locating richer pile variants and refining scrap loops.

## Theme & Design 🎯

Low‑tier tech re-emerging from ruins: you rebuild functionality from fragments.
Emphasis is on reclaiming and repurposing—fewer pristine fantasy metals, more battered panels,
coils, housings.

Later (post‑ModJam) goals include optional cross‑mod salvage mapping (turning foreign items into
meaningful scrap lines) without bloating early progression.

## Scrap Piles

They appear (or can be placed) in variable sizes (1–4). Interact to siphon loot and shrink them.
Similar piles sitting side by side can fuse if auto-merge is enabled.
Optional decay slowly removes neglected piles, keeping worlds clean.

Variants influence loot flavor & rarity. Balance is still evolving—drop your impressions.

## Recycler

Drop in items → get processed scrap or crafted recoverables. Designed to be:

* Redstone-friendly (comparator output)
* Cross-loader safe (menu opening uses adaptive logic)
* Extendable (internal recipe + state structure ready for expansion)

Upcoming ideas: progress overlay, subtle hum / particle feedback, specialty scrap tiers.

## Scrap Multitool

One charged core instead of a hotbar full of tools.

Highlights:

* Adaptive mining speed based on block tag (axe / pick / shovel)
* Energy costs: per block, per attack, per active use
* Replaceable battery; depleted cells visually downgrade
* Four reserved module slots (API scaffolded; gameplay modules arrive later)
* Shift + Right-click to configure
* Not enchantable on purpose—upgrades will be modular

Design goal: utility-first without overwhelming new players.

## Settings & Tuning

Configurable (present or planned):

* Auto-merge toggle
* Auto-pickup + cooldown
* Decay enable + chance
* Silk Touch + Fortune behavior
* Loot weighting & variant tables

Open an issue if you want a knob that isn’t there yet.

## Early Access & Feedback ⭐

This is a living mod during the jam phase:

* Numbers may shift
* Visuals are still being refined
* Cross-mod interactions are unverified

You can help by reporting:

* Crashes (include the log!)
* Unexpected recipe / loot results
* Performance spikes around piles or recycler
* Suggestions for new scrap uses, items, blocks or module effects

[Open an issue][issues]

## Wiki & Docs ℹ️

Light docs are being assembled. More pages (multitool internals, recipe data, balancing notes) will
appear as systems mature.

## Support

If you enjoy the mod and want to fuel more late-night iteration, a Ko‑fi tip means a lot.
Never required—always appreciated.

Thanks for giving broken tech a second life. ⚙️

## License ⚖️

Code: [MIT License](LICENSE.md)  
Assets (models, textures, sounds, etc.) are excluded unless explicitly stated.

## Rough Roadmap (after ModJam, not active yet)

* Modules for the multitool (AOE / scanner / HUD flair)
* Recycler tiering (speed, efficiency, catalysts)
* World scrap micro-structures / salvage caches
* Cross‑mod salvage tables (opt‑in integration)
* Workshop blocks (charging bay, analyzer station)
* Light lore fragments (data shards, encoded tags)

Thanks for playing & salvaging. ♻️

[ko-fi]: https://ko-fi.com/Kaworru

[wiki]: https://github.com/MarkusBordihn/BOs-Scrap-Tech-Workshop/wiki

[logo]: Common/src/main/resources/logo.png

[mod_page]: https://www.curseforge.com/minecraft/mc-mods/scrap-tech-workshop

[modrinth_page]: https://modrinth.com/mod/scrap-tech-workshop

[issues]: https://github.com/MarkusBordihn/BOs-Scrap-Tech-Workshop/issues

[modjam]: https://mod.curseforge.com/minecraft-modjam/
