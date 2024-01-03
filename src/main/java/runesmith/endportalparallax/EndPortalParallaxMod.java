package runesmith.endportalparallax;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runesmith.endportalparallax.client.renderer.Renderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndGatewayParallaxRenderer;
import runesmith.endportalparallax.client.renderer.blockentity.EndPortalParallaxRenderer;

public class EndPortalParallaxMod implements ClientModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("endportalparallax");
    //private static KeyBinding reload_binding;

    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
//        reload_binding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.endportalparallax.reload_shaders", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.endportalparallax.endportalparallax"));
//        ClientTickEvents.START_CLIENT_TICK.register(client -> {
//            while (reload_binding.wasPressed()) {
//                Renderer.reloadEndPortalParallaxShader();
//                client.player.sendMessage(Text.literal("End portal shaders reloaded"), false);
//            }
//        });
        Renderer.reloadEndPortalParallaxShader();
        BlockEntityRendererFactories.register(BlockEntityType.END_PORTAL, EndPortalParallaxRenderer<EndPortalBlockEntity>::new);
        BlockEntityRendererFactories.register(BlockEntityType.END_GATEWAY, EndGatewayParallaxRenderer::new);
    }
}