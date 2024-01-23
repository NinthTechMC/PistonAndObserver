package pistonmc.pistonandobserver.api;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.core.Config;

public class ObserverAPI {

    /**
     * Turn on the observer at x, y, z if it's not already on. Do nothing if the position is not an observer
     *
     * This does not check if the observer is facing the right direction. use notifyObserverAt for that.
     */
    public static void triggerObserverAt(World world, int x, int y, int z) {
        if (!Config.enableObserver) {
            return;
        }
        Block b = world.getBlock(x, y, z);
        if (b instanceof IBlockObserver) {
            IBlockObserver observer = (IBlockObserver) b;
            if (observer.isObserverOn(world, x, y, z)) {
                return;
            }
            int front = observer.getObserverFrontFacing(world, x, y, z);
            observer.onObserverUpdate(world, x, y, z, front);
        }
    }

    /**
     * Notify the observers at the 6 blocks around x, y, z if they are watching
     * in the right direction
     */
    public static void notifyObserversAround(World world, int x, int y, int z) {
        notifyObserverAt(world, x + 1, y, z, 4 /* west, -x */);
        notifyObserverAt(world, x - 1, y, z, 5 /* east, +x */);
        notifyObserverAt(world, x, y + 1, z, 0 /* down, -y */);
        notifyObserverAt(world, x, y - 1, z, 1 /* up, +y */);
        notifyObserverAt(world, x, y, z + 1, 2 /* north, -z */);
        notifyObserverAt(world, x, y, z - 1, 3 /* south, +z */);
    }

    /**
     * Turn on the observer at x, y, z only if its front (the input side) is facing the given direction
     *
     * Direction is `order_b` in {@link net.minecraft.util.EnumFacing}
     */
    public static void notifyObserverAt(World world, int x, int y, int z, int direction) {
        if (!Config.enableObserver) {
            return;
        }
        Block b = world.getBlock(x, y, z);
        if (b instanceof IBlockObserver) {
            IBlockObserver observer = (IBlockObserver) b;
            if (observer.isObserverOn(world, x, y, z)) {
                return;
            }
            int front = observer.getObserverFrontFacing(world, x, y, z);
            if (front == direction) {
                observer.onObserverUpdate(world, x, y, z, front);
            }
        }
    }

}
