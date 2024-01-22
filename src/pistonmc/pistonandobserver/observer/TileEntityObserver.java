// package pistonmc.flyingmachine.observer;
//
// import java.util.List;
// import cpw.mods.fml.relauncher.Side;
// import cpw.mods.fml.relauncher.SideOnly;
// import net.minecraft.block.Block;
// import net.minecraft.block.BlockRedstoneDiode;
// import net.minecraft.entity.Entity;
// import net.minecraft.init.Blocks;
// import net.minecraft.nbt.NBTTagCompound;
// import net.minecraft.tileentity.TileEntity;
// import net.minecraft.tileentity.TileEntityFlowerPot;
// import net.minecraft.util.AxisAlignedBB;
// import net.minecraft.util.Facing;
// import pistonmc.flyingmachine.BlockPos;
// import pistonmc.flyingmachine.ModObjects;
// import pistonmc.flyingmachine.observer.module.ObserverModule;
// import pistonmc.flyingmachine.observer.module.ObserverModuleBrewingStand;
// import pistonmc.flyingmachine.observer.module.ObserverModuleDoor;
// import pistonmc.flyingmachine.observer.module.ObserverModuleFlowerPot;
// import pistonmc.flyingmachine.observer.module.ObserverModuleGrass;
// import pistonmc.flyingmachine.observer.module.ObserverModuleNoteBlock;
// import pistonmc.flyingmachine.observer.module.ObserverModulePersistent;
// import pistonmc.flyingmachine.observer.module.ObserverModuleRepeater;
//
// /**
//  * Observer TileEntity that provides the "observe" logic
//  */
// public class TileEntityObserver extends TileEntity {
// 	// The ticking logic has changed in later versions. These numbers mimic the behavior the closest
// 	public static final int ON_DURATION = 1;
// 	public static final int OFF_COOLDOWN = 3;
// 	private Block lastSeenBlock;
// 	private int lastSeenMeta;
// 	private int coolDown;
// 	private int duration;
// 	/** direction this observer is being pushed. -1 if stationary */
// 	private int moveDirection;
// 	private float moveProgress;
// 	private float moveLastProgress;
// 	/** Helper to watch change every tick if this is not null */
// 	private ObserverModule observerModule;
//
// 	public TileEntityObserver() {
// 		lastSeenBlock = null;
// 		lastSeenMeta = -1;
// 		coolDown = 0;
// 		duration = 0;
// 		moveDirection = -1;
// 		moveProgress = 0;
// 		moveLastProgress = 0;
// 		observerModule = null;
// 	}
//
// 	public TileEntityObserver(int moveDirection) {
// 		this();
// 		this.moveDirection = moveDirection;
// 	}
//
// 	/**
// 	 * Try activating this observer
// 	 * 
// 	 * For regular blocks, it is only called when neighbour changes (passive observe). For special blocks (with module),
// 	 * it is called every tick (active observe)
// 	 * 
// 	 * It will activate if the new block is not the same as last seen (id and meta)
// 	 * If the new block is related to moving piston, it will not activate until the
// 	 * piston finishes moving (i.e. the block changes again)
// 	 */
// 	public void activateIfSeenChange() {
// 		if (!worldObj.isRemote && moveDirection == -1) {
// 			BlockPos frontPos = getObservedBlockPos();
// 			Block newBlock = frontPos.getBlockInWorld(worldObj);
// 			int newMeta = frontPos.getBlockMetadataInWorld(worldObj);
// 			if (newBlock != lastSeenBlock || newMeta != lastSeenMeta) {
// 				lastSeenBlock = newBlock;
// 				lastSeenMeta = newMeta;
// 				if (BlockObserver.shouldIgnoreBlock(worldObj, frontPos, newBlock)) {
// 					// Ignore piston movements, but update the state and remove module
// 					removeObserverModule();
// 					return;
// 				}
// 				// Check if module needs to be removed
// 				if (observerModule != null) {
// 					if (!observerModule.shouldKeepWatching(newBlock, newMeta)) {
// 						removeObserverModule();
// 					}
// 				}
// 			} else {
// 				// if block & meta are the same, maybe module wants to update ?
// 				if (observerModule == null) {
// 					return;
// 				}
// 				boolean changed = observerModule.isChanged(worldObj, frontPos, newMeta);
// 				if (!changed) {
// 					return;
// 				}
// 				// If did change, check if need to keep watching
// 				if (!observerModule.shouldKeepWatching(newBlock, newMeta)) {
// 					removeObserverModule();
// 				}
// 			}
//
// 			// check if need to create a module
// 			if (observerModule == null) {
// 				createObserverModuleIfNeeded(newBlock);
// 				if (observerModule != null) {
// 					// update initial state of module
// 					observerModule.isChanged(worldObj, frontPos, newMeta);
// 				}
// 			}
// 			// turn the observer on
// 			duration = ON_DURATION;
// 			markDirty();
// 		}
// 	}
//
// 	@Override
// 	public void updateEntity() {
// 		// piston functionality
// 		if (this.moveDirection >= 0) {
// 			this.moveLastProgress = this.moveProgress;
//
// 			if (this.moveLastProgress >= 1.0F) {
// 				this.pushEntities(1.0F, 0.25F);
// 				// moving finished, force update
// 				this.onStopMoving();
// 			} else {
// 				this.moveProgress += 0.5F;
// 				if (this.moveProgress >= 1.0F) {
// 					this.moveProgress = 1.0F;
// 				}
// 				this.pushEntities(this.moveProgress, this.moveProgress - this.moveLastProgress + 0.0625F);
// 			}
// 		}
//
// 		if (!worldObj.isRemote) {
// 			// initial detection
// 			if (lastSeenBlock == null || lastSeenMeta < 0) {
// 				detectBlockOnInitialPlace();
// 			}
//
// 			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
// 			if (coolDown > 0) {
// 				coolDown--;
// 			}
// 			if (BlockObserver.isOn(meta)) {
// 				// if is on, turn off
// 				if (duration > 0) {
// 					duration--;
// 				} else {
// 					worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta & 7, 3);
// 					ModObjects.blockObserver.notifyChangeForRedstone(worldObj, xCoord, yCoord, zCoord);
// 					coolDown = OFF_COOLDOWN;
// 					this.markDirty();
// 				}
// 			} else if (duration > 0 && coolDown <= 0) {
// 				// If should turn on, turn on
// 				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta | 8, 3);
// 				ModObjects.blockObserver.notifyChangeForRedstone(worldObj, xCoord, yCoord, zCoord);
// 				this.markDirty();
// 			} else if (observerModule != null && duration == 0) {
// 				// actively check if observer module is active (i.e. not null)
// 				activateIfSeenChange();
// 			}
// 		}
// 	}
//
// 	private void detectBlockOnInitialPlace() {
// 		BlockPos frontPos = getObservedBlockPos();
// 		lastSeenBlock = frontPos.getBlockInWorld(worldObj);
// 		lastSeenMeta = frontPos.getBlockMetadataInWorld(worldObj);
// 		createObserverModuleIfNeeded(lastSeenBlock);
// 		if (observerModule != null) {
// 			// update module state
// 			observerModule.isChanged(worldObj, frontPos, lastSeenMeta);
// 		}
// 	}
//
// 	// Piston Functions
//
// 	private BlockPos getObservedBlockPos() {
// 		int meta = this.getBlockMetadata();
// 		int back = BlockObserver.getObserverBackFacing(meta);
// 		int front = Facing.oppositeSide[back];
// 		return new BlockPos(xCoord, yCoord, zCoord).offset(front);
// 	}
//
// 	public int getMoveDirection() {
// 		return moveDirection;
// 	}
//
// 	private void pushEntities(float progress, float moveAmount) {
//
// 		// Get pushing AABB
// 		AxisAlignedBB axisalignedbb = Blocks.piston_extension.func_149964_a(this.worldObj, this.xCoord, this.yCoord,
// 				this.zCoord, ModObjects.blockObserver, 1.0F - progress, this.moveDirection);
//
// 		if (axisalignedbb != null) {
// 			@SuppressWarnings("unchecked")
// 			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity) null, axisalignedbb);
// 			for (Entity entity : list) {
// 				entity.moveEntity(moveAmount * Facing.offsetsXForSide[this.moveDirection],
// 						moveAmount * Facing.offsetsYForSide[this.moveDirection],
// 						moveAmount * Facing.offsetsZForSide[this.moveDirection]);
//
// 			}
//
// 		}
// 	}
//
// 	public void stopMoving() {
// 		if (this.moveLastProgress < 1.0F && this.worldObj != null) {
// 			this.moveLastProgress = this.moveProgress = 1.0F;
// 			this.onStopMoving();
// 		}
// 	}
//
// 	private void onStopMoving() {
// 		detectBlockOnInitialPlace();
// 		moveDirection = -1;
// 		// force turn on by setting duration = 1
// 		duration = ON_DURATION;
// 		this.markDirty();
// 		this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, ModObjects.blockObserver);
// 	}
//
// 	@Override
// 	public void readFromNBT(NBTTagCompound nbt) {
// 		super.readFromNBT(nbt);
// 		int id = nbt.getInteger("lastSeenBlock");
// 		this.lastSeenBlock = id < 0 ? null : Block.getBlockById(id);
// 		this.lastSeenMeta = nbt.getInteger("lastSeenMeta");
// 		this.duration = nbt.getInteger("duration");
// 		this.coolDown = nbt.getInteger("coolDown");
// 		this.moveDirection = nbt.getInteger("moveDirection");
// 		this.moveLastProgress = this.moveProgress = nbt.getFloat("moveProgress");
// 		if (this.moveDirection == -1) {
// 			createObserverModuleIfNeeded(this.lastSeenBlock);
// 			if (observerModule != null) {
// 				observerModule.readFromNBT(nbt);
// 			}
// 		}
//
// 	}
//
// 	@Override
// 	public void writeToNBT(NBTTagCompound nbt) {
// 		super.writeToNBT(nbt);
// 		nbt.setInteger("lastSeenBlock", this.lastSeenBlock == null ? -1 : Block.getIdFromBlock(this.lastSeenBlock));
// 		nbt.setInteger("lastSeenMeta", this.lastSeenMeta);
// 		nbt.setInteger("duration", duration);
// 		nbt.setInteger("coolDown", coolDown);
//
// 		nbt.setInteger("moveDirection", this.moveDirection);
// 		nbt.setFloat("moveProgress", this.moveLastProgress);
// 		if (this.observerModule != null) {
// 			observerModule.writeToNBT(nbt);
// 		}
// 	}
//
// 	private void createObserverModuleIfNeeded(Block block) {
// 		if (block == null) {
// 			return;
// 		}
// 		if (block == Blocks.hopper || block == Blocks.farmland || block == Blocks.dispenser
// 				|| block == Blocks.dropper) {
// 			observerModule = new ObserverModulePersistent(block);
// 		} else if (block == Blocks.brewing_stand) {
// 			observerModule = new ObserverModuleBrewingStand();
// 		} else if (block == Blocks.flower_pot) {
// 			// only need to observe if the flower pot is empty
// 			if (worldObj != null) {
// 				BlockPos frontPos = getObservedBlockPos();
// 				TileEntity tileEntity = frontPos.getTileEntityInWorld(worldObj);
// 				if (tileEntity instanceof TileEntityFlowerPot) {
// 					if (((TileEntityFlowerPot) tileEntity).getFlowerPotItem() != null) {
// 						return;
// 					}
// 				}
// 			}
// 			observerModule = new ObserverModuleFlowerPot();
// 		} else if (BlockRedstoneDiode.isRedstoneRepeaterBlockID(block)) {
// 			observerModule = new ObserverModuleRepeater();
// 		} else if (block == Blocks.grass) {
// 			observerModule = new ObserverModuleGrass();
// 		} else if (block == Blocks.noteblock) {
// 			observerModule = new ObserverModuleNoteBlock();
// 		} else if (block == Blocks.iron_door) {
// 			observerModule = new ObserverModuleDoor();
// 		}
// 	}
//
// 	private void removeObserverModule() {
// 		observerModule = null;
// 	}
//
// 	@SideOnly(Side.CLIENT)
// 	public float getProgressWithPartialTick(float partialTick) {
// 		if (partialTick > 1.0F) {
// 			partialTick = 1.0F;
// 		}
//
// 		return this.moveLastProgress + (this.moveProgress - this.moveLastProgress) * partialTick;
// 	}
//
// 	@SideOnly(Side.CLIENT)
// 	public float getXWithPartialTick(float partialTick) {
// 		if (this.moveDirection < 0) {
// 			return 0;
// 		}
// 		return (this.getProgressWithPartialTick(partialTick) - 1.0F) * Facing.offsetsXForSide[this.moveDirection];
//
// 	}
//
// 	@SideOnly(Side.CLIENT)
// 	public float getYWithPartialTick(float partialTick) {
// 		if (this.moveDirection < 0) {
// 			return 0;
// 		}
// 		return (this.getProgressWithPartialTick(partialTick) - 1.0F) * Facing.offsetsYForSide[this.moveDirection];
//
// 	}
//
// 	@SideOnly(Side.CLIENT)
// 	public float getZWithPartialTick(float partialTick) {
// 		if (this.moveDirection < 0) {
// 			return 0;
// 		}
// 		return (this.getProgressWithPartialTick(partialTick) - 1.0F) * Facing.offsetsZForSide[this.moveDirection];
//
// 	}
// }
