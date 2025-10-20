# Changelog for Scrap Tech Workshop

All notable changes to this project will be documented in this file.

## Note

This change log includes the summarized changes.
For the full changelog, please go to the [GitHub History][history] instead.

### v.16.0

- Fixed recharging station issues with different facing directions.
- Added reclaimed copper wire and refined copper wire items.
- Added floating scrap collector block.
- Added filter items for the floating scrap collector block.
- Added additional holo log for floating scrap collector and changed holo log order.
- Simplified Forge item registration.
- Fixed smaller issues and typos.

### v.15.0

- Fixed issue with fishing in Fabric catching to many scrap items.
- Added Mekanism support to be able to use Mekanism energy storage items to power blocks.
- Added recharging station for batteries and energy storage items.
- Added composter support for scrap items.

### v.14.0

- Replaced hard-coded break block list to a tag based system for better mod compatibility.
- Added additional blocks like dirt, sand, ... to the breakable blocks tag.
- Added anti-cheating for block breaking.
- Added proper energy system with mA, A and V units.
- Improved processing and performance.

### v.13.0

- Larger refactor and rework of the mod structure and code base for better maintainability.
- Fixed scrap box model and texture issues.
- Added stackable scrap item boxes with optimized models.
- Added scrap to scrap box and scrap box to scrap recipes.
- Added scrap and scrap box furnace smelting recipes.

### v.12.0

- Fixed #1 by adding legacy Forge mod loader support for 47.2.x.
- Fixed missing render types for Fabric.
- Added debug manager for less verbose logging on production systems.
- Added experimental scrap item boxes.
- Improved block, block item and item registry.
- Improved robot spawn conditions and balancing.
- Improved mod logo for better visibility.
- Improved wiki and documentation.

### v0.11.0

- Fixed multi-tool edge case issues.
- Reworked base robot entity, model and textures.
- Added mutli-tool creative item for better testing.
- Added different scrap collection station textures depending on the status.
- Added different robots for different use-cases.
- Added natural spawn for robots in the world.
- Improved robot pathing, movement and animations.
- Overworked textures for better visual appearance.

### v0.10.0

- Added experimental base robot entity and model.
- Added experimental collector stations.
- Added fast charge upgrade item.
- Added client-side only entity rendering and pathing for better performance.
- Improved multi-tool mode switching and handling.
- Improved existing menu handling and code structure.
- General performance improvements and optimizations.

### v0.9.0

- Fixed multi-tool mode switching issues.
- Fixed multi-tool mode missing secondary action.
- Fixed multi-tool holo texture for pickaxe and hoe modes.
- Fixed magnet fishing rod recipe issue.
- Added multi-tool secondary action rotation for blocks.
- Added EnergyCellConsumer interface for better energy handling.
- Added basic scrap collector station (unfished).
- Added experimental creative speed upgrade.

### v0.8.0

- Fixed holo log end effects and timing issues.
- Added holo log scavenging_briefing_03 with voice over.
- Added holo pad colors for easier identification.
- Optimized holo logs voice over sound files.

### v0.7.0

- Fixed advancement issues and automatic granting of advancements.
- Added new advancements for better gameplay experience and guidance.
- Added scrap fishing rod for more general scavenging.
- Added scrap magnet fishing rod for more technology scavenging.
- Added vanilla fishing rod support for scrap fishing.
- Added additional configuration options for scrap fishing.

### v0.6.0

- Fixed holo pad design and improved size.
- Fixed holo log timing issues and uncached parsing of holo log files.
- Fixed edge case were holo cube is not given to players in singleplayer mode.
- Added basic advancement system.
- Added holo log recipe display in the holo pads menu.
- Added holo log scavenging_briefing_01 with voice over.
- Added holo log scavenging_briefing_02 with voice over.
- Refactored holopad and holo log code for better support and handling.

### v0.5.0

- Refactored holo log player for better timing and handling.
- Fixed register issue with holocube block entity.
- Fixed multi-tool menu not opening in Fabric.
- Added holo pad model, texture and menu.
- Improved holocube texture, model and handling.
- General code improvements, clean-up and refactoring.

### v0.4.0

- Fixed serveral hololog and holocube related issues.
- Added hololog voice over support and sound files.
- Added hololog entity for better visuals.
- Improved holocube texture and model.
- Improved holocube handling.
- Removed hololog pause state to avoid issues with voice over timings.

### v0.3.0

- Added hololog support and hololog player.
- Added basic holocube model and texture.
- Added holocube support.

### v0.2.0

- Fixed multi-tool autoselection for targeted block.
- Added better energy management.
- Added multi-tool tool modes.
- Added better visually for the multitool display and hologram.
- Improved general assets and optimized sizes.
- Improved code structure and refactored code.

### v0.1.0

- First concept release of Scrap Tech Workshop mods for Fabric and Forge.

[history]: https://github.com/MarkusBordihn/BOs-Scrap-Tech-Workshop/commits/1.20.1
