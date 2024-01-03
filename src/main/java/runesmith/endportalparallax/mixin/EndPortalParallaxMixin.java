package runesmith.endportalparallax.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import runesmith.endportalparallax.client.renderer.Renderer;

@Mixin(MinecraftClient.class)
public class EndPortalParallaxMixin {
	@Inject(at = @At("HEAD"), method = "render")
	private void onRender(CallbackInfo info) {
		Renderer.updateLayerOffset();
	}
}