package pistonmc.pistonandobserver.mixins.observer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.api.IBlockObservable;
import pistonmc.pistonandobserver.api.IBlockObserver;
import pistonmc.pistonandobserver.api.ObserverAPI;

@Mixin(BlockDoor.class)
public class MixinBlockDoor implements IBlockObservable {

    @Override
    public void notifyObservers(World world, int x, int y, int z, Block oldBlock, Block newBlock) {
        boolean isUpper = (world.getBlockMetadata(x, y, z) & 8) != 0;
        if (isUpper) {
            // upper door will also notify observers looking at lower door
            Block lower = world.getBlock(x, y - 1, z);
            if (lower == (Object) this) {
                ObserverAPI.notifyObserversAround(world, x, y - 1, z);
            }
        } else {
            // lower door will also notify observers looking at upper door
            Block upper = world.getBlock(x, y + 1, z);
            if (upper == (Object) this) {
                ObserverAPI.notifyObserversAround(world, x, y + 1, z);
            }
        }
        ObserverAPI.notifyObserversAround(world, x, y, z);
    }

    // Doors check for any block that can provide power
    // This means when an observer turns off, the door will update and close
    // which is not desired
    @WrapOperation(
        method = "onNeighborBlockChange",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;canProvidePower()Z")
    )
    public boolean checkCanObserverProvidePower(
        Block neighbor,
        Operation<Boolean> canProvidePower,
        World world, int x, int y, int z, Block neighbor_
    ) {
        if (!(neighbor instanceof IBlockObserver)) {
            return canProvidePower.call(neighbor);
        }
        // check if there is any observer (of this type) pointing its back at the door
        Block blockEast = world.getBlock(x + 1, y, z);
        if (blockEast == neighbor && blockEast instanceof IBlockObserver) {
            if (((IBlockObserver) blockEast).getObserverBackFacing(world, x+1, y, z) == 4 /* west */) {
                return true;
            }
        }
        Block blockWest = world.getBlock(x - 1, y, z);
        if (blockWest == neighbor && blockWest instanceof IBlockObserver) {
            if (((IBlockObserver) blockWest).getObserverBackFacing(world, x-1, y, z) == 5 /* east */) {
                return true;
            }
        }
        Block blockNorth = world.getBlock(x, y, z - 1);
        if (blockNorth == neighbor && blockNorth instanceof IBlockObserver) {
            if (((IBlockObserver) blockNorth).getObserverBackFacing(world, x, y, z-1) == 3 /* south */) {
                return true;
            }
        }
        Block blockSouth = world.getBlock(x, y, z + 1);
        if (blockSouth == neighbor && blockSouth instanceof IBlockObserver) {
            if (((IBlockObserver) blockSouth).getObserverBackFacing(world, x, y, z+1) == 2 /* north */) {
                return true;
            }
        }
        boolean isUpper = (world.getBlockMetadata(x, y, z) & 8) != 0;
        if (isUpper) {
            // upper door only checks above
            Block blockAbove = world.getBlock(x, y + 1, z);
            if (blockAbove == neighbor && blockAbove instanceof IBlockObserver) {
                if (((IBlockObserver) blockAbove).getObserverBackFacing(world, x, y+1, z) == 0 /* down */) {
                    return true;
                }
            }
        } else {
            // lower door only checks below
            Block blockBelow = world.getBlock(x, y - 1, z);
            if (blockBelow == neighbor && blockBelow instanceof IBlockObserver) {
                if (((IBlockObserver) blockBelow).getObserverBackFacing(world, x, y-1, z) == 1 /* up */) {
                    return true;
                }
            }
        }
        return false;
    }
}
