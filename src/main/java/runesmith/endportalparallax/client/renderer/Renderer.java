package runesmith.endportalparallax.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.client.shader.ShaderInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class Renderer {
    private static ShaderInstance endPortalParallaxShader;
    private static ShaderDefault endPortalParallaxShaderIViewRotMatUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderCameraPosUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderPortalYUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderLayerOffsetUniform = new ShaderDefault();
    private static final HashMap<Integer, RenderType> RENDERTYPE_END_PORTAL_PARALLAX_MAP = new HashMap<>();

    public static void reloadEndPortalParallaxShader() {
        endPortalParallaxShaderIViewRotMatUniform = new ShaderDefault();
        endPortalParallaxShaderCameraPosUniform = new ShaderDefault();
        endPortalParallaxShaderPortalYUniform = new ShaderDefault();
        endPortalParallaxShaderLayerOffsetUniform = new ShaderDefault();
        if (endPortalParallaxShader != null) {
            endPortalParallaxShader.close();
            endPortalParallaxShader = null;
        }
        try {
            endPortalParallaxShader = new ShaderInstance(Minecraft.getInstance().getResourceManager(), "endportalparallax:rendertype_end_portal_parallax");
            endPortalParallaxShaderIViewRotMatUniform = endPortalParallaxShader.safeGetUniform("IViewRotMat");
            endPortalParallaxShaderCameraPosUniform = endPortalParallaxShader.safeGetUniform("CameraPos");
            endPortalParallaxShaderPortalYUniform = endPortalParallaxShader.safeGetUniform("PortalY");
            endPortalParallaxShaderLayerOffsetUniform = endPortalParallaxShader.safeGetUniform("LayerOffset");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ShaderInstance getEndPortalParallaxShader() {
        return endPortalParallaxShader;
    }

//    private static Field GlStateManager_activeTextureField;
//    static {
//        try {
//            GlStateManager_activeTextureField = GlStateManager.class.getDeclaredField("activeTexture");
//            GlStateManager_activeTextureField.setAccessible(true);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static int getActiveTexture() {
//        try {
//            return GL13.GL_TEXTURE0 + GlStateManager_activeTextureField.getInt(null);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return -1;
//    }

    @OnlyIn(Dist.CLIENT)
    public static final class ParallaxPortalTexturingState extends RenderState.TexturingState {
        private final int portalY;

        public ParallaxPortalTexturingState(Supplier<ShaderInstance> shaderInstanceSupplier, int portalY) {
            super("parallax_portal_texturing", () -> {
                ShaderInstance shaderInstance = shaderInstanceSupplier.get();
                if (shaderInstance == null) {
                    return;
                }

                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                shaderInstance.setSampler("Sampler0", () -> {
                    Texture endSkyTex = texturemanager.getTexture(EndPortalTileEntityRenderer.END_SKY_LOCATION);
                    if (endSkyTex == null) {
                        endSkyTex = new SimpleTexture(EndPortalTileEntityRenderer.END_SKY_LOCATION);
                        texturemanager.register(EndPortalTileEntityRenderer.END_SKY_LOCATION, endSkyTex);
                    }
                    return endSkyTex.getId();
                });
                shaderInstance.setSampler("Sampler1", () -> {
                    Texture endPortalTex = texturemanager.getTexture(EndPortalTileEntityRenderer.END_PORTAL_LOCATION);
                    if (endPortalTex == null) {
                        endPortalTex = new SimpleTexture(EndPortalTileEntityRenderer.END_PORTAL_LOCATION);
                        texturemanager.register(EndPortalTileEntityRenderer.END_PORTAL_LOCATION, endPortalTex);
                    }
                    return endPortalTex.getId();
                });
                endPortalParallaxShaderPortalYUniform.set(portalY + 0.5F);
                shaderInstance.apply();
            }, () -> {
                ShaderInstance shaderInstance = shaderInstanceSupplier.get();
                if (shaderInstance == null) {
                    return;
                }

                shaderInstance.clear();
                RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            });
            this.portalY = portalY;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                ParallaxPortalTexturingState ppts = (ParallaxPortalTexturingState) o;
                return this.portalY == ppts.portalY;
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Integer.hashCode(portalY);
        }
    }

    public static void renderPortal(TileEntity tileEntity, Matrix4f mat, BufferBuilder buffer, float offsetUp, float offsetDown) {
        renderPortalFace(tileEntity, mat, buffer, 1.0F, 0.0F, 1.0F, 1.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.SOUTH);
        renderPortalFace(tileEntity, mat, buffer, 0.0F, 1.0F, 0.0F, 0.0F, offsetUp, offsetUp, offsetDown, offsetDown, Direction.NORTH);
        renderPortalFace(tileEntity, mat, buffer, 1.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.EAST);
        renderPortalFace(tileEntity, mat, buffer, 0.0F, 0.0F, 1.0F, 0.0F, offsetDown, offsetUp, offsetUp, offsetDown, Direction.WEST);
        renderPortalFace(tileEntity, mat, buffer, 0.0F, 1.0F, 0.0F, 1.0F, offsetDown, offsetDown, offsetDown, offsetDown, Direction.DOWN);
        renderPortalFace(tileEntity, mat, buffer, 0.0F, 1.0F, 1.0F, 0.0F, offsetUp, offsetUp, offsetUp, offsetUp, Direction.UP);
    }

    private static void renderPortalFace(TileEntity tileEntity, Matrix4f mat, BufferBuilder buffer, float x1, float x2, float z1, float z2, float y1, float y2, float y3, float y4, Direction dir) {
        if (Block.shouldRenderFace(tileEntity.getBlockState(), tileEntity.getLevel(), tileEntity.getBlockPos(), dir)) {
            buffer.vertex(mat, x1, y1, z1).endVertex();
            buffer.vertex(mat, x2, y2, z1).endVertex();
            buffer.vertex(mat, x2, y3, z2).endVertex();
            buffer.vertex(mat, x1, y4, z2).endVertex();
        }
    }

    public static void updateInverseViewRotationMatrix(float yaw, float pitch, float roll) {
        Quaternion quat = new Quaternion(pitch, yaw + 180.0F, roll, true);
        Matrix4f mat = new Matrix4f(quat);
        if (mat.invert()) {
            endPortalParallaxShaderIViewRotMatUniform.set(mat);
        }
    }

    public static void updateCameraPos(Vector3d position) {
        endPortalParallaxShaderCameraPosUniform.set((float) position.x, (float) position.y, (float) position.z);
    }

    public static void updateLayerOffset() {
        endPortalParallaxShaderLayerOffsetUniform.set(System.currentTimeMillis() % 700000L / 700000.0F);
    }

    public static RenderType getRendertypeEndPortalParallax(int portalY) {
        return RENDERTYPE_END_PORTAL_PARALLAX_MAP.computeIfAbsent(portalY, pPortalY -> RenderType.create("end_portal_parallax", DefaultVertexFormats.POSITION, GL11.GL_QUADS, 256, false, false, RenderType.State.builder().setTexturingState(new ParallaxPortalTexturingState(Renderer::getEndPortalParallaxShader, pPortalY)).createCompositeState(false)));
    }
}
