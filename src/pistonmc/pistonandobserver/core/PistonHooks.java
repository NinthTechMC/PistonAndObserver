package pistonmc.pistonandobserver.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.ModObjects;
import pistonmc.pistonandobserver.mixins.piston.IMixinBlockPistonAccessor;
import pistonmc.pistonandobserver.piston.TileEntityPistonAir;

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
            // TODO: observer
            // else if (rootBlock == ModObjects.blockObserver) {
            //             TileEntity tileentity = rootPos.getTileEntityInWorld(world);
            //
            //             if (tileentity instanceof TileEntityObserver) {
            //                 TileEntityObserver observer = (TileEntityObserver) tileentity;
            //                 if (observer.getMoveDirection() == direction) {
            //                     observer.stopMoving();
            //                     doNotPullBlock = true;
            //                 }
            //             }
            //         }
            //
        }


        if (doNotPullBlock) {
            world.setBlock(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
                ModObjects.piston_air, 0, 3);
            world.setTileEntity(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
                new TileEntityPistonAir());
            return;
        }

        if (rootBlock.getMaterial() != Material.air
            && IMixinBlockPistonAccessor.callCanPushBlock(rootBlock, world, rootPos.x, rootPos.y, rootPos.z, false) 
            && (rootBlock.getMobilityFlag() == 0 || rootBlock instanceof BlockPistonBase)) {

            world.setBlock(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
                ModObjects.piston_air, 0, 3);
            world.setTileEntity(pullingTargetPos.x, pullingTargetPos.y, pullingTargetPos.z,
                new TileEntityPistonAir());

            PistonStructure structure = new PistonStructure(world, blockThis, new BlockPos(x, y, z), direction,
                false);

            // if structure can be pulled, move the structure, otherwise just retract the piston
            if (structure.calculate()) {
                structure.moveStructure();
            }

        }
    }
}
