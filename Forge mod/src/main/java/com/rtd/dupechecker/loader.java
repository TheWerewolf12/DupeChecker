package com.rtd.dupechecker;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = loader.MODID, version = loader.VERSION)
public class loader
{
    public static final String MODID = "dupechecker";
    public static final String VERSION = "1.0";
    public static Boolean Supported = true;
    public static String JavaBuildVersion = null;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new API_calls());
        ClientCommandHandler.instance.registerCommand(new command());
        JavaBuildVersion = getJavaVersion();
    }

    private static String getJavaVersion() {
        String version = System.getProperty("java.version");
        String[] splitted = version.split("_");
        Integer BuildVersion = Integer.valueOf(splitted[splitted.length-1]);
        if (BuildVersion <= 51) {
            System.out.println("[DupeChecker] Incompatitable build version. "+BuildVersion);
            Supported = false;
        }
        return BuildVersion.toString();
    }
}
