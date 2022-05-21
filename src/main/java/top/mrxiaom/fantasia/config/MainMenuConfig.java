package top.mrxiaom.fantasia.config;

import com.google.gson.*;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.mrxiaom.fantasia.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuConfig extends AbstractConfig {
    Logger logger = LogManager.getLogger("Fantasia-MainMenu");
    public final List<String> servers = new ArrayList<>();
    public String selected;
    public boolean isParallax;
    public boolean overrideBackground;
    public int merchantDelay;
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
        servers.clear();

        servers.add("§a自动路线:mc.66ko.cc");
        servers.add("§b电信路线:dx.66ko.cc");
        servers.add("§b联通路线:lt.66ko.cc");
        servers.add("§b移动路线:yd.66ko.cc");

        selected = "§a自动路线";

        isParallax = true;
        overrideBackground = true;
        merchantDelay = 20;
    }

    public void reloadConfig() {
        try {
            if (!configFile.exists()) throw new FileNotFoundException("找不到配置文件");
            JsonElement jsonElement = new JsonParser().parse(Utils.readAsString(configFile));
            JsonObject json = jsonElement.getAsJsonObject();

            loadDefaultConfig();

            if (json.has("servers")) {
                JsonArray serverArray = json.get("servers").getAsJsonArray();
                servers.clear();
                for (int i = 0; i < serverArray.size(); i++) {
                    servers.add(serverArray.get(i).getAsString());
                }
            }
            if(json.has("selected-server")) selected = json.get("selected-server").getAsString();
            if(json.has("is-parallax")) isParallax = json.get("is-parallax").getAsBoolean();
            if(json.has("is-override-background")) overrideBackground = json.get("is-override-background").getAsBoolean();
            if(json.has("merchant-delay")) merchantDelay = json.get("merchant-delay").getAsInt();
        } catch (Throwable t) {
            logger.warn("无法加载配置文件", t);
            if (configFile.exists()) {
                configFile.renameTo(new File(configFile.getParentFile(), configFile.getName() + ".old"));
            }
            loadDefaultConfig();
            saveConfig();
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
        json.addProperty("is-override-background", overrideBackground);
        json.addProperty("merchant-delay", merchantDelay);
        Utils.saveFromString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
