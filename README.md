# FlyingMachineBackport

This mod backports slime/honey blocks and observers to 1.7.10 so I can make flying machines

**note: working on an update to use mixins. Currently won't build**

You can see a demo on [youtube](https://www.youtube.com/watch?v=nKD4PSGlFjU)

## Features
- Observer: doesn't replicate the logic perfectly. The following works:
  - Movable by pistons
  - Flying machines
  - Clock by having 2 observers face-to-face (frequency is close to real observers, but slightly different)
  - Detects block and metadata changes
  - Detects special block changes:
    - Place potion on Brewing Stand
	- Iron Door open/close
	- Placing flower in flower pot
	- Snow on grass
	- Note/instrument/powered state change for Note Blocks
	- Locked state changes for powered Repeaters
- Slime & Honey Blocks
  - Slightly different texture and crafting recipe, because I didn't spend too much time on figuring out
    how to make the transparency work. Also I think 4x slime ball might conflict with recipes in some other mods.
	If you want to change the recipe, feel free to use MineTweaker
  - Slime Blocks don't stick to Honey Blocks
  - Honey block is transparent (don't carry redstone signal)
  - Interactions with pistons should be exactly the same
  
## Contribution
Contributions are welcome. Feel free to open issues/PRs if there is a bug or observer feature missing.

## Dev Setup
NEI:

You will need the dev JARs for
- CoreChickenLib
- CoreChickenCore
- NotEnoughItems

Put the mod in `/libs`, when launching, select `~\.gradle\caches\minecraft\net\minecraftforge\forge\1.7.10-10.13.4.1614-1.7.10\unpacked\conf` for the mcp conf dir

I use [mcmod](https://github.com/Pistonight/mcmod) to run commands
