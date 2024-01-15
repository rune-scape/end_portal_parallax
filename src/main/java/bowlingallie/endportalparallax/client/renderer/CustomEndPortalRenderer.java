package bowlingallie.endportalparallax.client.renderer;

import com.sun.javafx.geom.Vec3f;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class CustomEndPortalRenderer {
    private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random RANDOM = new Random(31100L);
    private final FloatBuffer texBuffer = GLAllocation.createDirectFloatBuffer(16);

    public void render(TileEntityEndPortal portalEntity, double posX, double posY, double posZ, Vec3d cameraPos, TextureManager r) {
        if (r == null) {
            return;
        }
        float minX = (float) posX;
        float maxX = minX + 1.0F;
        float minY = (float) posX;
        float maxY = minY + 1.0F;
        float minX = (float) posX;
        AxisAlignedBB aabb = portalEntity.getBlockType().getBlockState().getBaseState().getBoundingBox(portalEntity.getWorld(), portalEntity.getPos()).offset(posX, posY, posZ);
        GlStateManager.disableLighting();
        RANDOM.setSeed(31100L);
        for (int i = 0; i < 16; i++) {
            GlStateManager.pushMatrix();
            float portalSurfaceY = (float) aabb.maxY;
            float layerDepth = 16 - i;
            float layerScale = 0.0625F;
            float layerColorStrength = 1.0F / (layerDepth + 1.0F);
            if (i == 0) {
                r.bindTexture(END_SKY_TEXTURE);
                layerColorStrength = 0.1F;
                layerDepth = 65F;
                layerScale = 0.125F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            if (i == 1) {
                r.bindTexture(END_PORTAL_TEXTURE);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                layerScale = 0.5F;
            }
            float portalSurfaceOffsetY = (float) (cameraPos.y - portalSurfaceY);
            float layerOffsetY = layerDepth + portalSurfaceOffsetY;
            float layerSurfaceY = (portalSurfaceOffsetY / layerOffsetY) + portalSurfaceY;
            GlStateManager.translate(cameraPos.x, layerSurfaceY, cameraPos.z);
            GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL_EYE_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_PLANE, this.bufferTexData(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_PLANE, this.bufferTexData(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_PLANE, this.bufferTexData(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL_EYE_PLANE, this.bufferTexData(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
            GlStateManager.popMatrix();

            GlStateManager.matrixMode(GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, System.currentTimeMillis() % 700000L / 700000F, 0.0F);
            GlStateManager.scale(layerScale, layerScale, layerScale);
            GlStateManager.translate(0.5F, 0.5F, 0.0F);
            GlStateManager.rotate((i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
            GlStateManager.translate(-cameraPos.x, -cameraPos.z, -cameraPos.y);
            GlStateManager.translate(((float) cameraPos.x * layerDepth) / portalSurfaceOffsetY, ((float) cameraPos.z * layerDepth) / portalSurfaceOffsetY, -cameraPos.y + 20);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            float red = (RANDOM.nextFloat() * 0.5F + 0.1F) * layerColorStrength;
            float green = (RANDOM.nextFloat() * 0.5F + 0.4F) * layerColorStrength;
            float blue = (RANDOM.nextFloat() * 0.5F + 0.5F) * layerColorStrength;
            if (i == 0) {
                red = green = blue = layerColorStrength;
            }

            if (portalEntity.shouldRenderFace(EnumFacing.SOUTH)) {
                buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
            }

            if (portalEntity.shouldRenderFace(EnumFacing.NORTH)) {
                buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
            }

            if (portalEntity.shouldRenderFace(EnumFacing.EAST)) {
                buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
            }

            if (portalEntity.shouldRenderFace(EnumFacing.WEST)) {
                buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
            }

            if (portalEntity.shouldRenderFace(EnumFacing.DOWN)) {
                buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
            }

            if (portalEntity.shouldRenderFace(EnumFacing.UP)) {
                buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(red, green, blue, 1.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL_MODELVIEW);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }

    private FloatBuffer bufferTexData(float f, float f1, float f2, float f3) {
        texBuffer.clear();
        texBuffer.put(f).put(f1).put(f2).put(f3);
        texBuffer.flip();
        return texBuffer;
    }
}
