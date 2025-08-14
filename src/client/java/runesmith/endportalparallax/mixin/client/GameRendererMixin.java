package runesmith.endportalparallax.mixin.client;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import runesmith.endportalparallax.client.renderer.Renderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(at = @At("RETURN"), method = "loadShaders")
	private void onLoadShaders(CallbackInfo info) {
		Renderer.reloadEndPortalParallaxShader();
	}
	@Inject(at = @At("RETURN"), method = "clearShaders")
	private void onUnloadShaders(CallbackInfo info) {
		Renderer.unloadEndPortalParallaxShader();
	}
}