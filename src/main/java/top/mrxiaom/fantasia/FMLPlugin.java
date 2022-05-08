package top.mrxiaom.fantasia;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import top.mrxiaom.fantasia.config.ChatConfig;
import top.mrxiaom.fantasia.config.HUDConfig;
import top.mrxiaom.fantasia.config.MainMenuConfig;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name("Fantasia")
public class FMLPlugin implements IFMLLoadingPlugin {
    private static File mcLocation;
    private static File coremodLocation;
    private static File configPath;

    private static MainMenuConfig mainMenuConfig;
    private static ChatConfig chatConfig;
    private static HUDConfig hudConfig;
    public static MainMenuConfig getMainMenuConfig() {
        return mainMenuConfig;
    }

    public static ChatConfig getChatConfig() {
        return chatConfig;
    }

    public static HUDConfig getHudConfig() {
        return hudConfig;
    }

    public static void reloadConfig() {
        if (chatConfig == null) chatConfig = new ChatConfig(new File(FMLPlugin.getConfigPath(), "chat.json"));
        if (mainMenuConfig == null) mainMenuConfig = new MainMenuConfig(new File(FMLPlugin.getConfigPath(), "mainmenu.json"));
        if (hudConfig == null) hudConfig = new HUDConfig(new File(FMLPlugin.getConfigPath(), "hud.json"));
        chatConfig.reloadConfig();
        mainMenuConfig.reloadConfig();
        hudConfig.reloadConfig();
    }

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
        reloadConfig();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
