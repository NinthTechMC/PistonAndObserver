package pistonmc.pistonandobserver.observer;

import java.util.Random;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pistonmc.pistonandobserver.ModInfo;
import pistonmc.pistonandobserver.api.IBlockObservable;
import pistonmc.pistonandobserver.api.IBlockObserver;
import pistonmc.pistonandobserver.api.ObserverAPI;
import pistonmc.pistonandobserver.api.ObserverEvent;
import pistonmc.pistonandobserver.core.Config;

/**
 * The Observer Block
 */
public class BlockObserver extends Block implements IBlockObserver, IBlockObservable {

	public static int getBackFacing(int meta) {
		return meta & 7;
	}

	public static boolean isOn(int meta) {
		return (meta & 8) != 0;
	}

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
		this.setBlockTextureName(ModInfo.MODID+":observer_front");
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

    /*
     * IBlockObserver
     */

    @Override
    public int getObserverBackFacing(World world, int x, int y, int z) {
        return getBackFacing(world.getBlockMetadata(x, y, z));
    }

    @Override
    public int getObserverFrontFacing(World world, int x, int y, int z) {
        return this.getObserverBackFacing(world, x, y, z) ^ 1;
    }

    @Override
    public boolean isObserverOn(World world, int x, int y, int z) {
        return isOn(world.getBlockMetadata(x, y, z));
    }

    @Override
    public void onObserverUpdate(World world, int x, int y, int z, int frontSide) {
        if (Config.observerDelay == 0) {
            this.turnOnObserver(world, x, y, z, frontSide ^ 1);
            return;
        }
        world.scheduleBlockUpdate(x, y, z, this, Config.observerDelay);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isRemote) {
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if (isOn(meta)) {
            this.turnOffObserver(world, x, y, z, meta);
        } else {
            this.turnOnObserver(world, x, y, z, meta);
        }
    }

    public void turnOffObserver(World world, int x, int y, int z, int meta) {
        world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
    }

    public void turnOnObserver(World world, int x, int y, int z, int meta) {
        world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
        world.scheduleBlockUpdate(x, y, z, this, Config.observerDuration);
    }


    /*
     * Icon
     */

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		this.blockIcon = registry.registerIcon(ModInfo.MODID+":observer_front");
		this.backIcon = registry.registerIcon(ModInfo.MODID+":observer_back");
		this.backIconOn = registry.registerIcon(ModInfo.MODID+":observer_back_on");
		this.sideIcon = registry.registerIcon(ModInfo.MODID+":observer_side");
		this.topBottomIcon = registry.registerIcon(ModInfo.MODID+":observer_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		int k = getBackFacing(meta);
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
        return RenderObserver.renderId;
    }

    /*
     * Block
     */

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        // use the same logic as piston to determine orientation
        int l = BlockPistonBase.determineOrientation(world, x, y, z, entity);
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
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
		return getBackFacing(meta) == (direction^1) ? 15 : 0;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {    
        /* input side
         *  -1: UP
         *   0: NORTH
         *   1: EAST
         *   2: SOUTH
         *   3: WEST
        */
        switch (side) {
            case 0: return getBackFacing(world.getBlockMetadata(x, y, z)) == 2;
            case 1: return getBackFacing(world.getBlockMetadata(x, y, z)) == 5;
            case 2: return getBackFacing(world.getBlockMetadata(x, y, z)) == 3;
            case 3: return getBackFacing(world.getBlockMetadata(x, y, z)) == 4;
            default: return false;
        }
    }


	public void notifyChangeForRedstone(World world, int x, int y, int z) {
		int l = getBackFacing(world.getBlockMetadata(x, y, z));
		int backX = x + Facing.offsetsXForSide[l];
		int backY = y + Facing.offsetsYForSide[l];
		int backZ = z + Facing.offsetsZForSide[l];
		world.notifyBlockOfNeighborChange(backX, backY, backZ, this);
		world.notifyBlocksOfNeighborChange(backX, backY, backZ, this, Facing.oppositeSide[l]);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

    @Override
    public void onObserverEvent(ObserverEvent event) {
        if (event.oldBlock == Blocks.piston_extension) {
            // if the observer is moved by a piston, notify itself
            ObserverAPI.triggerObserverAt(event.world, event.x, event.y, event.z);
        }
        ObserverAPI.notifyObserversAround(event.world, event.x, event.y, event.z);
    }
}
