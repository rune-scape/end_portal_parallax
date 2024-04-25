package runesmith.endportalparallax.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import runesmith.endportalparallax.client.renderer.Renderer;

@Mixin(GameRenderer.class)
public class EndPortalParallaxGameRendererMixin {
	@Inject(at = @At("RETURN"), method = "loadPrograms")
	private void onLoadShaders(CallbackInfo info) {
		Renderer.reloadEndPortalParallaxShader();
	}
	@Inject(at = @At("RETURN"), method = "clearPrograms")
	private void onUnloadShaders(CallbackInfo info) {
		Renderer.unloadEndPortalParallaxShader();
	}
}