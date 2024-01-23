package pistonmc.pistonandobserver.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.mixins.piston.IMixinBlockPistonAccessor;

/**
 * Replacement impl for BlockPistonBase
 */
public class PistonHooks {

    public static boolean tryExtend(Block blockThis, World world, int x, int y, int z, int direction) {
        PistonStructure structure = new PistonStructure(world, blockThis, new BlockPos(x, y, z), direction, true);
        if (!structure.calculate()) {
            return false;
        }
        structure.moveStructure();
        return true;
    }

    public static void postRetract(BlockPistonBase blockThis, World world, int x, int y, int z, int direction) {
        IMixinBlockPistonAccessor accessor = (IMixinBlockPistonAccessor) blockThis;
        BlockPos pullingTargetPos = new BlockPos(x, y, z).offset(direction);

        BlockPos rootPos = new BlockPos(x, y, z).offset(direction, 2);
        Block rootBlock = rootPos.getBlockInWorld(world);

        boolean doNotPullBlock = !accessor.isStickyPiston();
        
        if (!doNotPullBlock) {
            /* one tick piston scenario */
            if (rootBlock == Blocks.piston_extension) {
                TileEntity tileentity = rootPos.getTileEntityInWorld(world);

                if (tileentity instanceof TileEntityPiston) {
                    TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;
                    if (tileentitypiston.getPistonOrientation() == direction && tileentitypiston.isExtending()) {
                        tileentitypiston.clearPistonTileEntity();
                        doNotPullBlock = true;
                    }
                }
            }
        }


        if (doNotPullBlock) {
            pullingTargetPos.setToAir(world);
            return;
        }

        if (rootBlock.getMaterial() != Material.air
            && IMixinBlockPistonAccessor.callCanPushBlock(rootBlock, world, rootPos.x, rootPos.y, rootPos.z, false) 
            && (rootBlock.getMobilityFlag() == 0 || rootBlock instanceof BlockPistonBase)) {

            pullingTargetPos.setToAir(world);

            PistonStructure structure = new PistonStructure(world, blockThis, new BlockPos(x, y, z), direction,
                false);

            // if structure can be pulled, move the structure, otherwise just retract the piston
            if (structure.calculate()) {
                structure.moveStructure();
            }

        }
    }
}
