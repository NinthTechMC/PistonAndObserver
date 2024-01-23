package pistonmc.pistonandobserver.mixins.observer;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.api.IBlockObservable;
import pistonmc.pistonandobserver.api.ObserverAPI;

@Mixin(Block.class)
public class MixinBlock implements IBlockObservable {

    @Override
    public void notifyObservers(World world, int x, int y, int z, Block oldBlock, Block newBlock) {
        ObserverAPI.notifyObserversAround(world, x, y, z);
    }
}
