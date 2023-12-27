package com.piston.mc.flyingmachinebackport.observer.module;

import com.piston.mc.flyingmachinebackport.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ObserverModuleDoor implements ObserverModule {
	private boolean lastOpened;

	@Override
	public boolean shouldKeepWatching(Block block, int meta) {
		return block == Blocks.iron_door;
	}

	@Override
	public boolean isChanged(World world, BlockPos pos, int meta) {
		// determine if door block is the upper one or lower one
		boolean isUpper = (meta & 8) != 0;
		if (isUpper) {
			pos = pos.offset(0);
			if (pos.getBlockInWorld(world) != Blocks.iron_door) {
				return false;
			}
			meta = pos.getBlockMetadataInWorld(world);
		}
		boolean newOpened = (meta & 4) != 0;
		boolean changed = newOpened != lastOpened;
		lastOpened = newOpened;

		return changed;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		lastOpened = nbt.getBoolean("observer_doorLastOpened");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("observer_doorLastOpened", lastOpened);
	}

}
