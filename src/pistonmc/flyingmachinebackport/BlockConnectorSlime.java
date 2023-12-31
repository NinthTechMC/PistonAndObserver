package pistonmc.flyingmachinebackport;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockConnectorSlime extends Block {

	public BlockConnectorSlime() {
		super(Material.clay);
		this.setBlockName("piston_connector_slime");
		this.setHardness(0.6F);
		this.setBlockTextureName(ModInfo.Id+":piston_connector_slime");
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setStepSound(SlimeBlockSoundType.instance);
	}

}
