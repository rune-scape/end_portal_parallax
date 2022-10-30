package runesmith.endportalparallax.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import runesmith.endportalparallax.client.renderer.Renderer;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EndGatewayParallaxRenderer extends TheEndGatewayRenderer {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/end_gateway_beam.png");
    
    public EndGatewayParallaxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@Nonnull TheEndGatewayBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
        if (blockEntity.isSpawning() || blockEntity.isCoolingDown()) {
            float f = blockEntity.isSpawning() ? blockEntity.getSpawnPercent(partialTicks) : blockEntity.getCooldownPercent(partialTicks);
            double d0 = blockEntity.isSpawning() ? (double)blockEntity.getLevel().getMaxBuildHeight() : 50.0D;
            f = Mth.sin(f * (float)Math.PI);
            int i = Mth.floor((double)f * d0);
            float[] afloat = blockEntity.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColors() : DyeColor.PURPLE.getTextureDiffuseColors();
            long j = blockEntity.getLevel().getGameTime();
            BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BEAM_LOCATION, partialTicks, f, j, -i, i * 2, afloat, 0.15F, 0.175F);
        }
        
        Renderer.renderPortal(blockEntity, poseStack.last().pose(), (BufferBuilder) bufferSource.getBuffer(renderType()), getOffsetUp(), getOffsetDown());
    }

    protected @Nonnull RenderType renderType() {
        return Renderer.getRendertypeEndPortalParallax();
    }
}