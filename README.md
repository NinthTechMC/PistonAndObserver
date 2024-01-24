# Piston and Observer

This mod backports sticky blocks (slime/honey) and observers to 1.7.10.

## Dependencies
- Unimixin (https://github.com/LegacyModdingMC/UniMixins/)
- GTNH Lib (https://github.com/GTNewHorizons/GTNHLib)
- LibPiston (https://github.com/NinthTechMC/LibPiston)

## Piston Features
Piston behavior is modified so that blocks that are stuck to sticky blocks will be moved
together. You can configure if vanilla pistons are changed to this, or new piston and sticky piston
blocks with this behavior are added (new blocks won't have recipes).

What are "sticky blocks"? This mod provides a slime block implementation and a honey block one
to mimic vanilla behavior. By default, they are only added if no compatible mods are detected.

Compatible mods include:
- Tinker's Construct's green and blue congealed slime blocks (green and blue don't stick to each other)
- Biomes O Plenty's honey block

You can also add more to the config, and also config what will not stick to what.

## Observer Features
Observers are not TileEntities (anymore). They are movable by pistons and can be used to create flying machines.

What works by default without individual tweaks (non-exhaustive):
- Any block change or metadata change (even when they don't cause block update)
- Flying machine
- Clock (by having 2 observers face-to-face)
- Place potion on Brewing Stand
- Placing flower in flower pot

Some tweaks to vanilla blocks are needed to have their state changes detected by observers like
in later versions. Every such modification has a config that you can turn off if it causes conflicts
with other mods.

- Doors opening/closing notify observers looking at both door blocks.
- Grass blocks changing between snowed and non-snowed states.
- Note blocks changing pitch, instrument, or powered state.
- Redstone Repeaters changing between locked and unlocked states.

## Observer API
This mod provides an API for other mods to integrate into the observer system:

- `ObserverAPI` to notify observers in the world of some change
- `IBlockObservable` for blocks to override how they notify observers
  - for example, vanilla observers notify themselves when moved by pistons
  - you can also make a block only notify observers in a certain direction, for example
- `IBlockObserver` for blocks to become an observer
- Subscribe to `ObserverEvent` to be notified whenever any block is changed.
  This allows observer updates for interactions between blocks that are not part of the block state,
  like note block instruments and grass block snowyness
  
## Contribution
Contributions are welcome. Feel free to open issues/PRs if there is a bug or observer feature missing.

## Dev Setup
Using [mcmod](https://github.com/NinthTechMC/mcmod) to generate a project with the [GTNH Example Mod](https://github.com/GTNewHorizons/ExampleMod1.7.10).
You can either use mcmod to interact with the generated project or develop in the generated project directly
