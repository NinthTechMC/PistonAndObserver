package pistonmc.flyingmachinebackport.observer.module;

import pistonmc.flyingmachinebackport.BlockPos;
import pistonmc.flyingmachinebackport.observer.BlockObserver;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;

public class ObserverModuleNoteBlock implements ObserverModule {

	private int lastNote;
	private boolean lastPoweredState;
	private Instrument lastInstrument;

	@Override
	public boolean shouldKeepWatching(Block block, int meta) {
		return block == Blocks.noteblock;
	}

	@Override
	public boolean isChanged(World world, BlockPos pos, int meta) {
		TileEntity tile = pos.getTileEntityInWorld(world);
		if (!(tile instanceof TileEntityNote)) {
			return false;
		}
		TileEntityNote noteBlock = (TileEntityNote) tile;
		int newNote = noteBlock.note;
		boolean newPoweredState = noteBlock.previousRedstoneState;
		BlockPos posBelow = pos.offset(0);
		Block blockBelow = posBelow.getBlockInWorld(world);
		Material materialBelow = blockBelow.getMaterial();
		Instrument newInstrument = getInstrumentFromMaterial(materialBelow);

		boolean changed = newNote != lastNote || newPoweredState != lastPoweredState || newInstrument != lastInstrument;
		lastNote = newNote;
		lastPoweredState = newPoweredState;

		if (BlockObserver.shouldIgnoreBlock(world, posBelow, blockBelow)) {
			lastInstrument = null;
			return false;
		}
		lastInstrument = newInstrument;
		return changed;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		lastNote = nbt.getInteger("observer_noteLastNote");
		lastPoweredState = nbt.getBoolean("observer_noteLastPoweredState");
		int instrument = nbt.getInteger("observer_lastInstrument");
		if (instrument >= 0 && instrument < Instrument.values().length) {
			lastInstrument = Instrument.values()[instrument];
		} else {
			lastInstrument = null;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("observer_noteLastNote", lastNote);
		nbt.setBoolean("observer_noteLastPoweredState", lastPoweredState);
		nbt.setInteger("observer_noteInstrument", lastInstrument == null ? -1 : lastInstrument.ordinal());
	}

	private Instrument getInstrumentFromMaterial(Material material) {
		if (material == Material.rock) {
			return Instrument.BASSDRUM;
		}

		if (material == Material.sand) {
			return Instrument.SNARE;
		}

		if (material == Material.glass) {
			return Instrument.CLICKS;
		}

		if (material == Material.wood) {
			return Instrument.BASSGUITAR;
		}
		return Instrument.PIANO;
	}

}
