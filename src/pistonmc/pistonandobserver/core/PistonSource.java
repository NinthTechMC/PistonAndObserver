package pistonmc.pistonandobserver.core;

import net.minecraft.block.Block;

/**
 * Wrapper for the block states before a piston is moved
 */
public class PistonSource {
	public final BlockPos pos;
	public final Block block;

	public PistonSource(BlockPos pos, Block block) {
		super();
		this.pos = pos;
		this.block = block;
	}
}
