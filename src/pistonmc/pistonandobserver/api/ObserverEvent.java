package pistonmc.pistonandobserver.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

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

    private IBlockAccess blockAccessBeforeChange;

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

    /**
     * Get an instance of IBlockAccess that, when getting the block at the changed coordinate,
     * returns the old block and old meta. Otherwise passes through to the world.
     *
     * Note that it will not return the old tile entity.
     */
    public IBlockAccess getBlockAccessBeforeChange() {
        if (this.blockAccessBeforeChange != null) {
            return this.blockAccessBeforeChange;
        }
        this.blockAccessBeforeChange = new WorldBeforeChange(this);
        return this.blockAccessBeforeChange;
    }

    public static class WorldBeforeChange implements IBlockAccess {
        public final ObserverEvent event;
        public WorldBeforeChange(ObserverEvent event) {
            this.event = event;
        }

        @Override
        public Block getBlock(int x, int y, int z) {
            if (x == this.event.x && y == this.event.y && z == this.event.z) {
                return this.event.oldBlock;
            }
            return this.event.world.getBlock(x, y, z);
        }

        @Override
        public TileEntity getTileEntity(int x, int y, int z) {
            if (x == this.event.x && y == this.event.y && z == this.event.z) {
                return null;
            }
            return this.event.world.getTileEntity(x, y, z);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getLightBrightnessForSkyBlocks(int x, int y, int z, int p_72802_4_) {
            return this.event.world.getLightBrightnessForSkyBlocks(x, y, z, p_72802_4_);
        }

        @Override
        public int getBlockMetadata(int x, int y, int z) {
            if (x == this.event.x && y == this.event.y && z == this.event.z) {
                return this.event.oldMeta;
            }
            return this.event.world.getBlockMetadata(x, y, z);
        }

        @Override
        public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
            if (x == this.event.x && y == this.event.y && z == this.event.z) {
                return this.event.oldBlock.isProvidingStrongPower(this, x, y, z, directionIn);
            }
            return this.event.world.isBlockProvidingPowerTo(x, y, z, directionIn);
        }

        @Override
        public boolean isAirBlock(int x, int y, int z) {
            if (x == this.event.x && y == this.event.y && z == this.event.z) {
                return this.event.oldBlock.isAir(this, x, y, z);
            }
            return this.event.world.isAirBlock(x, y, z);
        }

        @Override
        public BiomeGenBase getBiomeGenForCoords(int x, int z) {
            return this.event.world.getBiomeGenForCoords(x, z);
        }

        @Override
        public int getHeight() {
            return this.event.world.getHeight();
        }

        @SideOnly(Side.CLIENT)
        @Override
        public boolean extendedLevelsInChunkCache() {
            return this.event.world.extendedLevelsInChunkCache();
        }

        @Override
        public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
            if (x == this.event.x && y == this.event.y && z == this.event.z) {
                return this.event.oldBlock.isSideSolid(this, x, y, z, side);
            }
            return this.event.world.isSideSolid(x, y, z, side, _default);
        }
    }

}
