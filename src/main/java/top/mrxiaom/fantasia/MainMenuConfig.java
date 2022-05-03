package top.mrxiaom.fantasia;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainMenuConfig extends AbstractConfig{
    Logger logger = LogManager.getLogger("Fantasia-MainMenu");
    public final List<String> servers = new ArrayList<>();
    public String selected;
    public boolean isParallax;
    public MainMenuConfig(File configFile) {
        super(configFile);
    }

    public List<ServerData> getServerList() {
        List<ServerData> serverList = new ArrayList<>();
        for (String name : servers) {
            if (!name.contains(":")) continue;
            int i = name.indexOf(":");
            serverList.add(new ServerData(name.substring(0, i), name.substring(i + 1), false));
        }
        return serverList;
    }

    @Override
    public void loadDefaultConfig() {
        logger.info("正在保存默认配置文件");

        servers.clear();

        servers.add("§a自动路线:mc.66ko.cc");
        servers.add("§b电信路线:dx.66ko.cc");
        servers.add("§b联通路线:lt.66ko.cc");
        servers.add("§b移动路线:yd.66ko.cc");

        selected = "§a自动路线";

        isParallax = true;
        saveConfig();
    }

    public void reloadConfig() {
        try {
            if (!configFile.exists()) throw new FileNotFoundException("找不到配置文件");
            JsonElement jsonElement = new JsonParser().parse(Utils.readAsString(configFile));

            servers.clear();

            JsonObject json = jsonElement.getAsJsonObject();
            JsonArray serverArray = json.get("servers").getAsJsonArray();
            for (int i = 0; i < serverArray.size(); i++) {
                servers.add(serverArray.get(i).getAsString());
            }
            selected = json.get("selected-server").getAsString();
            isParallax = json.get("is-parallax").getAsBoolean();
        } catch (Throwable t) {
            logger.warn("无法加载配置文件", t);
            if (configFile.exists()) {
                configFile.renameTo(new File(configFile.getParentFile(), configFile.getName() + ".old"));
            }
            loadDefaultConfig();
        }
    }

    @Override
    public void saveConfig() {
        JsonObject json = new JsonObject();
        JsonArray serversArray = new JsonArray();
        for (String s : servers) {
            serversArray.add(s);
        }
        json.add("servers", serversArray);
        json.addProperty("selected-server", selected);
        json.addProperty("is-parallax", isParallax);
        Utils.saveFromString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
