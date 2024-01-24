package pistonmc.pistonandobserver.mixins.observer;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNote;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.api.ObserverAPI;

@Mixin(BlockNote.class)
public class MixinBlockNote {
    @Inject(
        method = "onNeighborBlockChange",
        at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/tileentity/TileEntityNote;previousRedstoneState:Z")
    )
    public void notifyObserverWhenPowerStateChanges(World world, int x, int y, int z, Block neighbor, CallbackInfo ci) {
        if (world.isRemote) {
            return;
        }
        ObserverAPI.fireObserverEvent(world, x, y, z, (Block) (Object) this, (Block) (Object) this, world.getBlockMetadata(x, y, z));
    }
}
