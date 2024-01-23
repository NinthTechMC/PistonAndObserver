package pistonmc.pistonandobserver;

import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraft.block.BlockPistonBase;
import pistonmc.pistonandobserver.observer.BlockObserver;

/**
 * Injected reference to objects
 */
@ObjectHolder(ModInfo.MODID)
public class ModObjects {
    public static final BlockPistonBase modified_piston = null;
    public static final BlockPistonBase modified_sticky_piston = null;
	public static final BlockObserver observer = null;
}
