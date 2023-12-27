package com.piston.mc.flyingmachinebackport;

import com.piston.mc.flyingmachinebackport.init.Init;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Mod entry point
 */
@Mod(modid = ModInfo.Id, version = ModInfo.Version)
public class ModMain
{
	@SidedProxy(
		clientSide = "com.piston.mc.flyingmachinebackport.init.InitClient",
		serverSide = "com.piston.mc.flyingmachinebackport.init.InitServer"
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
