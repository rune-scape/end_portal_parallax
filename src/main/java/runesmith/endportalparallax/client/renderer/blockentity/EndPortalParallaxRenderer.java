package runesmith.endportalparallax.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import runesmith.endportalparallax.client.renderer.Renderer;

@OnlyIn(Dist.CLIENT)
public class EndPortalParallaxRenderer<T extends TheEndPortalBlockEntity> extends TheEndPortalRenderer<T> {
    public EndPortalParallaxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        Renderer.renderPortal(blockEntity, poseStack.last().pose(), bufferSource.getBuffer(renderType()), getOffsetUp(), getOffsetDown());
    }

    protected @NotNull RenderType renderType() {
        return Renderer.getRendertypeEndPortalParallax();
    }
}