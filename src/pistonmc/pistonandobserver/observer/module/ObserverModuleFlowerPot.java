// package pistonmc.flyingmachine.observer.module;
//
// import net.minecraft.block.Block;
// import net.minecraft.init.Blocks;
// import net.minecraft.nbt.NBTTagCompound;
// import net.minecraft.tileentity.TileEntity;
// import net.minecraft.tileentity.TileEntityFlowerPot;
// import net.minecraft.world.World;
// import pistonmc.flyingmachine.BlockPos;
//
// public class ObserverModuleFlowerPot implements ObserverModule {
// 	private boolean potHasItem;
//
// 	@Override
// 	public boolean shouldKeepWatching(Block block, int meta) {
// 		/*
// 		 * In 1.7.10, you cannot remove flower from pot without breaking it. so there is
// 		 * no need to keep watching after flower is placed
// 		 */
// 		return block == Blocks.flower_pot && !potHasItem;
// 	}
//
// 	@Override
// 	public boolean isChanged(World world, BlockPos pos, int meta) {
// 		TileEntity tile = pos.getTileEntityInWorld(world);
// 		if (!(tile instanceof TileEntityFlowerPot)) {
// 			return false;
// 		}
// 		TileEntityFlowerPot flowerPot = (TileEntityFlowerPot) tile;
// 		boolean newHas = flowerPot.getFlowerPotItem() != null;
// 		boolean changed = potHasItem != newHas;
// 		potHasItem = newHas;
// 		return changed;
// 	}
//
// 	@Override
// 	public void readFromNBT(NBTTagCompound nbt) {
// 		potHasItem = nbt.getBoolean("observer_flowerPotHasItem");
// 	}
//
// 	@Override
// 	public void writeToNBT(NBTTagCompound nbt) {
// 		nbt.setBoolean("observer_flowerPotHasItem", potHasItem);
// 	}
//
// }
