package pistonmc.flyingmachinebackport.coremod;

import pistonmc.flyingmachinebackport.ConfigContainer;

import net.minecraftforge.common.config.Property;

/**
 * Configuration for the coremod
 */
public class CoremodConfig {
	private static ConfigContainer hooksEnableSlimeBlockPiston = new ConfigContainer(Property.Type.BOOLEAN,
			"Hooks.EnableSlimeBlockPiston",
			"Enable hooks to make piston work with slime block/honey block. If disabled, slime block/honey block will function like regular blocks.",
			"true");

	public static boolean hooksEnableSlimeBlockPiston() {
		return hooksEnableSlimeBlockPiston.get().getBoolean();
	}
}
