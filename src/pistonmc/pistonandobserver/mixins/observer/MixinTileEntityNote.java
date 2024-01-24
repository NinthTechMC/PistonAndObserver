package pistonmc.pistonandobserver.mixins.observer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.api.ObserverAPI;

@Mixin(TileEntityNote.class)
public class MixinTileEntityNote {
    @Inject(method = "changePitch", at = @At("HEAD"))
    public void notifyObserverWhenChangingPitch(CallbackInfo ci) {
        TileEntityNote note = (TileEntityNote) (Object) this;
        World world = note.getWorldObj();
        if (world == null || world.isRemote) {
            return;
        }
        Block block = world.getBlock(note.xCoord, note.yCoord, note.zCoord);
        int meta = world.getBlockMetadata(note.xCoord, note.yCoord, note.zCoord);
        ObserverAPI.fireObserverEvent(world, note.xCoord, note.yCoord, note.zCoord, block, block, meta);
    }
}
