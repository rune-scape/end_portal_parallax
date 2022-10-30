package runesmith.endportalparallax.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import runesmith.endportalparallax.client.renderer.Renderer;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EndPortalParallaxRenderer<T extends EndPortalTileEntity> extends EndPortalTileEntityRenderer<T> {
    public EndPortalParallaxRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(@Nonnull T tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferSource, int combinedLightIn, int combinedOverlayIn) {
        Renderer.renderPortal(tileEntity, matrixStack.getLast().getMatrix(), (BufferBuilder) bufferSource.getBuffer(Renderer.getRendertypeEndPortalParallax(tileEntity.getPos().getY())), getOffset(), 0.0F);
    }
}