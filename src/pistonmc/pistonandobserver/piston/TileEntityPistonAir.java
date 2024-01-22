package pistonmc.pistonandobserver.piston;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pistonmc.pistonandobserver.ModObjects;

public class TileEntityPistonAir extends TileEntity {
	/*
	 * Tile entity to fill up spaces that will be air after the piston is done moving. Set itself to air after
	 * piston is done moving. 
	 * 
	 * This is so that observers can fire after pistons are
	 * done moving (i.e. seeing block changed to PistonAir is ignored, but fired when PistonAir is changed to Air)
	 */
	private float progress;
	private float lastProgress;

	public TileEntityPistonAir() {
	}

	/**
	 * removes a piston's tile entity (and if the piston is moving, stops it)
	 */
	public void clearPistonTileEntity() {
		if (this.lastProgress < 1.0F && this.worldObj != null) {
			this.lastProgress = this.progress = 1.0F;
			this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();

			this.setToAir();
		}
	}

	@Override
	public void updateEntity() {
		this.lastProgress = this.progress;

		if (this.lastProgress >= 1.0F) {
			this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
			this.invalidate();

			this.setToAir();
		} else {
			this.progress += 0.5F;

			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}
		}
	}
	
	private void setToAir() {
		if (this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == ModObjects.piston_air) {
			this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, Blocks.air);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.lastProgress = this.progress = nbt.getFloat("progress");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("progress", this.lastProgress);
	}
}
