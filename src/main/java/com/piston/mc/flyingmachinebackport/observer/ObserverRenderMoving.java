package com.piston.mc.flyingmachinebackport.observer;

import org.lwjgl.opengl.GL11;

import com.piston.mc.flyingmachinebackport.ModObjects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Rendering logic for moving observers, so they appear to be moving with pistons
 */
public class ObserverRenderMoving extends TileEntitySpecialRenderer {
	private ObserverRenderCore delegate;
	private RenderBlocks renderBlocks;

	public ObserverRenderMoving(ObserverRenderCore delegate) {
		this.delegate = delegate;
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
		if (!(tileEntity instanceof TileEntityObserver)) {
			return;
		}

		TileEntityObserver observer = (TileEntityObserver) tileEntity;

		if (observer.getMoveDirection() != -1 && observer.getProgressWithPartialTick(partialTick) < 1.0F) {
			Tessellator tessellator = Tessellator.instance;
			this.bindTexture(TextureMap.locationBlocksTexture);
			RenderHelper.disableStandardItemLighting();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);

			if (Minecraft.isAmbientOcclusionEnabled()) {
				GL11.glShadeModel(GL11.GL_SMOOTH);
			} else {
				GL11.glShadeModel(GL11.GL_FLAT);
			}

			tessellator.startDrawingQuads();
			tessellator.setTranslation(x - observer.xCoord + observer.getXWithPartialTick(partialTick),
					y - observer.yCoord + observer.getYWithPartialTick(partialTick),
					z - observer.zCoord + observer.getZWithPartialTick(partialTick));
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

			renderBlocks.setRenderAllFaces(true);

			renderBlocks.setRenderBoundsFromBlock(ModObjects.blockObserver);

			int meta = observer.getWorldObj().getBlockMetadata(observer.xCoord, observer.yCoord, observer.zCoord);
			int back = BlockObserver.getObserverBackFacing(meta);
			delegate.renderObserver(renderBlocks, observer.xCoord, observer.yCoord, observer.zCoord, meta, back);

			renderBlocks.setRenderAllFaces(false);

			tessellator.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
			RenderHelper.enableStandardItemLighting();
		}

	}

	@Override
	public void func_147496_a(World world) // setWorld
	{
		this.renderBlocks = new RenderBlocks(world);
	}

}
