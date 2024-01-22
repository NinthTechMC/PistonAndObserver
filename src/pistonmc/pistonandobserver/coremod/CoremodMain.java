package pistonmc.pistonandobserver.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/**
 * Coremod entry point
 */
@IFMLLoadingPlugin.TransformerExclusions({ "pistonmc.pistonandoberver.coremod" })
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class CoremodMain implements IFMLLoadingPlugin { //, IEarlyMixinLoader {
	// public static Logger log = LogManager.getLogger("PistonAndObserver Core");

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
    //
    // @Override
    // public String getMixinConfig() {
    //     return "mixins.pistonandobserver.json";
    // }
    //
    // @Override
    // public List<String> getMixins(Set<String> loadedCoreMods) {
    //     return Arrays.asList(
    //         "pistonmc.pistonandobserver.mixins.piston.IMixinBlockPistonAccessor",
    //         "pistonmc.pistonandobserver.mixins.piston.MixinBlockPistonBase"
    //     );
    // }

}
