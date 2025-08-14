package runesmith.endportalparallax.client.renderer;

import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

import java.io.IOException;

public final class FabricShader extends Shader {
    public FabricShader(ResourceFactory factory, Identifier name, VertexFormat format) throws IOException {
        super(factory, name.toString(), format);
    }

    public static String rewriteAsId(String input, String containedId) {
        Identifier contained = new Identifier(containedId);
        return contained.getNamespace() + Identifier.NAMESPACE_SEPARATOR + input.replace(containedId, contained.getPath());
    }
}