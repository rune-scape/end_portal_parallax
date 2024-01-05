package runesmith.endportalparallax.client.renderer;

import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import runesmith.endportalparallax.EndPortalParallaxMod;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class Renderer {
    private static ShaderInstance endPortalParallaxShader;
    private static AbstractUniform endPortalParallaxShaderLayerOffsetUniform = new AbstractUniform();
    private static AbstractUniform endPortalParallaxShaderCameraPosUniform = new AbstractUniform();
    public static final RenderType RENDERTYPE_END_PORTAL_PARALLAX = RenderType.create("end_portal_parallax", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(Renderer::getEndPortalParallaxShader)).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));
    public static boolean renderBorked = false;

    public static void reloadEndPortalParallaxShader(ResourceManager resourceManager) {
        endPortalParallaxShaderLayerOffsetUniform = new AbstractUniform();
        endPortalParallaxShaderCameraPosUniform = new AbstractUniform();
        if (endPortalParallaxShader != null) {
            endPortalParallaxShader.close();
            endPortalParallaxShader = null;
        }
        try {
            endPortalParallaxShader = new ShaderInstance(resourceManager, new ResourceLocation("endportalparallax", "rendertype_end_portal_parallax"), DefaultVertexFormat.POSITION_TEX);
            endPortalParallaxShaderLayerOffsetUniform = endPortalParallaxShader.safeGetUniform("LayerOffset");
            endPortalParallaxShaderCameraPosUniform = endPortalParallaxShader.safeGetUniform("CameraPos");
        } catch (IOException e) {
            e.printStackTrace();
            borked(e.getMessage());
        }
    }

    public static ShaderInstance getEndPortalParallaxShader() {
        if (endPortalParallaxShader == null) {
            borked("endPortalParallaxShader == null");
            return GameRenderer.getRendertypeEndPortalShader();
        }
        return endPortalParallaxShader;
    }

    public static void borked(String s) {
        if (!renderBorked) {
            renderBorked = true;
            EndPortalParallaxMod.LOGGER.error("End portal shaders are borked :/ " + s);
        }
    }

    public static void renderPortal(BlockEntity blockEntity, Matrix4f mat, VertexConsumer vertexConsumer, float offsetUp, float offsetDown) {
        renderPortalFace(blockEntity, mat, vertexConsumer, 1.0F, 0.0F, 1.0F, 1.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.SOUTH);
        renderPortalFace(blockEntity, mat, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.NORTH);
        renderPortalFace(blockEntity, mat, vertexConsumer, 1.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.EAST);
        renderPortalFace(blockEntity, mat, vertexConsumer, 0.0F, 0.0F, 1.0F, 0.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.WEST);
        renderPortalFace(blockEntity, mat, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetDown, offsetDown, offsetDown, Direction.DOWN);
        renderPortalFace(blockEntity, mat, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, offsetUp, offsetUp, offsetUp, offsetUp, Direction.UP);
    }

    private static void renderPortalFace(BlockEntity blockEntity, Matrix4f mat, VertexConsumer vertexConsumer, float x1, float x2, float z1, float z2, float y1, float y2, float y3, float y4, Direction dir) {
        if (blockEntity.getLevel() == null) {
            borked("blockEntity.getLevel() == null");
        } else if (Block.shouldRenderFace(blockEntity.getBlockState(), blockEntity.getLevel(), blockEntity.getBlockPos(), dir, blockEntity.getBlockPos().relative(dir))) {
            BlockPos blockPos = blockEntity.getBlockPos();
            addPortalVertex(vertexConsumer, mat, x1, y1, z1, y1 - 0.5f);
            addPortalVertex(vertexConsumer, mat, x2, y2, z1, y2 - 0.5f);
            addPortalVertex(vertexConsumer, mat, x2, y3, z2, y3 - 0.5f);
            addPortalVertex(vertexConsumer, mat, x1, y4, z2, y4 - 0.5f);
        }
    }

    private static void addPortalVertex(VertexConsumer vertexConsumer, Matrix4f mat, float x, float y, float z, float p) {
        vertexConsumer.vertex(mat, x, y, z);
        vertexConsumer.uv(0.0f, p);
        vertexConsumer.endVertex();
    }

    public static void updateCameraPos(Vec3 position) {
        endPortalParallaxShaderCameraPosUniform.set((float) position.x, (float) position.y, (float) position.z);
    }

    public static void updateLayerOffset() {
        endPortalParallaxShaderLayerOffsetUniform.set(System.currentTimeMillis() % 700000L / 700000.0F);
    }

    public static RenderType getRendertypeEndPortalParallax() {
        return RENDERTYPE_END_PORTAL_PARALLAX;
    }
}
