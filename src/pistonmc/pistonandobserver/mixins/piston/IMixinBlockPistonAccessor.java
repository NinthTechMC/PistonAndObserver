package pistonmc.pistonandobserver.mixins.piston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.world.World;

@Mixin(BlockPistonBase.class)
public interface IMixinBlockPistonAccessor {
    @Accessor("isSticky")
    boolean isStickyPiston();

    @Invoker
    boolean callIsIndirectlyPowered(World world, int x, int y, int z, int side);

    @Invoker
    public static boolean callCanPushBlock(Block blockToPush, World world, int x, int y, int z, boolean shouldDestroy) {
        throw new RuntimeException("IMixinBlockPistonAccessor failed to apply");
    }

}
