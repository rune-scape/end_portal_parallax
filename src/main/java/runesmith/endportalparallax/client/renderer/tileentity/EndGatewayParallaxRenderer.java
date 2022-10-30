package runesmith.endportalparallax.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.BeaconTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.EndGatewayTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import runesmith.endportalparallax.client.renderer.Renderer;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EndGatewayParallaxRenderer extends EndGatewayTileEntityRenderer {
    public static final ResourceLocation END_GATEWAY_BEAM_TEXTURE = new ResourceLocation("textures/entity/end_gateway_beam.png");

    public EndGatewayParallaxRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(@Nonnull EndGatewayTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferSource, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntity.isSpawning() || tileEntity.isCoolingDown()) {
            float f = tileEntity.isSpawning() ? tileEntity.getSpawnPercent(partialTicks) : tileEntity.getCooldownPercent(partialTicks);
            double d0 = tileEntity.isSpawning() ? 256.0D : 50.0D;
            f = MathHelper.sin(f * (float)Math.PI);
            int i = MathHelper.floor((double)f * d0);
            float[] afloat = tileEntity.isSpawning() ? DyeColor.MAGENTA.getColorComponentValues() : DyeColor.PURPLE.getColorComponentValues();
            long j = tileEntity.getWorld().getGameTime();
            BeaconTileEntityRenderer.renderBeamSegment(matrixStack, bufferSource, END_GATEWAY_BEAM_TEXTURE, partialTicks, f, j, 0, i, afloat, 0.15F, 0.175F);
            BeaconTileEntityRenderer.renderBeamSegment(matrixStack, bufferSource, END_GATEWAY_BEAM_TEXTURE, partialTicks, f, j, 0, -i, afloat, 0.15F, 0.175F);
        }
        
        Renderer.renderPortal(tileEntity, matrixStack.getLast().getMatrix(), (BufferBuilder) bufferSource.getBuffer(Renderer.getRendertypeEndPortalParallax(tileEntity.getPos().getY())), getOffset(), 0.0F);
    }
}