package pistonmc.flyingmachine.observer;

import net.minecraft.client.renderer.RenderBlocks;
import pistonmc.flyingmachine.ModObjects;

/**
 * Rendering logic for observers, whose sides need to rotate to make the triangle point to the back
 */
public class ObserverRenderCore {
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

		boolean result = renderer.renderStandardBlock(ModObjects.blockObserver, x, y, z);
		renderer.uvRotateSouth = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
		return result;
	}
}
