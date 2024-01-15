package runesmith.endportalparallax.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.client.shader.ShaderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL13;
import runesmith.endportalparallax.client.renderer.tileentity.EndPortalParallaxRenderer;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class Renderer {
    private static ShaderManager endPortalParallaxShader;
    private static boolean endPortalParallaxShaderDirty = true;
    private static ShaderDefault endPortalParallaxShaderCameraPosUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderLayerOffsetUniform = new ShaderDefault();
    private static ShaderDefault endPortalParallaxShaderPortalYUniform = new ShaderDefault();

    public static void invalidateEndPortalParallaxShader() {
        endPortalParallaxShaderDirty = true;
    }

    public static ShaderManager getEndPortalParallaxShader() {
        if (endPortalParallaxShaderDirty) {
            endPortalParallaxShaderDirty = false;
            endPortalParallaxShaderCameraPosUniform = new ShaderDefault();
            endPortalParallaxShaderLayerOffsetUniform = new ShaderDefault();
            endPortalParallaxShaderPortalYUniform = new ShaderDefault();
            if (endPortalParallaxShader != null) {
                endPortalParallaxShader.close();
                endPortalParallaxShader = null;
            }
            try {
                endPortalParallaxShader = new ShaderManager(Minecraft.getInstance().getResourceManager(), "endportalparallax:rendertype_end_portal_parallax");
                endPortalParallaxShaderCameraPosUniform = endPortalParallaxShader.getShaderUniform("CameraPos");
                endPortalParallaxShaderLayerOffsetUniform = endPortalParallaxShader.getShaderUniformOrDefault("LayerOffset");
                endPortalParallaxShaderPortalYUniform = endPortalParallaxShader.getShaderUniformOrDefault("PortalY");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public static void renderPortal(TileEntity tileEntity, double x, double y, double z, double offsetUp, double offsetDown) {
        GlStateManager.disableAlphaTest();

        double minX = x;
        double maxX = x + 1.0;
        double minY = y + offsetDown;
        double maxY = y + offsetUp;
        double minZ = z;
        double maxZ = z + 1.0;

        ShaderManager shader = getEndPortalParallaxShader();
        if (shader != null) {
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            ITextureObject endSkyTex = texturemanager.getTexture(EndPortalParallaxRenderer.END_SKY_TEXTURE);
            if (endSkyTex == null) {
                endSkyTex = new SimpleTexture(EndPortalParallaxRenderer.END_SKY_TEXTURE);
                texturemanager.loadTexture(EndPortalParallaxRenderer.END_SKY_TEXTURE, endSkyTex);
            }
            shader.addSamplerTexture("Sampler0", endSkyTex);

            ITextureObject endPortalTex = texturemanager.getTexture(EndPortalParallaxRenderer.END_PORTAL_TEXTURE);
            if (endPortalTex == null) {
                endPortalTex = new SimpleTexture(EndPortalParallaxRenderer.END_PORTAL_TEXTURE);
                texturemanager.loadTexture(EndPortalParallaxRenderer.END_PORTAL_TEXTURE, endPortalTex);
            }
            shader.addSamplerTexture("Sampler1", endPortalTex);

            endPortalParallaxShaderPortalYUniform.set((float) tileEntity.getPos().getY() + 0.5F);

            shader.useShader();
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION);

        renderPortalFace(tileEntity, buffer, maxX, minX, maxZ, maxZ, maxY, maxY, minY, minY, EnumFacing.SOUTH);
        renderPortalFace(tileEntity, buffer, minX, maxX, minZ, minZ, maxY, maxY, minY, minY, EnumFacing.NORTH);
        renderPortalFace(tileEntity, buffer, maxX, maxX, minZ, maxZ, minY, maxY, maxY, minY, EnumFacing.EAST);
        renderPortalFace(tileEntity, buffer, minX, minX, maxZ, minZ, minY, maxY, maxY, minY, EnumFacing.WEST);
        renderPortalFace(tileEntity, buffer, minX, maxX, minZ, maxZ, minY, minY, minY, minY, EnumFacing.DOWN);
        renderPortalFace(tileEntity, buffer, minX, maxX, maxZ, minZ, maxY, maxY, maxY, maxY, EnumFacing.UP);

        tessellator.draw();

        if (shader != null) {
            shader.endShader();
        }

        GlStateManager.activeTexture(GL13.GL_TEXTURE0);
    }

    private static void renderPortalFace(TileEntity tileEntity, BufferBuilder buffer, double x1, double x2, double z1, double z2, double y1, double y2, double y3, double y4, EnumFacing dir) {
        if (Block.shouldSideBeRendered(tileEntity.getBlockState(), tileEntity.getWorld(), tileEntity.getPos(), dir)) {
            buffer.pos(x1, y1, z1).endVertex();
            buffer.pos(x2, y2, z1).endVertex();
            buffer.pos(x2, y3, z2).endVertex();
            buffer.pos(x1, y4, z2).endVertex();
        }
    }

    public static void updateCameraPos(Vec3d position) {
        endPortalParallaxShaderCameraPosUniform.set((float) position.x, (float) position.y, (float) position.z);
    }

    public static void updateLayerOffset() {
        endPortalParallaxShaderLayerOffsetUniform.set(System.currentTimeMillis() % 700000L / 700000.0F);
    }
}
