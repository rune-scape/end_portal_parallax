package runesmith.endportalparallax.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.client.shader.ShaderInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class Renderer {
    private static ShaderInstance endPortalParallaxShader;
    private static ShaderDefault endPortalParallaxShaderIViewRotMatUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderCameraPosUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderPortalYUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderLayerOffsetUniform = new ShaderDefault();
    private static final HashMap<Integer, RenderType> END_PORTAL_PARALLAX_MAP = new HashMap<>();

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
            endPortalParallaxShaderIViewRotMatUniform = endPortalParallaxShader.getShaderUniform("IViewRotMat");
            endPortalParallaxShaderCameraPosUniform = endPortalParallaxShader.getShaderUniform("CameraPos");
            endPortalParallaxShaderPortalYUniform = endPortalParallaxShader.getShaderUniform("PortalY");
            endPortalParallaxShaderLayerOffsetUniform = endPortalParallaxShader.getShaderUniform("LayerOffset");
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
                Texture endSkyTex = texturemanager.getTexture(EndPortalTileEntityRenderer.END_SKY_TEXTURE);
                if (endSkyTex == null) {
                    endSkyTex = new SimpleTexture(EndPortalTileEntityRenderer.END_SKY_TEXTURE);
                    texturemanager.loadTexture(EndPortalTileEntityRenderer.END_SKY_TEXTURE, endSkyTex);
                }
                shaderInstance.func_216537_a("Sampler0", endSkyTex);

                Texture endPortalTex = texturemanager.getTexture(EndPortalTileEntityRenderer.END_PORTAL_TEXTURE);
                if (endPortalTex == null) {
                    endPortalTex = new SimpleTexture(EndPortalTileEntityRenderer.END_PORTAL_TEXTURE);
                    texturemanager.loadTexture(EndPortalTileEntityRenderer.END_PORTAL_TEXTURE, endPortalTex);
                }
                shaderInstance.func_216537_a("Sampler1", endPortalTex);
                endPortalParallaxShaderPortalYUniform.set(portalY + 0.5F);
                shaderInstance.func_216535_f();
            }, () -> {
                ShaderInstance shaderInstance = shaderInstanceSupplier.get();
                if (shaderInstance == null) {
                    return;
                }

                shaderInstance.func_216544_e();
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
        if (Block.shouldSideBeRendered(tileEntity.getBlockState(), tileEntity.getWorld(), tileEntity.getPos(), dir)) {
            buffer.pos(mat, x1, y1, z1).endVertex();
            buffer.pos(mat, x2, y2, z1).endVertex();
            buffer.pos(mat, x2, y3, z2).endVertex();
            buffer.pos(mat, x1, y4, z2).endVertex();
        }
    }

    public static void updateInverseViewRotationMatrix(float yaw, float pitch, float roll) {
        Quaternion quat = new Quaternion(pitch, yaw + 180.0F, roll, true);
        Matrix4f mat = new Matrix4f(quat);
        if (mat.invert()) {
            endPortalParallaxShaderIViewRotMatUniform.set(mat);
        }
    }

    public static void updateCameraPos(Vec3d position) {
        endPortalParallaxShaderCameraPosUniform.set((float) position.x, (float) position.y, (float) position.z);
    }

    public static void updateLayerOffset() {
        endPortalParallaxShaderLayerOffsetUniform.set(System.currentTimeMillis() % 700000L / 700000.0F);
    }

    public static RenderType getRendertypeEndPortalParallax(int portalY) {
        return END_PORTAL_PARALLAX_MAP.computeIfAbsent(portalY, pPortalY -> RenderType.makeType("end_portal_parallax", DefaultVertexFormats.POSITION, GL11.GL_QUADS, 256, false, false, RenderType.State.getBuilder().texturing(new ParallaxPortalTexturingState(Renderer::getEndPortalParallaxShader, pPortalY)).build(false)));
    }
}
