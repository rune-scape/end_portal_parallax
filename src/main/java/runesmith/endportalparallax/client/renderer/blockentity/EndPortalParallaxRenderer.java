package runesmith.endportalparallax.client.renderer.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.BufferVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import runesmith.endportalparallax.client.renderer.Renderer;

@Environment(value = EnvType.CLIENT)
public class EndPortalParallaxRenderer<T extends EndPortalBlockEntity> extends EndPortalBlockEntityRenderer<T> {
    public EndPortalParallaxRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(T endPortalBlockEntity, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int combinedLightIn, int combinedOverlayIn) {
        Renderer.renderPortal(endPortalBlockEntity, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider.getBuffer(getLayer()), getTopYOffset(), getBottomYOffset());
    }

    @Override
    protected RenderLayer getLayer() {
        return Renderer.getRenderLayerEndPortalParallax();
    }
}