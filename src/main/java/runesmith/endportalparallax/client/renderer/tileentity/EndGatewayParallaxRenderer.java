package runesmith.endportalparallax.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntityEndGatewayRenderer;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import runesmith.endportalparallax.client.renderer.Renderer;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EndGatewayParallaxRenderer extends TileEntityEndGatewayRenderer {
    @Override
    public void render(@Nonnull TileEntityEndPortal tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        Renderer.renderPortal(tileEntity, x, y, z, getOffset(), 0.0F);
    }
}