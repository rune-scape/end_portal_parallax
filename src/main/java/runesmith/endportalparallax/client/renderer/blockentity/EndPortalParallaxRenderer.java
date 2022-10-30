package runesmith.endportalparallax.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import runesmith.endportalparallax.client.renderer.Renderer;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EndPortalParallaxRenderer<T extends TheEndPortalBlockEntity> extends TheEndPortalRenderer<T> {
    public EndPortalParallaxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@Nonnull T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        Renderer.renderPortal(blockEntity, poseStack.last().pose(), (BufferBuilder) bufferSource.getBuffer(renderType()), getOffsetUp(), getOffsetDown());
    }

    protected @Nonnull RenderType renderType() {
        return Renderer.getRendertypeEndPortalParallax();
    }
}