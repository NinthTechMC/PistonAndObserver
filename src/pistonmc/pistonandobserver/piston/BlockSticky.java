package pistonmc.pistonandobserver.piston;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import pistonmc.pistonandobserver.ModInfo;

public class BlockSticky extends Block {

    private boolean renderAsNormalBlock;

    public BlockSticky(String name, boolean isTransparent) {
        super(Material.clay);
        this.renderAsNormalBlock = !isTransparent;
        this.setBlockName(name);
		this.setHardness(0.6F);
        this.setBlockTextureName(ModInfo.MODID + ":" + name);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

	@Override
	public boolean renderAsNormalBlock() {
        return this.renderAsNormalBlock;
	}
}
