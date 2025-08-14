package runesmith.endportalparallax.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import runesmith.endportalparallax.client.renderer.Renderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Inject(at = @At("HEAD"), method = "render")
	private void onRender(CallbackInfo info) {
		Renderer.updateCameraPos(MinecraftClient.getInstance().gameRenderer.getCamera().getPos().toVector3f());
		Renderer.updateLayerOffset();
	}
}