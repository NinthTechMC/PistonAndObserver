package pistonmc.pistonandobserver.mixins.piston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import pistonmc.pistonandobserver.core.Config;
import pistonmc.pistonandobserver.core.PistonHooks;

@Mixin(BlockPistonBase.class)
public class MixinBlockPistonBase extends Block {
    protected MixinBlockPistonBase(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "canExtend", at = @At("HEAD"), cancellable = true)
    private static void preCanExtend(World world, int x, int y, int z, int direction, CallbackInfoReturnable<Boolean> cir) {
        if (Config.modifyVanillaPiston) {
            cir.setReturnValue(true);
            return;
        }
        Block b = world.getBlock(x, y, z);
        if (Config.shouldModifyPiston(b)) {
            cir.setReturnValue(true);
            return;
        }
    }

    @Inject(method = "canPushBlock", at = @At("HEAD"), cancellable = true)
    private static void preCanPushBlock(Block blockToPush, World world, int x, int y, int z, boolean shouldDestroy, CallbackInfoReturnable<Boolean> cir) {
        // TODO: observers
        // TODO: observer
		// if (block == ModObjects.blockObserver) {
		// 	TileEntity tile = pos.getTileEntityInWorld(world);
		// 	if (!(tile instanceof TileEntityObserver)) {
		// 		// error case
		// 		return false;
		// 	}
		// 	return ((TileEntityObserver) tile).getMoveDirection() == -1; // stationary observers can be moved by pistons
		//
		// }
        if (blockToPush instanceof BlockPistonBase) {
            int meta = world.getBlockMetadata(x, y, z);
            if (BlockPistonBase.isExtended(meta)) {
                cir.setReturnValue(false);
                return;
            }
            cir.setReturnValue(!blockToPush.hasTileEntity(meta));
            return;
        }
    }

    @Inject(method = "onBlockEventReceived", at = @At(value = "FIELD", target = "Lnet/minecraft/block/BlockPistonBase;isSticky:Z"), cancellable = true)
    public void preStickyCheck(World world, int x, int y, int z, int eventId, int meta, CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldModifyPiston((Object) this)) {
            int direction = BlockPistonBase.getPistonOrientation(meta);
            PistonHooks.postRetract((BlockPistonBase) (Object) this, world, x, y, z, direction);
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
            cir.setReturnValue(true);
            return;
        }
    }

    @Inject(method = "tryExtend", at = @At("HEAD"), cancellable = true)
    public void preTryExtend(World world, int x, int y, int z, int direction, CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldModifyPiston((Object) this)) {
            boolean ret = PistonHooks.tryExtend((Block) (Object) this, world, x, y, z, direction);
            cir.setReturnValue(ret);
            return;
        }
    }
}
