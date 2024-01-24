package pistonmc.pistonandobserver.mixins.observer;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.Block;
import pistonmc.pistonandobserver.api.IBlockObservable;
import pistonmc.pistonandobserver.api.ObserverAPI;
import pistonmc.pistonandobserver.api.ObserverEvent;

@Mixin(Block.class)
public class MixinBlock implements IBlockObservable {

    @Override
    public void onObserverEvent(ObserverEvent event) {
        ObserverAPI.notifyObserversAround(event.world, event.x, event.y, event.z);
    }
}
