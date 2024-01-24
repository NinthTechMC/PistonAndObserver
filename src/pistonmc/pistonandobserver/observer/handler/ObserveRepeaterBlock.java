package pistonmc.pistonandobserver.observer.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.api.ObserverAPI;
import pistonmc.pistonandobserver.api.ObserverEvent;

public class ObserveRepeaterBlock {
    @SubscribeEvent
    public void onObserverEvent(ObserverEvent event) {
        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;
        this.checkRepeaterAt(event, world, x - 1, y, z);
        this.checkRepeaterAt(event, world, x + 1, y, z);
        this.checkRepeaterAt(event, world, x, y, z - 1);
        this.checkRepeaterAt(event, world, x, y, z + 1);
    }

    private void checkRepeaterAt(ObserverEvent event, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof BlockRedstoneRepeater)) {
            return;
        }
        BlockRedstoneRepeater repeater = (BlockRedstoneRepeater) block;

        IBlockAccess oldWorld = event.getBlockAccessBeforeChange();
        int oldMeta = oldWorld.getBlockMetadata(x, y, z);
        boolean wasLocked = repeater.func_149910_g(oldWorld, x, y, z, oldMeta);
        int newMeta = world.getBlockMetadata(x, y, z);
        boolean isLocked = repeater.func_149910_g(world, x, y, z, newMeta);
        if (wasLocked != isLocked) {
            ObserverAPI.fireObserverEvent(world, x, y, z, block, block, oldMeta);
        }
    }
}
