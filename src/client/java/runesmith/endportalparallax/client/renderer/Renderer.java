package runesmith.endportalparallax.client.renderer;

import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import runesmith.endportalparallax.EndPortalParallaxClient;

import java.io.IOException;

public class Renderer extends RenderLayer {
    private static final Identifier endPortalParallaxShaderLocation = new Identifier("endportalparallax", "rendertype_end_portal_parallax");
    private static FabricShaderProgram endPortalParallaxShader;
    private static Uniform endPortalParallaxShaderLayerOffsetUniform = new Uniform();
    private static Uniform endPortalParallaxShaderCameraPosUniform = new Uniform();
    public static final RenderLayer RENDERLAYER_END_PORTAL_PARALLAX = of("end_portal_parallax", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 256, false, false, MultiPhaseParameters.builder().program(new RenderPhase.ShaderProgram(Renderer::getEndPortalParallaxShader)).texture(RenderPhase.Textures.create().add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false).add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false).build()).build(false));
    public static boolean renderBorked = false;

    public Renderer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static void unloadEndPortalParallaxShader() {
        endPortalParallaxShaderLayerOffsetUniform = new Uniform();
        endPortalParallaxShaderCameraPosUniform = new Uniform();
        if (endPortalParallaxShader != null) {
            endPortalParallaxShader.close();
            endPortalParallaxShader = null;
        }
    }

    public static void reloadEndPortalParallaxShader() {
        unloadEndPortalParallaxShader();
        try {
            endPortalParallaxShader = new FabricShaderProgram(MinecraftClient.getInstance().getResourceManager(), endPortalParallaxShaderLocation, VertexFormats.POSITION_TEXTURE);
            endPortalParallaxShaderLayerOffsetUniform = endPortalParallaxShader.getUniformOrDefault("LayerOffset");
            endPortalParallaxShaderCameraPosUniform = endPortalParallaxShader.getUniformOrDefault("CameraPos");
        } catch (IOException e) {
            e.printStackTrace();
            borked(e.getMessage());
        }
    }

    public static net.minecraft.client.gl.ShaderProgram getEndPortalParallaxShader() {
        if (endPortalParallaxShader == null) {
            borked("endPortalParallaxShader == null");
            return GameRenderer.getRenderTypeEndPortalProgram();
        }
        return endPortalParallaxShader;
    }

    public static void borked(String s) {
        if (!renderBorked) {
            renderBorked = true;
            EndPortalParallaxClient.LOGGER.error("endportalparallax: shaders are borked :/ {}", s);
            if (MinecraftClient.getInstance().inGameHud != null) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("endportalparallax: shaders are borked :/ please report"));
            }
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
        if (blockEntity.getWorld() == null) {
            borked("blockEntity.getWorld() == null");
        } else if (Block.shouldDrawSide(blockEntity.getCachedState(), blockEntity.getWorld(), blockEntity.getPos(), dir, blockEntity.getPos().offset(dir))) {
            addPortalVertex(vertexConsumer, mat, x1, y1, z1, y1 - 0.5f);
            addPortalVertex(vertexConsumer, mat, x2, y2, z1, y2 - 0.5f);
            addPortalVertex(vertexConsumer, mat, x2, y3, z2, y3 - 0.5f);
            addPortalVertex(vertexConsumer, mat, x1, y4, z2, y4 - 0.5f);
        }
    }

    private static void addPortalVertex(VertexConsumer vertexConsumer, Matrix4f mat, float x, float y, float z, float p) {
        vertexConsumer.vertex(mat, x, y, z);
        // the UV coord here is just a hack to pass the y level for the portal effect for each vertex
        vertexConsumer.texture(0.0f, p);
        vertexConsumer.next();
    }

    public static void updateCameraPos(Vector3f pos) {
        endPortalParallaxShaderCameraPosUniform.set(pos);
    }

    public static void updateLayerOffset() {
        endPortalParallaxShaderLayerOffsetUniform.set(System.currentTimeMillis() % 700000L / 700000.0F);
    }

    public static RenderLayer getRenderLayerEndPortalParallax() {
        return RENDERLAYER_END_PORTAL_PARALLAX;
    }
}
