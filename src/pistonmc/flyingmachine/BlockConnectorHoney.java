package pistonmc.flyingmachine;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockConnectorHoney extends Block {

	public BlockConnectorHoney() {
		super(Material.clay);
		this.setBlockName("piston_connector_honey");
		this.setHardness(0.6F);
		this.setBlockTextureName(ModInfo.Id+":piston_connector_honey");
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setStepSound(SlimeBlockSoundType.instance);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false; // honey block is transparent
	}

}
