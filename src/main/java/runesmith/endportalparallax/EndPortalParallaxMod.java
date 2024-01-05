package runesmith.endportalparallax;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
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
import net.neoforged.neoforge.client.event.ViewportEvent;
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
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceManager resourceManager = null;
    private static final KeyMapping RELOAD_SHADERS_KEYMAPPING = new KeyMapping("Reload Shaders", GLFW.GLFW_KEY_F9, "endportalparallax");

    public EndPortalParallaxMod(IEventBus modEventBus) {
        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::registerShaders);
        modEventBus.addListener(this::registerRenderers);

        NeoForge.EVENT_BUS.register(this);
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
                RenderSystem.recordRenderCall(() -> {
                    try {
                        Method reloadShadersMethod = GameRenderer.class.getDeclaredMethod("reloadShaders", ResourceProvider.class);
                        reloadShadersMethod.setAccessible(true);
                        reloadShadersMethod.invoke(Minecraft.getInstance().gameRenderer, resourceManager);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Reloading shaders"));
            }
        }
    }
}
