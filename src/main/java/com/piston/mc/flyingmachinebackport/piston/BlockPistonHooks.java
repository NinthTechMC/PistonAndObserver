package com.piston.mc.flyingmachinebackport.piston;

import com.piston.mc.flyingmachinebackport.BlockPos;
import com.piston.mc.flyingmachinebackport.ModObjects;
import com.piston.mc.flyingmachinebackport.observer.TileEntityObserver;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;

/**
 * Hooks for BlockPistonBase, as well as other utilities
 *
 */
public class BlockPistonHooks {
	private static final int EVENT_EXTEND = 0;
	private static final int EVENT_RETRACT = 1;

	// Hooks BlockPistonBase.onBlockEventReceived, used to retract or extend the piston
	public static boolean onBlockEventReceived(Block blockThis, World world, int x, int y, int z, int eventData,
			int meta) {
		if (!world.isRemote) {
			boolean isPowered = isIndirectlyPowered(world, x, y, z, meta);

			if (isPowered && eventData == EVENT_RETRACT) {
				world.setBlockMetadataWithNotify(x, y, z, meta | 8, 2);
				return false;
			}

			if (!isPowered && eventData == EVENT_EXTEND) {
				return false;
			}
		}
		
		// check if the piston can extend
		int direction = BlockPistonBase.getPistonOrientation(meta);
		if (eventData == EVENT_EXTEND) {

			PistonStructure structure = new PistonStructure(world, blockThis, new BlockPos(x, y, z), direction, true);
			if (!structure.calculate()) {
				return false;
			}
			structure.moveStructure();

			world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.out", 0.5F,
					world.rand.nextFloat() * 0.25F + 0.6F);
		} else if (eventData == EVENT_RETRACT) {
			BlockPos pullingTargetPos = new BlockPos(x, y, z).offset(direction);
			TileEntity tileArm = pullingTargetPos.getTileEntityInWorld(world);

			if (tileArm instanceof TileEntityPiston) {
				((TileEntityPiston) tileArm).clearPistonTileEntity();
			}
			if (tileArm instanceof TileEntityObserver) {
				((TileEntityObserver) tileArm).stopMoving();
			}

			// retract this piston
			world.setBlock(x, y, z, Blocks.piston_extension, meta, 2);
			world.setTileEntity(x, y, z, new TileEntityPiston(blockThis, meta, meta, false, true));

			if (blockThis == Blocks.sticky_piston) {

				BlockPos rootPos = new BlockPos(x, y, z).offset(direction, 2);
				Block rootBlock = rootPos.getBlockInWorld(world);

				boolean doNotPullBlock = false; /* one tick piston scenario */

				if (rootBlock == Blocks.piston_extension) {
					TileEntity tileentity = rootPos.getTileEntityInWorld(world);

					if (tileentity instanceof TileEntityPiston) {
						TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;
						if (tileentitypiston.getPistonOrientation() == direction && tileentitypiston.isExtending()) {
							tileentitypiston.clearPistonTileEntity();
							doNotPullBlock = true;
						}
					}
				} else if (rootBlock == ModObjects.blockObserver) {
					TileEntity tileentity = rootPos.getTileEntityInWorld(world);

					if (tileentity instanceof TileEntityObserver) {
						TileEntityObserver observer = (TileEntityObserver) tileentity;
						if (observer.getMoveDirection() == direction) {
							observer.stopMoving();
							doNotPullBlock = true;
						}
					}
				}

				if (!doNotPullBlock && rootBlock.getMaterial() != Material.air
						&& canPushBlock(rootBlock, world, rootPos, false) && (rootBlock.getMobilityFlag() == 0
								|| rootBlock == Blocks.piston || rootBlock == Blocks.sticky_piston)) {

					world.setBlock(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
							ModObjects.blockPistonAir, 0, 3);
					world.setTileEntity(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
							new TileEntityPistonAir());
					PistonStructure structure = new PistonStructure(world, blockThis, new BlockPos(x, y, z), direction,
							false);

					// if structure can be pulled, move the structure, otherwise just retract the piston
					if (structure.calculate()) {
						structure.moveStructure();
					}

				} else if (!doNotPullBlock) {

					world.setBlock(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
							ModObjects.blockPistonAir, 0, 3);
					world.setTileEntity(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
							new TileEntityPistonAir());
				}

			} else {
				world.setBlock(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z, ModObjects.blockPistonAir, 0,
						3);
				world.setTileEntity(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
						new TileEntityPistonAir());
			}

			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.in", 0.5F,
					world.rand.nextFloat() * 0.15F + 0.6F);

		}

		return true;
	}

	public static boolean canPushBlock(Block block, World world, BlockPos pos, boolean shouldDestroy) {
		if (block == Blocks.obsidian) {
			return false;
		}
		if (block == ModObjects.blockObserver) {
			TileEntity tile = pos.getTileEntityInWorld(world);
			if (!(tile instanceof TileEntityObserver)) {
				// error case
				return false;
			}
			return ((TileEntityObserver) tile).getMoveDirection() == -1; // stationary observers can be moved by pistons

		}
		if (!isPiston(block)) {
			if (block.getBlockHardness(world, pos.x, pos.y, pos.z) == -1.0F) {
				return false;
			}

			if (block.getMobilityFlag() == 2) {
				return false;
			}

			if (block.getMobilityFlag() == 1) {
				return shouldDestroy;
			}
		} else if (BlockPistonBase.isExtended(pos.getBlockMetadataInWorld(world))) {
			return false;
		}
		return !pos.getBlockInWorld(world).hasTileEntity(pos.getBlockMetadataInWorld(world));
	}

	public static boolean isPiston(Block b) {
		return b == Blocks.piston || b == Blocks.sticky_piston;
	}

	// The same method redeclared here for access. can also use access transformer
	// but this is easier
	private static boolean isIndirectlyPowered(World world, int x, int y, int z, int meta) {
		// piston face
		if (meta != 0 && world.getIndirectPowerOutput(x, y - 1, z, 0)) {
			return true;
		}
		if (meta != 1 && world.getIndirectPowerOutput(x, y + 1, z, 1)) {
			return true;
		}
		if (meta != 2 && world.getIndirectPowerOutput(x, y, z - 1, 2)) {
			return true;
		}
		if (meta != 3 && world.getIndirectPowerOutput(x, y, z + 1, 3)) {
			return true;
		}

		if (meta != 4 && world.getIndirectPowerOutput(x - 1, y, z, 4)) {
			return true;
		}

		if (meta != 5 && world.getIndirectPowerOutput(x + 1, y, z, 5)) {
			return true;
		}

		// bud
		if (world.getIndirectPowerOutput(x, y, z, 0)) {
			return true;
		}
		if (world.getIndirectPowerOutput(x, y + 2, z, 1)) {
			return true;
		}
		if (world.getIndirectPowerOutput(x, y + 1, z - 1, 2)) {
			return true;
		}
		if (world.getIndirectPowerOutput(x, y + 1, z + 1, 3)) {
			return true;
		}
		if (world.getIndirectPowerOutput(x - 1, y + 1, z, 4)) {
			return true;
		}
		if (world.getIndirectPowerOutput(x + 1, y + 1, z, 5)) {
			return true;
		}
		return false;

	}

	// Hooks BlockPistonBase.canExtend
	public static boolean canExtend(World world, int x, int y, int z, int direction) {
		// check is done in onBlockEventReceived
		return true;
	}
}
