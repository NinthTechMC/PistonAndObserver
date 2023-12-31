package pistonmc.flyingmachinebackport;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

/**
 * Wrapper for x,y,z
 */
public class BlockPos {
	public final int x;
	public final int y;
	public final int z;

	public BlockPos(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPos offset(int facing) {
		return this.offset(facing, 1);
	}

	@Override
	public String toString() {
		return "BlockPos(x=" + x + " y=" + y + " z=" + z + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object other) {
		return other.getClass() == BlockPos.class && equals((BlockPos) other);
	}

	public boolean equals(BlockPos other) {
		return other != null && x == other.x && y == other.y && z == other.z;
	}

	/**
	 * Offsets this BlockPos n blocks in the given direction
	 */
	public BlockPos offset(int facing, int n) {
		return n == 0 ? this
				: new BlockPos(x + Facing.offsetsXForSide[facing] * n, y + Facing.offsetsYForSide[facing] * n,
						z + Facing.offsetsZForSide[facing] * n);
	}

	public Block getBlockInWorld(World world) {
		return world.getBlock(x, y, z);
	}

	public int getBlockMetadataInWorld(World world) {
		return world.getBlockMetadata(x, y, z);
	}

	public TileEntity getTileEntityInWorld(World world) {
		return world.getTileEntity(x, y, z);
	}

	public boolean isAir(Block block, World world) {
		return block.isAir(world, x, y, z);
	}

}
