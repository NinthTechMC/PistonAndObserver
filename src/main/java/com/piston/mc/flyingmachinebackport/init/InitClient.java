package com.piston.mc.flyingmachinebackport.init;

import com.piston.mc.flyingmachinebackport.observer.ObserverRenderCore;
import com.piston.mc.flyingmachinebackport.observer.ObserverRenderMoving;
import com.piston.mc.flyingmachinebackport.observer.ObserverRenderStationary;
import com.piston.mc.flyingmachinebackport.observer.TileEntityObserver;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class InitClient implements Init {
	private Init common = new InitCommon();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		common.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		common.init(event);
		registerObserverRendering();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		common.postInit(event);
	}
	
	private void registerObserverRendering() {
		ObserverRenderCore observerRenderCore = new ObserverRenderCore();
		int id = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new ObserverRenderStationary(id, observerRenderCore));

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityObserver.class,
				new ObserverRenderMoving(observerRenderCore));
	}

}
