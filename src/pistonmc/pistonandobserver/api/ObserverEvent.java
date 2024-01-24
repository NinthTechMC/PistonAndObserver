package pistonmc.pistonandobserver.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Event fired when a block is about to notify observers around it
 *
 * If the event is cancelled, onObserverEvent will not be called on the new block.
 * Cancel the event if you are handling the notification yourself.
 *
 * This event is fired on the MinecraftForge.EVENT_BUS
 */
@Cancelable
public class ObserverEvent extends Event {
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public final Block oldBlock;
    public final Block newBlock;
    public final int oldMeta;

    public ObserverEvent(World world, int x, int y, int z, Block oldBlock, Block newBlock,
            int oldMeta) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.oldMeta = oldMeta;
    }

}
