package runesmith.endportalparallax.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntityEndPortalRenderer;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import runesmith.endportalparallax.client.renderer.Renderer;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EndPortalParallaxRenderer extends TileEntityEndPortalRenderer {
    public static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
    public static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");

    @Override
    public void render(@Nonnull TileEntityEndPortal tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        Renderer.renderPortal(tileEntity, x, y, z, getOffset(), 0.0F);
    }
}