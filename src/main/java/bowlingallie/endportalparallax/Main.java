package bowlingallie.endportalparallax;

import bowlingallie.endportalparallax.client.renderer.tileentity.TileEntityEndGatewayParallaxRenderer;
import bowlingallie.endportalparallax.client.renderer.tileentity.TileEntityEndPortalParallaxRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "endportalparallax";
    public static final String NAME = "End Portal Parallax";
    public static final String VERSION = "1.0";
    public static final String CLIENT_PROXY_CLASS = "bowlingallie.endportalparallax.proxy.ClientProxy";

    private static Logger logger;
    @Instance
    public static Main instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        TileEntityRendererDispatcher.instance.renderers.put(TileEntityEndPortal.class, new TileEntityEndPortalParallaxRenderer());
        TileEntityRendererDispatcher.instance.renderers.put(TileEntityEndGateway.class, new TileEntityEndGatewayParallaxRenderer());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
