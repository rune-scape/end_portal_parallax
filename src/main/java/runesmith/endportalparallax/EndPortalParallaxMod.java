package runesmith.endportalparallax;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import runesmith.endportalparallax.client.renderer.Renderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndGatewayParallaxRenderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndPortalParallaxRenderer;

@OnlyIn(Dist.CLIENT)
@Mod("endportalparallax")
public class EndPortalParallaxMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static KeyMapping reloadShadersKeyMapping = null;
    public static boolean isDev = false;

    public EndPortalParallaxMod(IEventBus modEventBus) {
        modEventBus.register(this);
        MinecraftForge.EVENT_BUS.register(Renderer.class);
        if (isDev) {
            reloadShadersKeyMapping = new KeyMapping("Reload Shaders", GLFW.GLFW_KEY_F9, "endportalparallax");
            MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
        }
    }

    @SubscribeEvent
    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        if (isDev) {
            event.register(reloadShadersKeyMapping);
        }
    }

    @SubscribeEvent
    public void registerShaders(RegisterShadersEvent event) {
        Renderer.reloadEndPortalParallaxShader(isDev ? Minecraft.getInstance().getResourceManager() : event.getResourceManager());
    }

    @SubscribeEvent
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityType.END_PORTAL, EndPortalParallaxRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityType.END_GATEWAY, EndGatewayParallaxRenderer::new);
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (isDev && event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            while (reloadShadersKeyMapping.consumeClick()) {
                RenderSystem.recordRenderCall(() -> {
                    Minecraft.getInstance().gameRenderer.reloadShaders(Minecraft.getInstance().getResourceManager());
                });
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Reloading shaders"));
            }
        }
    }
}
