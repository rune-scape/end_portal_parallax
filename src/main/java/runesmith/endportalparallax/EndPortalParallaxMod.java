package runesmith.endportalparallax;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import runesmith.endportalparallax.client.renderer.Renderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndGatewayParallaxRenderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndPortalParallaxRenderer;

@OnlyIn(Dist.CLIENT)
@Mod("endportalparallax")
public class EndPortalParallaxMod {
    private static final KeyMapping RELOAD_SHADERS_KEYMAPPING = new KeyMapping("Reload Shaders", GLFW.GLFW_KEY_F9, "endportalparallax");

    public EndPortalParallaxMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerShaders);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRenderers);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(RELOAD_SHADERS_KEYMAPPING);
    }

    public void registerShaders(RegisterShadersEvent event) {
        Renderer.reloadEndPortalParallaxShader();
    }

    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityType.END_PORTAL, EndPortalParallaxRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityType.END_GATEWAY, EndGatewayParallaxRenderer::new);
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
                RenderSystem.recordRenderCall(Renderer::reloadEndPortalParallaxShader);
                Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("End portal shaders reloaded"));
            }
        }
    }
}
