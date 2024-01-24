package pistonmc.pistonandobserver.coremod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import pistonmc.pistonandobserver.ModInfo;
import pistonmc.pistonandobserver.core.Config;

/**
 * Mixin & Coremod config
 */
public class ModTweaker implements IMixinConfigPlugin {

    static {
        Config.preInit();
        ModInfo.log.info("enablePiston = " + Config.enablePiston);
        ModInfo.log.info("enableObserver = " + Config.enableObserver);
    }

    @Override
    public void onLoad(String mixinPackage) {
        ModInfo.log.info("Loading mixins from " + mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        ModInfo.log.info("Adding mixins to load");
        List<String> mixins = new ArrayList<>();
        if (Config.enablePiston) {
            ModInfo.log.info("Adding piston mixins");
            mixins.add("piston.IMixinBlockPistonAccessor");
            mixins.add("piston.MixinBlockPistonBase");
            if (!Config.modifyVanillaPiston) {
                ModInfo.log.info("Adding piston extension mixin because modifyVanillaPiston = false");
                mixins.add("piston.MixinBlockPistonExtension");
            }
        } else {
            ModInfo.log.info("Skipping piston mixins because enablePiston = false");
        }
        if (Config.enableObserver) {
            ModInfo.log.info("Adding observer mixins");
            if (Config.enableWorldTweaks) {
                mixins.add("observer.MixinBlock");
                mixins.add("observer.MixinWorld");
                mixins.add("observer.MixinForgeHooks");
            } else {
                ModInfo.log.info("Skipping world mixins because enableWorldTweaks = false");
            }
            if (Config.enableDoorTweaks) {
                mixins.add("observer.MixinBlockDoor");
            } else {
                ModInfo.log.info("Skipping door mixins because enableDoorTweaks = false");
            }
            if (Config.enableNoteBlockTweaks) {
                mixins.add("observer.MixinBlockNote");
                mixins.add("observer.MixinTileEntityNote");
            } else {
                ModInfo.log.info("Skipping note block mixins because enableNoteBlockTweaks = false");
            }
        } else {
            ModInfo.log.info("Skipping observer mixins because enableObserver = false");
        }
        for (String mixin: mixins) {
            ModInfo.log.info("Adding mixin " + mixin);
        }
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
        ModInfo.log.info("Applying mixin " + mixinClassName + " to " + targetClassName);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
    }

}
