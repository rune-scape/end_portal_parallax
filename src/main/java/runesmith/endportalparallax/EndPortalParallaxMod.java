package runesmith.endportalparallax;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import runesmith.endportalparallax.client.renderer.Renderer;
import runesmith.endportalparallax.client.renderer.tileentity.EndGatewayParallaxRenderer;
import runesmith.endportalparallax.client.renderer.tileentity.EndPortalParallaxRenderer;

@OnlyIn(Dist.CLIENT)
@Mod("endportalparallax")
public class EndPortalParallaxMod {
    private static final KeyBinding RELOAD_SHADERS_KEYBINDING = new KeyBinding("Reload Shaders", GLFW.GLFW_KEY_F9, "endportalparallax");

    public EndPortalParallaxMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEndPortal.class, new EndPortalParallaxRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEndGateway.class, new EndGatewayParallaxRenderer());
        ClientRegistry.registerKeyBinding(RELOAD_SHADERS_KEYBINDING);
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Renderer.updateCameraPos(ActiveRenderInfo.getCameraPosition());
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
            while (RELOAD_SHADERS_KEYBINDING.isPressed()) {
                Renderer.invalidateEndPortalParallaxShader();
                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("End portal shaders reloaded"));
            }
        }
    }
}
