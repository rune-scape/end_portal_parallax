package bowlingallie.endportalparallax.client.renderer.tileentity;

import bowlingallie.endportalparallax.client.renderer.CustomEndPortalRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEndPortal;

public class TileEntityEndPortalParallaxRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
    private final CustomEndPortalRenderer END_PORTAL_RENDERER = new CustomEndPortalRenderer();

    @Override
    public void render(TileEntityEndPortal endPortal, double x, double y, double z, float partialTicks, int breakProgress, float alpha) {
        TileEntityRendererDispatcher info = TileEntityRendererDispatcher.instance;
        END_PORTAL_RENDERER.render(endPortal, getOffsetTop(), getOffsetBottom(), x, y, z, info.entityX, info.entityY, info.entityZ, info.renderEngine);
    }

    public float getOffsetTop() {
        return 0.75F;
    }

    public float getOffsetBottom() {
        return 0.1F;
    }
}