package pistonmc.pistonandobserver.observer.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.api.ObserverAPI;
import pistonmc.pistonandobserver.api.ObserverEvent;

/**
 * Observe snowed state of grass blocks
 */
public class ObserveGrassBlock {
    @SubscribeEvent
    public void onObserverEvent(ObserverEvent event) {
        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;
        Block blockBelow = world.getBlock(x, y - 1, z);
        if (blockBelow != Blocks.grass) {
            return;
        }
        if (isBlockSnow(event.newBlock) == isBlockSnow(event.oldBlock)) {
            return;
        }
        int meta = world.getBlockMetadata(x, y - 1, z);
        ObserverAPI.fireObserverEvent(world, x, y - 1, z, blockBelow, blockBelow, meta);
    }

    private boolean isBlockSnow(Block block) {
        Material material = block.getMaterial();
        return material == Material.snow || material == Material.craftedSnow;
    }
}
