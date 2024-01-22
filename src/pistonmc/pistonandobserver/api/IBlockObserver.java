package pistonmc.pistonandobserver.api;

import net.minecraft.world.World;

/**
 * Make your block implement this interface to receive updates as if
 * it was an observer
 */
public interface IBlockObserver {
    /**
     * Get the side the back of the observer (the output side) is facing
     *
     * See {@link net.minecraft.util.Facing} for possible values
     */
    public int getObserverBackFacing(World world, int x, int y, int z);

    /**
     * Get the side the front of the observer (the input side) is facing
     *
     * See {@link net.minecraft.util.Facing} for possible values
     *
     * This doesn't have to be the opposite of getObserverBackFacing
     */
    public int getObserverFrontFacing(World world, int x, int y, int z);

    /**
     * Check if the observer is on
     */
    public boolean isObserverOn(World world, int x, int y, int z);

    /**
     * Called to turn on the observer. You will only be called if your
     * front is facing the right direction, and you are not already on
     */
    public void turnOnObserver(World world, int x, int y, int z, int frontSide);
}
