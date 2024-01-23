package pistonmc.pistonandobserver.observer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import pistonmc.pistonandobserver.ModObjects;

/**
 * Logic for rendering stationary observers.
 * 
 * This makes sure observers are rendered correctly in the inventory.
 * It also turns off block rendering for moving observers
 */
public class RenderObserver implements ISimpleBlockRenderingHandler {
	public static int renderId;

	public RenderObserver(int renderId) {
		RenderObserver.renderId = renderId;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		metadata = 2; // Set the orientation of the observer in inventory
		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) {

		// TileEntity tileEntity = world.getTileEntity(x, y, z);
		// if (tileEntity instanceof TileEntityObserver) {
		// 	if (((TileEntityObserver) tileEntity).getMoveDirection() != -1) {
		// 		return true; // moving observers are rendered by tile entity renderer
		// 	}
		// }

		int meta = world.getBlockMetadata(x, y, z);
		int back = BlockObserver.getBackFacing(meta);

		return this.renderObserver(renderer, x, y, z, meta, back);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderId;
	}

	public boolean renderObserver(RenderBlocks renderer, int x, int y, int z, int meta, int back) {
		switch (back) {
		case 0: /* up */
			renderer.uvRotateSouth = 1;
			renderer.uvRotateNorth = 1;
			renderer.uvRotateEast = 3;
			renderer.uvRotateWest = 3;
			break;
		case 1: /* down */
			renderer.uvRotateSouth = 1;
			renderer.uvRotateNorth = 1;
			break;
		case 3:
			renderer.uvRotateTop = 3;
			renderer.uvRotateBottom = 3;
			break;
		case 4:
			renderer.uvRotateTop = 2;
			renderer.uvRotateBottom = 1;
			break;
		case 5:
			renderer.uvRotateTop = 1;
			renderer.uvRotateBottom = 2;
			break;
		}

		boolean result = renderer.renderStandardBlock(ModObjects.observer, x, y, z);
		renderer.uvRotateSouth = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
		return result;
	}

}
