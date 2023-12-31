package pistonmc.flyingmachine.observer.module;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.world.World;
import pistonmc.flyingmachine.BlockPos;

/**
 * Special case to handle brewing stands
 */
public class ObserverModuleBrewingStand implements ObserverModule {
	private static final int POTION_SLOT_SIZE = 3;
	private boolean[] hasPotion;

	public ObserverModuleBrewingStand() {
		hasPotion = new boolean[POTION_SLOT_SIZE];
	}

	@Override
	public boolean shouldKeepWatching(Block block, int meta) {
		return block == Blocks.brewing_stand;
	}

	/*
	 * placing potion on brewing stand can be detected, even when block and meta
	 * stays the same
	 */
	@Override
	public boolean isChanged(World world, BlockPos pos, int meta) {
		TileEntity tile = pos.getTileEntityInWorld(world);
		if (!(tile instanceof TileEntityBrewingStand)) {
			return false;
		}
		TileEntityBrewingStand brewingStand = (TileEntityBrewingStand) tile;
		// slots 0-2 are potions
		boolean changed = false;
		for (int i = 0; i < POTION_SLOT_SIZE; i++) {
			ItemStack potion = brewingStand.getStackInSlot(i);
			boolean has = potion != null;
			if (has != hasPotion[i]) {
				hasPotion[i] = has;
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for (int i = 0; i < POTION_SLOT_SIZE; i++) {
			hasPotion[i] = nbt.getBoolean("observer_brewing" + i);
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		for (int i = 0; i < POTION_SLOT_SIZE; i++) {
			nbt.setBoolean("observer_brewing" + i, hasPotion[i]);
		}
	}

}
