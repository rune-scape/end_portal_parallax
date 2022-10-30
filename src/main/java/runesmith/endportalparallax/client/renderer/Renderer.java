package runesmith.endportalparallax.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class Renderer {
    private static ShaderInstance endPortalParallaxShader;
    private static AbstractUniform endPortalParallaxShaderLayerOffsetUniform = new AbstractUniform();
    public static final VertexFormatElement ELEMENT_UV3D = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 3);
    public static final VertexFormat POSITION_TEX3D = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", DefaultVertexFormat.ELEMENT_POSITION).put("UV3D", ELEMENT_UV3D).build());
    public static final RenderType RENDERTYPE_END_PORTAL_PARALLAX = RenderType.create("end_portal_parallax", POSITION_TEX3D, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(Renderer::getEndPortalParallaxShader)).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));

    public static void reloadEndPortalParallaxShader() {
        endPortalParallaxShaderLayerOffsetUniform = new AbstractUniform();
        if (endPortalParallaxShader != null) {
            endPortalParallaxShader.close();
            endPortalParallaxShader = null;
        }
        try {
            endPortalParallaxShader = new ShaderInstance(Minecraft.getInstance().getResourceManager(), new ResourceLocation("endportalparallax:rendertype_end_portal_parallax"), POSITION_TEX3D);
            endPortalParallaxShaderLayerOffsetUniform = endPortalParallaxShader.safeGetUniform("LayerOffset");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ShaderInstance getEndPortalParallaxShader() {
        if (endPortalParallaxShader == null) {
            return GameRenderer.getRendertypeEndPortalShader();
        }
        return endPortalParallaxShader;
    }

    public static void renderPortal(BlockEntity blockEntity, Matrix4f mat, BufferBuilder buffer, float offsetUp, float offsetDown) {
        renderPortalFace(blockEntity, mat, buffer, 1.0F, 0.0F, 1.0F, 1.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.SOUTH);
        renderPortalFace(blockEntity, mat, buffer, 0.0F, 1.0F, 0.0F, 0.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.NORTH);
        renderPortalFace(blockEntity, mat, buffer, 1.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.EAST);
        renderPortalFace(blockEntity, mat, buffer, 0.0F, 0.0F, 1.0F, 0.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.WEST);
        renderPortalFace(blockEntity, mat, buffer, 0.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetDown, offsetDown, offsetDown, Direction.DOWN);
        renderPortalFace(blockEntity, mat, buffer, 0.0F, 1.0F, 1.0F, 0.0F, offsetUp, offsetUp, offsetUp, offsetUp, Direction.UP);
    }

    private static void renderPortalFace(BlockEntity blockEntity, Matrix4f mat, BufferBuilder buffer, float x1, float x2, float z1, float z2, float y1, float y2, float y3, float y4, Direction dir) {
        if (Block.shouldRenderFace(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos(), dir, blockEntity.getBlockPos().relative(dir))) {
            BlockPos blockPos = blockEntity.getBlockPos();
            addPortalVertex(buffer, mat, x1, y1, z1, blockPos.getX() + x1, blockPos.getZ() + z1, y1 - 0.5F);
            addPortalVertex(buffer, mat, x2, y2, z1, blockPos.getX() + x2, blockPos.getZ() + z1, y2 - 0.5F);
            addPortalVertex(buffer, mat, x2, y3, z2, blockPos.getX() + x2, blockPos.getZ() + z2, y3 - 0.5F);
            addPortalVertex(buffer, mat, x1, y4, z2, blockPos.getX() + x1, blockPos.getZ() + z2, y4 - 0.5F);
        }
    }

    private static void addPortalVertex(BufferBuilder buffer, Matrix4f mat, float x, float y, float z, float s, float t, float p) {
        buffer.vertex(mat, x, y, z);
        buffer.putFloat(0, s);
        buffer.putFloat(4, t);
        buffer.putFloat(8, p);
        buffer.nextElement();
        buffer.endVertex();
    }

    public static void updateLayerOffset() {
        endPortalParallaxShaderLayerOffsetUniform.set(System.currentTimeMillis() % 700000L / 700000.0F);
    }

    public static RenderType getRendertypeEndPortalParallax() {
        return RENDERTYPE_END_PORTAL_PARALLAX;
    }
}
