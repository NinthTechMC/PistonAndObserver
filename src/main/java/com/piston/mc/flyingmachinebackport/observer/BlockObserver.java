package com.piston.mc.flyingmachinebackport.observer;

import com.piston.mc.flyingmachinebackport.BlockPos;
import com.piston.mc.flyingmachinebackport.ModInfo;
import com.piston.mc.flyingmachinebackport.ModObjects;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * The Observer Block
 * 
 * Unfortunately, 1.7.10 has no mechanism to store the state needed by an observer without using
 * TileEntity, which would make the observer immovable by pistons and thus make flying machines impossible.
 * 
 * The workaround happens in the piston class, where observers are treated specially so that they are movable.
 */
public class BlockObserver extends Block implements ITileEntityProvider {

	@SideOnly(Side.CLIENT)
	private IIcon topBottomIcon;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;
	@SideOnly(Side.CLIENT)
	private IIcon backIcon;
	@SideOnly(Side.CLIENT)
	private IIcon backIconOn;

	public BlockObserver() {
		super(Material.rock);
		this.setHardness(3.0F);
		this.setBlockName("observer");
		this.setBlockTextureName(ModInfo.Id+":observer_front");
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	public static int getObserverBackFacing(int meta) {
		return meta & 7;
	}

	/**
	 * Determine if the metadata is related to something powered.
	 */
	public static boolean isOn(int meta) {
		return (meta & 8) != 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		this.blockIcon = registry.registerIcon(ModInfo.Id+":observer_front");
		this.backIcon = registry.registerIcon(ModInfo.Id+":observer_back");
		this.backIconOn = registry.registerIcon(ModInfo.Id+":observer_back_on");
		this.sideIcon = registry.registerIcon(ModInfo.Id+":observer_side");
		this.topBottomIcon = registry.registerIcon(ModInfo.Id+":observer_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		int k = getObserverBackFacing(meta);
		if (k > 5) {
			// error case
			return this.blockIcon;
		}
		if (side == k) {
			return isOn(meta) ? this.backIconOn : this.backIcon;
		}
		if (side == Facing.oppositeSide[k]) {
			return this.blockIcon;
		}
		if (k == 0 /* down */ || k == 1 /* up */) {
			if (side == 2 /* north */ || side == 3 /* south */) {
				return this.topBottomIcon;
			}
			return this.sideIcon;
		}
		if (side == 0 /* down */ || side == 1 /* up */) {
			return this.topBottomIcon;

		}
		return this.sideIcon;
	}

	@Override
	public int getRenderType() {
		return ObserverRenderStationary.renderId;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		// use the same logic as piston to determine orientation
		int l = BlockPistonBase.determineOrientation(world, x, y, z, entity);
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileEntityObserver();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityObserver) {
			TileEntityObserver observer = (TileEntityObserver) tileEntity;
			observer.activateIfSeenChange();
		}
		super.onNeighborBlockChange(world, x, y, z, block);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		super.breakBlock(world, x, y, z, block, meta);
		notifyChangeForRedstone(world, x, y, z);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int direction) {
		return this.isProvidingWeakPower(world, x, y, z, direction);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int direction) {
		int meta = world.getBlockMetadata(x, y, z);
		if (!isOn(meta)) {
			return 0;
		}
		return getObserverBackFacing(meta) == Facing.oppositeSide[direction] ? 15 : 0;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	public void notifyChangeForRedstone(World world, int x, int y, int z) {
		int l = getObserverBackFacing(world.getBlockMetadata(x, y, z));
		int backX = x + Facing.offsetsXForSide[l];
		int backY = y + Facing.offsetsYForSide[l];
		int backZ = z + Facing.offsetsZForSide[l];
		world.notifyBlockOfNeighborChange(backX, backY, backZ, this);
		world.notifyBlocksOfNeighborChange(backX, backY, backZ, this, Facing.oppositeSide[l]);
	}

	/**
	 * Checks if the block is a solid face on the given side, used by placement
	 * logic.
	 *
	 * @param world The current world
	 * @param x     X Position
	 * @param y     Y position
	 * @param z     Z position
	 * @param side  The side to check
	 * @return True if the block is solid on the specified side.
	 */
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false; // when moving by piston, need to be not opaque
	}

	/**
	 * Test if the block changed should be ignored. for example, a block pushed by
	 * piston or a moving observer
	 * 
	 * @param world
	 * @param pos
	 * @param newBlock
	 * @return
	 */
	public static boolean shouldIgnoreBlock(World world, BlockPos pos, Block newBlock) {
		if (newBlock == Blocks.piston_extension || newBlock == ModObjects.blockPistonAir) {
			return true;
		}
		if (newBlock != ModObjects.blockObserver) {
			return false;
		}
		// observer special case
		TileEntityObserver observer = (TileEntityObserver) pos.getTileEntityInWorld(world);
		if (observer.getMoveDirection() != -1) {
			return true; // ignore moving observer
		}
		return false;
	}

}
