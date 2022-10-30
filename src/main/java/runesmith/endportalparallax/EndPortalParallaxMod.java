package runesmith.endportalparallax;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import org.lwjgl.glfw.GLFW;
import runesmith.endportalparallax.client.renderer.Renderer;
import runesmith.endportalparallax.client.renderer.tileentity.EndGatewayParallaxRenderer;
import runesmith.endportalparallax.client.renderer.tileentity.EndPortalParallaxRenderer;

@OnlyIn(Dist.CLIENT)
@Mod("endportalparallax")
public class EndPortalParallaxMod {
//    private static final KeyBinding RELOAD_SHADERS_KEYBINDING = new KeyBinding("Reload Shaders", GLFW.GLFW_KEY_F9, "endportalparallax");

    public EndPortalParallaxMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setup(FMLClientSetupEvent event) {
        Renderer.invalidateEndPortalParallaxShader();
        ClientRegistry.bindTileEntitySpecialRenderer(EndPortalTileEntity.class, new EndPortalParallaxRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(EndGatewayTileEntity.class, new EndGatewayParallaxRenderer());
//        ClientRegistry.registerKeyBinding(RELOAD_SHADERS_KEYBINDING);
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Renderer.updateCameraPos(event.getInfo().getProjectedView());
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) { // Only call code once as the tick event is called twice every tick
            Renderer.updateLayerOffset();
        }
    }

//    @SubscribeEvent
//    public void onClientTick(TickEvent.ClientTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
//            while (RELOAD_SHADERS_KEYBINDING.isPressed()) {
//                Renderer.invalidateEndPortalParallaxShader();
//                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent("End portal shaders reloaded"));
//            }
//        }
//    }
}
