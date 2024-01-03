package runesmith.endportalparallax.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;

import java.io.IOException;

import static runesmith.endportalparallax.EndPortalParallaxMod.LOGGER;

public class Renderer extends RenderLayer {
    private static FabricShaderProgram endPortalParallaxShader;
    private static Uniform endPortalParallaxShaderLayerOffsetUniform = new Uniform();
    public static final VertexFormatElement ELEMENT_UV3D = new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.UV, 3);
    public static final VertexFormat POSITION_TEX3D = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", VertexFormats.POSITION_ELEMENT).put("UV3D", ELEMENT_UV3D).build());
    public static final RenderLayer RENDERLAYER_END_PORTAL_PARALLAX = of("end_portal_parallax", POSITION_TEX3D, VertexFormat.DrawMode.QUADS, 576, false, false, MultiPhaseParameters.builder().program(new RenderPhase.ShaderProgram(Renderer::getEndPortalParallaxShader)).texture(RenderPhase.Textures.create().add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false).add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false).build()).build(false));
    public static boolean renderBorked = false;

    public Renderer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static void reloadEndPortalParallaxShader() {
        RenderSystem.recordRenderCall(() -> {
            renderBorked = false;
            endPortalParallaxShaderLayerOffsetUniform = new Uniform();
            if (endPortalParallaxShader != null) {
                endPortalParallaxShader.close();
                endPortalParallaxShader = null;
            }
            try {
                endPortalParallaxShader = new FabricShaderProgram(MinecraftClient.getInstance().getResourceManager(), new Identifier("endportalparallax", "rendertype_end_portal_parallax"), POSITION_TEX3D);
                endPortalParallaxShaderLayerOffsetUniform = endPortalParallaxShader.getUniformOrDefault("LayerOffset");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static net.minecraft.client.gl.ShaderProgram getEndPortalParallaxShader() {
        if (endPortalParallaxShader == null) {
            return GameRenderer.getRenderTypeEndPortalProgram();
        }
        return endPortalParallaxShader;
    }

    public static void borked() {
        if (!renderBorked) {
            renderBorked = true;
            if (MinecraftClient.getInstance().player != null) {
                LOGGER.error("End portal shaders are borked :/");
            }
        }
    }

    public static void renderPortal(BlockEntity blockEntity, Matrix4f mat, VertexConsumer vertexConsumer, float offsetUp, float offsetDown) {
        if (vertexConsumer instanceof BufferVertexConsumer buffer) {
            renderPortalFace(blockEntity, mat, buffer, 1.0F, 0.0F, 1.0F, 1.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.SOUTH);
            renderPortalFace(blockEntity, mat, buffer, 0.0F, 1.0F, 0.0F, 0.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.NORTH);
            renderPortalFace(blockEntity, mat, buffer, 1.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.EAST);
            renderPortalFace(blockEntity, mat, buffer, 0.0F, 0.0F, 1.0F, 0.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.WEST);
            renderPortalFace(blockEntity, mat, buffer, 0.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetDown, offsetDown, offsetDown, Direction.DOWN);
            renderPortalFace(blockEntity, mat, buffer, 0.0F, 1.0F, 1.0F, 0.0F, offsetUp, offsetUp, offsetUp, offsetUp, Direction.UP);
        } else {
            Renderer.borked();
        }
    }

    private static void renderPortalFace(BlockEntity blockEntity, Matrix4f mat, BufferVertexConsumer buffer, float x1, float x2, float z1, float z2, float y1, float y2, float y3, float y4, Direction dir) {
        if (blockEntity.getWorld() == null) {
            borked();
            return;
        }

        if (Block.shouldDrawSide(blockEntity.getCachedState(), blockEntity.getWorld(), blockEntity.getPos(), dir, blockEntity.getPos().offset(dir))) {
            BlockPos blockPos = blockEntity.getPos();
            addPortalVertex(buffer, mat, x1, y1, z1, blockPos.getX() + x1, blockPos.getZ() + z1, y1 - 0.5F);
            addPortalVertex(buffer, mat, x2, y2, z1, blockPos.getX() + x2, blockPos.getZ() + z1, y2 - 0.5F);
            addPortalVertex(buffer, mat, x2, y3, z2, blockPos.getX() + x2, blockPos.getZ() + z2, y3 - 0.5F);
            addPortalVertex(buffer, mat, x1, y4, z2, blockPos.getX() + x1, blockPos.getZ() + z2, y4 - 0.5F);
        }
    }

    private static void addPortalVertex(BufferVertexConsumer buffer, Matrix4f mat, float x, float y, float z, float s, float t, float p) {
        buffer.vertex(mat, x, y, z);
        buffer.putFloat(0, s);
        buffer.putFloat(4, t);
        buffer.putFloat(8, p);
        buffer.nextElement();
        buffer.next();
    }

    public static void updateLayerOffset() {
        endPortalParallaxShaderLayerOffsetUniform.set(System.currentTimeMillis() % 700000L / 700000.0F);
    }

    public static RenderLayer getRenderLayerEndPortalParallax() {
        return RENDERLAYER_END_PORTAL_PARALLAX;
    }
}
