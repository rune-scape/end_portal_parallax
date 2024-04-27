package runesmith.endportalparallax.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import runesmith.endportalparallax.client.renderer.Renderer;

@Mixin(WorldRenderer.class)
public class EndPortalParallaxWorldRendererMixin {
	@Inject(at = @At("HEAD"), method = "render")
	private void onRender(CallbackInfo info) {
		Renderer.updateCameraPos(new Vec3f(MinecraftClient.getInstance().gameRenderer.getCamera().getPos()));
		Renderer.updateLayerOffset();
	}
}