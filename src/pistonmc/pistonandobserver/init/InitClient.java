package pistonmc.pistonandobserver.init;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import pistonmc.pistonandobserver.core.Config;
import pistonmc.pistonandobserver.observer.RenderObserver;

public class InitClient implements Init {
	private Init common = new InitCommon();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		common.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		common.init(event);
		this.registerObserverRendering();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		common.postInit(event);
	}
	
	private void registerObserverRendering() {
        if (Config.enableObserverBlock) {
            int id = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new RenderObserver(id));
        }
	}

}
