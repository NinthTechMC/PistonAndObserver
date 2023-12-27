package com.piston.mc.flyingmachinebackport;


import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

/**
 * Container for one config value
 */
public class ConfigContainer {

	private final Property.Type type;
	/**
	 * name of the config, should be [Category].[Config.Name]
	 */
	private final String name;
	private final String comment;
	private final Object defaultValue; /* either String or String[] */

	private Property cachedProperty = null;

	public ConfigContainer(Type type, String name, String comment, Object defaultValue) {
		super();
		this.type = type;
		this.name = name;
		this.comment = comment;
		this.defaultValue = defaultValue;
	}

	public Property get() {
		if (cachedProperty == null) {
			cachedProperty = ModConfig.getInstance().loadProperty(name, defaultValue, type, comment);
		}
		return cachedProperty;
	}
}
