package com.piston.mc.flyingmachinebackport.piston;

import com.piston.mc.flyingmachinebackport.BlockPos;

import net.minecraft.tileentity.TileEntityPiston;

/**
 * Wrapper for block state after piston is moved
 */
public class PistonTarget {
	public final BlockPos pos;
	public final int meta;
	public final TileEntityPiston movingBlock;

	public PistonTarget(BlockPos pos, int meta, TileEntityPiston movingBlock) {
		super();
		this.pos = pos;
		this.meta = meta;
		this.movingBlock = movingBlock;
	}
}
