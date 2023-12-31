package pistonmc.flyingmachine.observer.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import pistonmc.flyingmachine.BlockPos;

public class ObserverModuleRepeater implements ObserverModule {
	private boolean repeaterLocked;

	@Override
	public boolean shouldKeepWatching(Block block, int meta) {
		return BlockRedstoneDiode.isRedstoneRepeaterBlockID(block);
	}

	@Override
	public boolean isChanged(World world, BlockPos pos, int meta) {
		boolean newRepeaterLocked = Blocks.powered_repeater.func_149910_g(world, pos.x, pos.y, pos.z, meta);
		boolean changed = repeaterLocked != newRepeaterLocked;
		repeaterLocked = newRepeaterLocked;
		return changed;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		repeaterLocked = nbt.getBoolean("observer_repeaterLocked");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("observer_repeaterLocked", repeaterLocked);
	}

}
