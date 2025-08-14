package runesmith.endportalparallax.mixin.client;

import net.minecraft.client.gl.Program;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import runesmith.endportalparallax.client.renderer.FabricShader;

@Mixin(Shader.class)
public class ShaderMixin {
    @Shadow
    @Final
    private String name;

    // Allow loading FabricShaderPrograms from arbitrary namespaces.
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"), allow = 1)
    private String modifyProgramId(String id) {
        if ((Object) this instanceof FabricShader) {
            return FabricShader.rewriteAsId(id, name);
        }

        return id;
    }

    // Allow loading shader stages from arbitrary namespaces.
    @ModifyVariable(method = "loadProgram", at = @At("STORE"), ordinal = 1)
    private static String modifyStageId(String id, ResourceFactory factory, Program.Type type, String name) {
        if (name.contains(String.valueOf(Identifier.NAMESPACE_SEPARATOR))) {
            return FabricShader.rewriteAsId(id, name);
        }

        return id;
    }
}
