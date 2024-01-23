package pistonmc.pistonandobserver;

import pistonmc.pistonandobserver.init.Init;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Mod entry point
 */
@Mod(
    modid = ModInfo.MODID,
    version = Tags_GENERATED.VERSION, 
    dependencies = "required-after:gtnhlib@[0.2.1,);"
        + "required-after:libpiston@[1.1.0,);"
        + "after:TConstruct;"
        + "after:BiomesOPlenty"
)
public class ModMain
{
	@SidedProxy(
		clientSide = "pistonmc.pistonandobserver.init.InitClient",
		serverSide = "pistonmc.pistonandobserver.init.InitCommon"
	)
	public static Init initProxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		initProxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		initProxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		initProxy.postInit(event);
	}

}
