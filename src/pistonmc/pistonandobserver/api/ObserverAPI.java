package pistonmc.pistonandobserver.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ObserverAPI {
    /**
     * Turn on the observer at x, y, z. Do nothing if the position is not an observer
     *
     * This does not check if the observer is facing the right direction. use notifyObserverAt for that.
     */
    public static void triggerObserverAt(World world, int x, int y, int z) {
        // // we have mixed-in the interface
        // ((IObserverNotifier) world.getBlock(x, y, z)).notifyObserverAt(x, y, z);
    }

    /**
     * Notify the observers at the 6 blocks around x, y, z if they are watching
     * in the right direction
     */
    public static void notifyObserversAround(World world, int x, int y, int z) {
        notifyObserverAt(world, x + 1, y, z, EnumFacing.EAST);
        notifyObserverAt(world, x - 1, y, z, EnumFacing.WEST);
        notifyObserverAt(world, x, y + 1, z, EnumFacing.UP);
        notifyObserverAt(world, x, y - 1, z, EnumFacing.DOWN);
        notifyObserverAt(world, x, y, z + 1, EnumFacing.SOUTH);
        notifyObserverAt(world, x, y, z - 1, EnumFacing.NORTH);
    }

    /**
     * Turn on the observer at x, y, z only if its back (the output side) is facing the given direction
     */
    public static void notifyObserverAt(World world, int x, int y, int z, EnumFacing observerFacing) {
    }
}
