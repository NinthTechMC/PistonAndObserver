package pistonmc.pistonandobserver.mixins.observer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import pistonmc.pistonandobserver.api.IBlockObservable;

@Mixin(World.class)
public class MixinWorld {
    @WrapOperation(
        method = "setBlock(IIILnet/minecraft/block/Block;II)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;func_150807_a(IIILnet/minecraft/block/Block;I)Z")
    )
    public boolean notifyObserverWhenSetBlock(
        Chunk chunk, int xInChunk, int yInChunk, int zInChunk, Block block, int meta,
        Operation<Boolean> setBlock,
        int x, int y, int z, Block block2, int meta2, int flags) {
    
        World world = (World) (Object) this;
        if (world.captureBlockSnapshots && !world.isRemote) {
            // capturing snapshots, don't update
            // just set the block regularly
            return setBlock.call(chunk, xInChunk, yInChunk, zInChunk, block, meta);
        }

        // vanilla will not call chunk.getBlock if the block doesn't cause an update
        // however we have to call it to get the old block
        Block oldBlock = chunk.getBlock(xInChunk, yInChunk, zInChunk);

        boolean changed = setBlock.call(chunk, xInChunk, yInChunk, zInChunk, block, meta);
        if (changed && !world.isRemote) {
            IBlockObservable observed = (IBlockObservable) block2;
            observed.notifyObservers(world, x, y, z, oldBlock, block2);
        }
        return changed;
      
    }

    @WrapOperation(
        method = "setBlockMetadataWithNotify(IIIII)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBlockMetadata(IIII)Z")
    )
    public boolean notifyObserverWhenSetBlockMetadata(
        Chunk chunk, int xInChunk, int yInChunk, int zInChunk, int meta,
        Operation<Boolean> setBlockMetadata,
        int x, int y, int z, int meta2, int flags) {

        boolean changed = setBlockMetadata.call(chunk, xInChunk, yInChunk, zInChunk, meta);

        World world = (World) (Object) this;
        if (changed && !world.isRemote) {
            Block block = chunk.getBlock(xInChunk, yInChunk, zInChunk);
            IBlockObservable observed = (IBlockObservable) block;
            observed.notifyObservers(world, x, y, z, block, block);
        }
        return changed;
    }
}
