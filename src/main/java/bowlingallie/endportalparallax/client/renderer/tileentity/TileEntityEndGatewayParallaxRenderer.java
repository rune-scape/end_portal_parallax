package bowlingallie.endportalparallax.client.renderer.tileentity;

import bowlingallie.endportalparallax.client.renderer.CustomEndPortalRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEndPortal;

public class TileEntityEndGatewayParallaxRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
    private final CustomEndPortalRenderer END_PORTAL_RENDERER = new CustomEndPortalRenderer();

    @Override
    public void render(TileEntityEndPortal endPortal, double x, double y, double z, float partialTicks, int breakProgress, float alpha) {
        TileEntityRendererDispatcher info = TileEntityRendererDispatcher.instance;
        END_PORTAL_RENDERER.render(endPortal, x, y, z, ActiveRenderInfo.getCameraPosition(), info.renderEngine);
    }
}