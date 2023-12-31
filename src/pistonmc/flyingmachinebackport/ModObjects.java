package pistonmc.flyingmachinebackport;

import pistonmc.flyingmachinebackport.observer.BlockObserver;
import pistonmc.flyingmachinebackport.piston.BlockPistonAir;

import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

/**
 * Injected reference to objects
 */
@ObjectHolder(ModInfo.Id)
public class ModObjects {
	public static final BlockConnectorSlime blockPistonConnectorSlime = null;
	public static final BlockConnectorHoney blockPistonConnectorHoney = null;
	public static final BlockObserver blockObserver = null;
	public static final BlockPistonAir blockPistonAir = null;

	public static final ItemHoneyBall itemHoneyBall = null;
}
