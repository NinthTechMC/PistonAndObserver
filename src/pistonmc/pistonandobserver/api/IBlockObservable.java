package pistonmc.pistonandobserver.api;

import net.minecraft.world.World;

/**
 * Interface for blocks to implement to override how they notify observers
 *
 * For example, you can only notify observers on certain sides of the block.
 *
 * All blocks by default, will notify observers on all 6 sides.
 */
public interface IBlockObservable {
    public void notifyObservers(World world, int x, int y, int z);
}
