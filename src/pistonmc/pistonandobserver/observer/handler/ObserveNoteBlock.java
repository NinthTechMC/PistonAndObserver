package pistonmc.pistonandobserver.observer.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.api.ObserverAPI;
import pistonmc.pistonandobserver.api.ObserverEvent;

/**
 * Observe instrument changes of note blocks
 */
public class ObserveNoteBlock {
    @SubscribeEvent
    public void onObserverEvent(ObserverEvent event) {
        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;
        Block blockAbove = world.getBlock(x, y + 1, z);
        if (blockAbove != Blocks.noteblock) {
            return;
        }
        if (getInstrument(event.newBlock) == getInstrument(event.oldBlock)) {
            return;
        }
        int meta = world.getBlockMetadata(x, y + 1, z);
        ObserverAPI.fireObserverEvent(world, x, y + 1, z, blockAbove, blockAbove, meta);
    }

    private int getInstrument(Block block) {
        Material material = block.getMaterial();
        if (material == Material.rock) {
            return 1;
        }
        if (material == Material.sand) {
            return 2;
        }
        if (material == Material.glass) {
            return 3;
        }
        if (material == Material.wood) {
            return 4;
        }
        return 0;
    }
}
