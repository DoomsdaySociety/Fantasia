package top.mrxiaom.fantasia.config;

import net.minecraft.client.multiplayer.ServerData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainMenuConfig extends AbstractConfig {
    @Config("servers")
    public final List<String> servers = defaultServers();
    @Config("selected-server")
    public String selected = "§a自动路线";
    @Config("is-override-background")
    public boolean overrideBackground = true;
    @Config("is-parallax")
    public boolean isParallax = true;
    @Config("merchant-delay")
    public int merchantDelay = 20;

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

    public static List<String> defaultServers() {
        List<String> servers = new ArrayList<>();
        servers.add("§a自动路线:mc.66ko.cc");
        servers.add("§b电信路线:dx.66ko.cc");
        servers.add("§b联通路线:lt.66ko.cc");
        servers.add("§b移动路线:yd.66ko.cc");
        return servers;
    }
}
