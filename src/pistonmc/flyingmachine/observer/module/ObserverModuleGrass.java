package pistonmc.flyingmachine.observer.module;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import pistonmc.flyingmachine.BlockPos;
import pistonmc.flyingmachine.observer.BlockObserver;

public class ObserverModuleGrass implements ObserverModule {

	private boolean ignoring;
	private boolean snowy;

	@Override
	public boolean shouldKeepWatching(Block block, int meta) {
		return block == Blocks.grass;
	}

	@Override
	public boolean isChanged(World world, BlockPos pos, int meta) {
		BlockPos up = pos.offset(1);
		Block upBlock = up.getBlockInWorld(world);
		Material upMaterial = upBlock.getMaterial();
		boolean newSnowy = upMaterial == Material.snow || upMaterial == Material.craftedSnow;
		boolean changed = snowy != newSnowy || ignoring;
		snowy = newSnowy;
		if (BlockObserver.shouldIgnoreBlock(world, up, upBlock)) {
			ignoring = true;
			return false;
		}
		ignoring = false;
		return changed;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		snowy = nbt.getBoolean("observer_grassSnowy");
		ignoring = nbt.getBoolean("observer_ignoring");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("observer_grassSnowy", snowy);
		nbt.setBoolean("observer_ignoring", ignoring);
	}

}
