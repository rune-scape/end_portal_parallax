package runesmith.endportalparallax;

import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import runesmith.endportalparallax.client.renderer.Renderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndGatewayParallaxRenderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndPortalParallaxRenderer;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
@Mod("endportalparallax")
public class EndPortalParallaxMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceManager resourceManager = null;
    private static final KeyMapping RELOAD_SHADERS_KEYMAPPING = new KeyMapping("Reload Shaders", GLFW.GLFW_KEY_F9, "endportalparallax");

    public EndPortalParallaxMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeyMappings);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerShaders);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRenderers);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(RELOAD_SHADERS_KEYMAPPING);
    }

    public void registerShaders(RegisterShadersEvent event) {
        resourceManager = Minecraft.getInstance().getResourceManager();
        Renderer.reloadEndPortalParallaxShader(resourceManager);
    }

    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityType.END_PORTAL, EndPortalParallaxRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityType.END_GATEWAY, EndGatewayParallaxRenderer::new);
    }

    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Renderer.updateCameraPos(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) { // Only call code once as the tick event is called twice every tick
            Renderer.updateLayerOffset();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            while (RELOAD_SHADERS_KEYMAPPING.consumeClick()) {
                RenderSystem.recordRenderCall(() -> Minecraft.getInstance().gameRenderer.reloadShaders(resourceManager));
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Reloading shaders"));
            }
        }
    }
}
