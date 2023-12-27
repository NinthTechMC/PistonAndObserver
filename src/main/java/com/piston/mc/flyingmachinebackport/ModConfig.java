package com.piston.mc.flyingmachinebackport;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Config singleton
 */
public class ModConfig {
	private static ModConfig instance = null;

	public static ModConfig getInstance() {
		if (instance == null) {
			instance = new ModConfig();
			instance.loadFromFile();
		}
		return instance;
	}

	public static final String CONFIG_NAME = "FlyingMachineBackport.cfg";
	private Configuration config = null;

	private Map<String, String> categoryDescription;
	private boolean initializing;

	private ModConfig() {
		categoryDescription = new HashMap<String, String>();
		categoryDescription.put("Hooks", "Enable or disable hooks");
		initializing = true;
	}

	public void loadFromFile() {
		File configFile = new File("").getAbsoluteFile().toPath().resolve("config").resolve("Pistonight")
				.resolve(CONFIG_NAME).toFile();
		config = new Configuration(configFile);
		config.load();

	}

	public void finishInitialization() {
		initializing = false;
		save();
	}

	private void save() {
		if (config.hasChanged()) {
			config.save();
		}
	}

	public Property loadProperty(String categorizedName, Object defaultValue, Property.Type type, String comment) {

		int dotIndex = categorizedName.indexOf('.');
		String category;
		String propertyName;
		if (dotIndex == -1) {
			category = "Main";
			propertyName = categorizedName;
		} else {
			category = categorizedName.substring(0, dotIndex);
			propertyName = categorizedName.substring(dotIndex + 1);
		}
		ConfigCategory configCategory = loadCategory(category, categoryDescription.getOrDefault(category, ""));
		Property prop = configCategory.get(propertyName);
		if (prop == null) {
			if (defaultValue instanceof String) {
				prop = new Property(propertyName, (String) defaultValue, type);
			} else {
				prop = new Property(propertyName, (String[]) defaultValue, type);
			}
			configCategory.put(propertyName, prop);
		}
		prop.comment = comment;

		if (!initializing) {
			save();
		}

		return prop;
	}

	private ConfigCategory loadCategory(String name, String comment) {
		ConfigCategory c = config.getCategory(name);
		String old = c.getComment();
		if ((comment != null && !comment.equals(old)) || (comment == null && old != null)) {
			c.setComment(comment);
		}
		return c;
	}

}
