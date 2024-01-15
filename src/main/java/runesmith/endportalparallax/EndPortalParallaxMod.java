package runesmith.endportalparallax;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import runesmith.endportalparallax.client.renderer.Renderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndGatewayParallaxRenderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndPortalParallaxRenderer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@OnlyIn(Dist.CLIENT)
@Mod("endportalparallax")
public class EndPortalParallaxMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceProvider resourceProvider = null;
    private static KeyMapping reloadShadersKeyMapping = null;
    public static boolean isDev = true;

    public EndPortalParallaxMod(IEventBus modEventBus) {
        modEventBus.register(this);
        NeoForge.EVENT_BUS.register(Renderer.class);
        if (isDev) {
            reloadShadersKeyMapping = new KeyMapping("Reload Shaders", GLFW.GLFW_KEY_F9, "endportalparallax");
            NeoForge.EVENT_BUS.addListener(this::onClientTick);
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
        Renderer.reloadEndPortalParallaxShader(isDev ? Minecraft.getInstance().getResourceManager() : event.getResourceProvider());
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
                    try {
                        Method reloadShadersMethod = GameRenderer.class.getDeclaredMethod("reloadShaders", ResourceProvider.class);
                        reloadShadersMethod.setAccessible(true);
                        reloadShadersMethod.invoke(Minecraft.getInstance().gameRenderer, resourceProvider);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Reloading shaders"));
            }
        }
    }
}
