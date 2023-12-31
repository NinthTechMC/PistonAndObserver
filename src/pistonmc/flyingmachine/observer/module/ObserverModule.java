package pistonmc.flyingmachine.observer.module;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import pistonmc.flyingmachine.BlockPos;

/**
 * Logic for handling interaction between observer and special blocks
 */
public interface ObserverModule {
	/**
	 * return true if the module should be used to actively detect upcoming changes
	 * @param block what the current block in front of the observer is
	 * @param meta metadata of the current block
	 * @return
	 */
	boolean shouldKeepWatching(Block block, int meta);

	/**
	 * Ask if the block is changed
	 * 
	 * Only called if the block/meta are the same, and after observer is initially placed
	 * @param world
	 * @param pos
	 * @param meta
	 * @return true if the block changed from last stored state
	 */
	boolean isChanged(World world, BlockPos pos, int meta);

	// read from nbt, called from TileEntityObserver if a module is loaded on
	// readFromNBT
	public void readFromNBT(NBTTagCompound nbt);

	// write to nbt, called from TileEntityObserver if module is not on writeToNBT
	public void writeToNBT(NBTTagCompound nbt);
}
