package pistonmc.pistonandobserver;

import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
// import pistonmc.flyingmachine.observer.BlockObserver;
// import pistonmc.flyingmachine.piston.BlockPistonAir;
import net.minecraft.block.BlockPistonBase;
import pistonmc.pistonandobserver.piston.BlockPistonAir;

/**
 * Injected reference to objects
 */
@ObjectHolder(ModInfo.MODID)
public class ModObjects {
    public static final BlockPistonBase modified_piston = null;
    public static final BlockPistonBase modified_sticky_piston = null;
	// public static final BlockObserver blockObserver = null;
	public static final BlockPistonAir piston_air = null;
	//
	// public static final ItemHoneyBall itemHoneyBall = null;
}
