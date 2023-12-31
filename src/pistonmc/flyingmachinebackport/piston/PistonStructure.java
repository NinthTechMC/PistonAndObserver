package pistonmc.flyingmachinebackport.piston;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import pistonmc.flyingmachinebackport.BlockPos;
import pistonmc.flyingmachinebackport.ModObjects;
import pistonmc.flyingmachinebackport.observer.TileEntityObserver;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

/**
 * Back port of BlockPistonStructureHelper in 1.11, which is used to detect the structure to be moved by a piston
 */
public class PistonStructure {
	// the world
	private final World world;
	// the piston
	private final Block piston;
	// position of the piston
	private final BlockPos pistonPos;
	// the direction the structure is moving
	private final int direction;
	// root pos is the block pos that touches piston face
	private final BlockPos rootPos;
	// if piston is extending
	private final boolean extending;

	// blocks that will be moved in the structure
	private final List<BlockPos> toMove = Lists.<BlockPos>newArrayList();
	// blocks that will be destroyed by moving the structure
	private final List<BlockPos> toDestroy = Lists.<BlockPos>newArrayList();

	public PistonStructure(World world, Block piston, BlockPos pistonPos, int direction, boolean extending) {
		this.world = world;
		this.piston = piston;
		this.pistonPos = pistonPos;
		this.extending = extending;

		if (extending) {
			this.direction = direction;
			this.rootPos = pistonPos.offset(this.direction);
		} else {
			this.direction = Facing.oppositeSide[direction];
			this.rootPos = pistonPos.offset(direction, 2);
		}
	}

	/**
	 * @return true if the structure can be moved
	 */
	public boolean calculate() {
		toMove.clear();
		toDestroy.clear();
		Block rootBlock = rootPos.getBlockInWorld(world);

		// handle root block
		if (!canPushBlock(rootBlock, rootPos, false)) {
			if (rootBlock.getMobilityFlag() == 1) {
				if (extending) {
					// Only push to destroy if extending
					toDestroy.add(rootPos);
				}
				return true;
			}
			return false;

		}

		if (!this.addBlockLine(rootPos)) {
			return false;
		}

		for (int i = 0; i < toMove.size(); ++i) {

			BlockPos branchRootPos = toMove.get(i);
			Block branchRoot = branchRootPos.getBlockInWorld(world);

			if (isSlimeOrHoney(branchRoot) && !this.addBranchingBlocks(branchRoot, branchRootPos)) {
				return false;
			}
		}

		return true;

	}

	// return false if the structure cannot be moved, otherwise true
	private boolean addBlockLine(BlockPos from) {

		// add this block
		Block block = from.getBlockInWorld(world);
		if (from.isAir(block, world)) {
			return true; // air blocks don't count toward limit and cannot push other blocks
		}
		if (!canPushBlock(block, from, false)) {
			return true; // non pushable blocks are not stuck to the structure
		}
		if (from.equals(this.pistonPos)) {
			return true; // the piston is also never stuck to the structure
		}
		if (toMove.contains(from)) {
			return true; // if added, do not add again
		}

		// now this block can be moved
		if (toMove.size() > 11) {
			return false; // structure cannot be moved because too many moveable blocks are stuck to it
		}

		// for slime blocks, keep pulling slime blocks behind it
		int numBlockBehind = 0;

		if (isSlimeOrHoney(block)) {
			Block stickType = block; // slime or honey
			BlockPos blockPosBehindSlime = from;
			while (block == stickType) {
				blockPosBehindSlime = blockPosBehindSlime.offset(Facing.oppositeSide[direction]);
				Block blockBehindSlime = blockPosBehindSlime.getBlockInWorld(world);
				if (blockPosBehindSlime.isAir(blockBehindSlime, world)) {
					break; // air blocks do not interact with slime
				}
				if (isSlimeOrHoney(blockBehindSlime) && blockBehindSlime != stickType) {
					break; // slime and honey don't interact with each other
				}
				if (!canPushBlock(blockBehindSlime, blockPosBehindSlime, false)) {
					break; // stop sticking if cannot move the block
				}
				if (blockPosBehindSlime.equals(pistonPos)) {
					break; // stop sticking if the block behind is the piston. happens during extension if
							// first block is slime
				}
				block = blockBehindSlime;
				// can stick the next one
				numBlockBehind++;
				if (numBlockBehind + toMove.size() > 11) {
					return false; // the whole structure is stuck so none can be moved
				}
			}
		}

		// add all blocks behind to pull
		for (int i = numBlockBehind; i >= 0; i--) {
			toMove.add(from.offset(Facing.oppositeSide[direction], i));
		}

		// add self
		int numBlock = numBlockBehind + 1;

		// add the blocks in front of this block
		int numFront = 1;

		while (true) {
			BlockPos currentCenterBlockPos = from.offset(direction, numFront);
			int k = toMove.indexOf(currentCenterBlockPos);

			if (k > -1) {

				this.reorderListAtCollision(numBlock, k);

				for (int l = 0; l <= k + numBlock; ++l) {
					BlockPos branchRootPos = toMove.get(l);
					Block branchRoot = branchRootPos.getBlockInWorld(world);
					if (isSlimeOrHoney(branchRoot) && !this.addBranchingBlocks(branchRoot, branchRootPos)) {
						return false;
					}
				}

				return true;
			}

			Block forwardBlock = currentCenterBlockPos.getBlockInWorld(world);

			if (currentCenterBlockPos.isAir(forwardBlock, world)) {
				return true;
			}

			if (!canPushBlock(forwardBlock, currentCenterBlockPos, true)
					|| currentCenterBlockPos.equals(this.pistonPos)) {
				return false; // entire structure obstructed
			}

			if (forwardBlock.getMobilityFlag() == 1) {
				toDestroy.add(currentCenterBlockPos);
				return true;
			}

			if (toMove.size() >= 12) {
				return false;
			}

			toMove.add(currentCenterBlockPos);
			++numBlock;
			++numFront;
		}
	}

	private void reorderListAtCollision(int end, int start) {
		List<BlockPos> first = Lists.<BlockPos>newArrayList();
		List<BlockPos> last = Lists.<BlockPos>newArrayList();
		List<BlockPos> middle = Lists.<BlockPos>newArrayList();
		first.addAll(toMove.subList(0, start));
		last.addAll(toMove.subList(toMove.size() - end, toMove.size()));
		middle.addAll(toMove.subList(start, toMove.size() - end));
		toMove.clear();
		toMove.addAll(first);
		toMove.addAll(last);
		toMove.addAll(middle);
	}

	private boolean addBranchingBlocks(Block stickType, BlockPos from) {
		int[] facings = { 0, 1, 2, 3, 5, 4 }; // east is checked before west in EnumFacing which is used in 1.11
		for (int facing : facings) {
			BlockPos branchPos = from.offset(facing);
			Block branchBlock = branchPos.getBlockInWorld(world);
			if (isSlimeOrHoney(branchBlock) && stickType != branchBlock) {
				// slime and honey don't interact with each other, skip
				continue;
			}
			if (!isOnSameAxis(facing, direction) && !this.addBlockLine(branchPos)) {
				return false;
			}
		}

		return true;
	}

	private boolean isOnSameAxis(int dir1, int dir2) {
		return dir1 == dir2 || dir1 == Facing.oppositeSide[dir2];

	}

	private boolean isSlimeOrHoney(Block block) {
		return block == ModObjects.blockPistonConnectorHoney || block == ModObjects.blockPistonConnectorSlime;
	}

	/**
	 * returns true if the specified block can be moved by piston
	 */
	private boolean canPushBlock(Block block, BlockPos pos, boolean shouldDestroy) {
		return BlockPistonHooks.canPushBlock(block, world, pos, shouldDestroy);
	}

	/**
	 * Move the structure, must be called after calling `calculate`
	 */
	public void moveStructure() {

		int updateSize = toMove.size() + toDestroy.size();
		List<BlockPos> pistonAirs = new ArrayList<BlockPos>(updateSize);
		List<PistonSource> notifications = new ArrayList<PistonSource>(updateSize);

		// first destroy all the blocks in toDestroy
		for (int i = toDestroy.size() - 1; i >= 0; i--) {
			BlockPos destroyPos = toDestroy.get(i);
			Block block = destroyPos.getBlockInWorld(world);
			// Forge: With our change to how snowballs are dropped this needs to disallow to
			// mimic vanilla behavior.
			float chance = block instanceof BlockSnow ? -1.0f : 1.0f;
			block.dropBlockAsItemWithChance(world, destroyPos.x, destroyPos.y, destroyPos.z,
					destroyPos.getBlockMetadataInWorld(world), chance, 0);
			// Set to air
			pistonAirs.add(destroyPos);
			world.setBlock(destroyPos.x, destroyPos.y, destroyPos.z, ModObjects.blockPistonAir, 0, 3);
			notifications.add(new PistonSource(destroyPos, block));
		}

		// then move all the blocks
		for (int i = toMove.size() - 1; i >= 0; i--) {
			BlockPos sourcePos = toMove.get(i);
			Block blockToMove = sourcePos.getBlockInWorld(world);
			int metaOfBlockToMove = sourcePos.getBlockMetadataInWorld(world);
			// set to air, no notify
			pistonAirs.add(sourcePos);
			world.setBlock(sourcePos.x, sourcePos.y, sourcePos.z, ModObjects.blockPistonAir, 0, 2);

			BlockPos targetPos = sourcePos.offset(direction);
			Block movingBlock;
			TileEntity movingTileEntity;
			if (blockToMove == ModObjects.blockObserver) {
				movingBlock = ModObjects.blockObserver;
				movingTileEntity = new TileEntityObserver(direction);
			} else {
				movingBlock = Blocks.piston_extension;
				movingTileEntity = new TileEntityPiston(blockToMove, metaOfBlockToMove, direction, true, false);
			}
			world.setBlock(targetPos.x, targetPos.y, targetPos.z, movingBlock, metaOfBlockToMove, 7);
			world.setTileEntity(targetPos.x, targetPos.y, targetPos.z, movingTileEntity);
			notifications.add(new PistonSource(sourcePos, blockToMove));
		}

		int pistonDirection = extending ? direction : Facing.oppositeSide[direction];
		BlockPos pistonTarget = pistonPos.offset(pistonDirection);
		// extend the piston
		if (extending) {
			boolean sticky = piston == Blocks.sticky_piston;
			int meta = direction | (sticky ? 8 : 0);
			world.setBlock(pistonTarget.x, pistonTarget.y, pistonTarget.z, Blocks.piston_extension, meta, 4);
			world.setTileEntity(pistonTarget.x, pistonTarget.y, pistonTarget.z,
					new TileEntityPiston(Blocks.piston_head, meta, direction, true, true));
		}
		// set tile entities for piston air, if it is not overriden by other moving
		// blocks
		for (BlockPos air : pistonAirs) {
			if (air.getBlockInWorld(world) == ModObjects.blockPistonAir) {
				world.setTileEntity(air.x, air.y, air.z, new TileEntityPistonAir());
			}
		}

		// notify change
		for (PistonSource change : notifications) {
			BlockPos sourcePos = change.pos;
			world.notifyBlocksOfNeighborChange(sourcePos.x, sourcePos.y, sourcePos.z, change.block);
		}

		world.notifyBlocksOfNeighborChange(pistonTarget.x, pistonTarget.y, pistonTarget.z, Blocks.piston_head);
		world.notifyBlocksOfNeighborChange(pistonPos.x, pistonPos.y, pistonPos.z, piston);

	}

}
