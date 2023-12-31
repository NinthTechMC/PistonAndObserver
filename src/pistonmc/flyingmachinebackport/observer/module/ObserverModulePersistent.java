package pistonmc.flyingmachinebackport.observer.module;

import pistonmc.flyingmachinebackport.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Persistently watch for changes but only updates when block/meta are different.
 * 
 * This is for blocks that can update, but don't notify neighbours when they do, like farmland turning into dirt
 */
public class ObserverModulePersistent implements ObserverModule {
	private Block forBlock;

	public ObserverModulePersistent(Block forBlock) {
		super();
		this.forBlock = forBlock;
	}

	@Override
	public boolean shouldKeepWatching(Block block, int meta) {
		return block == forBlock;
	}

	@Override
	public boolean isChanged(World world, BlockPos pos, int meta) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	}

}
