package pistonmc.flyingmachine.init;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Init proxy
 */
public interface Init {
	public void preInit(FMLPreInitializationEvent event);

	public void init(FMLInitializationEvent event);

	public void postInit(FMLPostInitializationEvent event);
}
