package pistonmc.flyingmachinebackport;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemHoneyBall extends Item {

	public ItemHoneyBall() {
		super();
		this.setCreativeTab(CreativeTabs.tabMisc);
		this.setUnlocalizedName("honey_ball");
		this.setTextureName(ModInfo.Id+":honey_ball");
	}

}
