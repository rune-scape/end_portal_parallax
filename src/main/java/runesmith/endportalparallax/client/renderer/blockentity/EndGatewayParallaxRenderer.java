package runesmith.endportalparallax.client.renderer.blockentity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.render.BufferVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import runesmith.endportalparallax.client.renderer.Renderer;

@Environment(value = EnvType.CLIENT)
public class EndGatewayParallaxRenderer extends EndGatewayBlockEntityRenderer {
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");

    public EndGatewayParallaxRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(EndGatewayBlockEntity endGatewayBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int combinedLightIn, int combinedOverlayIn) {
        if (endGatewayBlockEntity.isRecentlyGenerated() || endGatewayBlockEntity.needsCooldownBeforeTeleporting()) {
            float g = endGatewayBlockEntity.isRecentlyGenerated() ? endGatewayBlockEntity.getRecentlyGeneratedBeamHeight(f) : endGatewayBlockEntity.getCooldownBeamHeight(f);
            double d = endGatewayBlockEntity.isRecentlyGenerated() ? (double)endGatewayBlockEntity.getWorld().getTopY() : 50.0;
            g = MathHelper.sin(g * (float)Math.PI);
            int k = MathHelper.floor((double)g * d);
            float[] fs = endGatewayBlockEntity.isRecentlyGenerated() ? DyeColor.MAGENTA.getColorComponents() : DyeColor.PURPLE.getColorComponents();
            long l = endGatewayBlockEntity.getWorld().getTime();
            BeaconBlockEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider, BEAM_TEXTURE, f, g, l, -k, k * 2, fs, 0.15f, 0.175f);
        }

        Renderer.renderPortal(endGatewayBlockEntity, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider.getBuffer(getLayer()), getTopYOffset(), getBottomYOffset());
    }

    @Override
    protected RenderLayer getLayer() {
        return Renderer.getRenderLayerEndPortalParallax();
    }
}