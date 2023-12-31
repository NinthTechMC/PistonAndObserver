package pistonmc.flyingmachinebackport;

import net.minecraft.block.Block;

public class SlimeBlockSoundType extends Block.SoundType {
	public static final Block.SoundType instance = new SlimeBlockSoundType();

	private SlimeBlockSoundType() {
		super("stone", 1.0F, 1.0F);
	}

	@Override
	public String getBreakSound() {
		return "mob.slime.big";
	}

	@Override
	public String getStepResourcePath() {
		return "mob.slime.big";
	}

}
