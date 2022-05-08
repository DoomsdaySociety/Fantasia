package top.mrxiaom.fantasia;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name("Fantasia")
public class FMLPlugin implements IFMLLoadingPlugin {
    private static File mcLocation;
    private static File coremodLocation;
    private static File configPath;

    public static File getMcLocation() {
        return mcLocation;
    }

    public static File getCoremodLocation() {
        return coremodLocation;
    }

    public static File getConfigPath() {
        return configPath;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return ModWrapper.class.getName();
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        for (String s : data.keySet()) {
            System.out.println(s + ": " + (data.get(s) == null ? "" : ("(" + data.get(s).getClass().getName() + ") ")) + data.get(s));
        }
        mcLocation = (File) data.get("mcLocation");
        coremodLocation = (File) data.get("coremodLocation");

        configPath = new File(FMLPlugin.getMcLocation(), "fantasia");
        ModWrapper.reloadConfig();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
