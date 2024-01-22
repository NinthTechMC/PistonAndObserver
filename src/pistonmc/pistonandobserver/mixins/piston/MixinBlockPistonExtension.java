package pistonmc.pistonandobserver.mixins.piston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.core.BlockPos;
import pistonmc.pistonandobserver.core.Config;

@Mixin(BlockPistonExtension.class)
public class MixinBlockPistonExtension {
    @Inject(method = "onNeighborBlockChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"), cancellable = true)
    public void checkModdedPiston(World world, int x, int y, int z, Block neighbor, CallbackInfo ci) {
        if (Config.modifyVanillaPiston) {
            // modded pistons are not added if vanilla is modified
            return;
        }
        int direction = BlockPistonExtension.getDirectionMeta(world.getBlockMetadata(x, y, z));
        BlockPos pistonPos = new BlockPos(x, y, z).oppositeOffset(direction, 1);
        Block block = pistonPos.getBlockInWorld(world);
        if (block instanceof BlockPistonBase) {
            ci.cancel();
        }
    }
}
