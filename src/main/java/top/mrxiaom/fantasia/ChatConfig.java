package top.mrxiaom.fantasia;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatConfig extends AbstractConfig{
    Logger logger = LogManager.getLogger("Fantasia-Chat");

    List<String> ignoredPlayers = new ArrayList<>();
    String chatPattern;
    int prefixGroup = 1;
    int nameGroup = 2;
    int suffixGroup = 3;
    Map<String, String> suffixes = new HashMap<>();
    Pattern compiledChatPattern;
    String chatReplaceFormat;
    Map<String, String> chatReplacement = new HashMap<>();
    List<String> ignoredKeywords = new ArrayList<>();
    List<String> ignoredPatterns = new ArrayList<>();
    public ChatConfig(File configFile) {
        super(configFile);
    }

    public void loadDefaultConfig() {
        logger.info("正在保存默认配置文件");
        ignoredPlayers.clear();
        ignoredKeywords.clear();
        ignoredPatterns.clear();
        chatReplacement.clear();
        suffixes.clear();
        /*
            采样(&应替换为§): &7[&b&l‣&是&7] &f<&7[&6开荒者&7]&eLittleCatX&7[&6&l✓&7]&f> &fa.a
        */
        chatPattern = "§7\\[§b§l‣§是§7] §f<(.*)§e([A-Za-z0-9_]*)(.*)?§f> (.*)";
        compiledChatPattern = Pattern.compile(chatPattern);
        chatReplaceFormat = "$prefix$player$suffix &7>> ";
        prefixGroup = 1;
        nameGroup = 2;
        suffixGroup = 3;
        suffixes.put("none", "§7§l✓");
        suffixes.put("vip", "§b§l✓");
        suffixes.put("svip", "§6§l✓");
        suffixes.put("mvp", "§c§l✓");
        saveConfig();
    }

    public void reloadConfig() {
        try {
            if (!configFile.exists()) throw new FileNotFoundException("找不到配置文件");
            JsonElement jsonElement = new JsonParser().parse(Utils.readAsString(configFile));

            ignoredPlayers.clear();
            ignoredKeywords.clear();
            ignoredPatterns.clear();
            chatReplacement.clear();
            suffixes.clear();

            JsonObject json = jsonElement.getAsJsonObject();

            JsonObject chatSettings = json.get("chat-format").getAsJsonObject();
            chatPattern = chatSettings.get("pattern").getAsString();
            compiledChatPattern = Pattern.compile(chatPattern);
            chatReplaceFormat = chatSettings.get("new-format").getAsString().replace("&", "§");
            prefixGroup = chatSettings.get("prefix-group").getAsInt();
            nameGroup = chatSettings.get("player-group").getAsInt();
            suffixGroup = chatSettings.get("suffix-group").getAsInt();
            JsonObject suffixObject = chatSettings.get("suffixes").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : suffixObject.entrySet()) {
                suffixes.put(entry.getKey(), entry.getValue().getAsString().replace("&", "§"));
            }

            JsonArray players = json.get("ignored-players").getAsJsonArray();
            JsonArray keywords = json.get("ignored-keywords").getAsJsonArray();
            JsonArray patterns = json.get("ignored-patterns").getAsJsonArray();
            JsonObject chatObject = json.get("chat-replacement").getAsJsonObject();

            for (int i = 0; i < players.size(); i++) {
                ignoredPlayers.add(players.get(i).getAsString());
            }
            for (int i = 0; i < keywords.size(); i++) {
                ignoredKeywords.add(keywords.get(i).getAsString());
            }
            for (int i = 0; i < patterns.size(); i++) {
                ignoredPatterns.add(patterns.get(i).getAsString());
            }
            for (Map.Entry<String, JsonElement> entry : chatObject.entrySet()) {
                chatReplacement.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (Throwable t) {
            logger.warn("无法加载配置文件", t);
            if (configFile.exists()) {
                configFile.renameTo(new File(configFile.getParentFile(), configFile.getName() + ".old"));
            }
            loadDefaultConfig();
        }
    }

    public void saveConfig() {
        JsonObject json = new JsonObject();
        JsonArray arrayPlayers = new JsonArray();
        for (String s : ignoredPlayers) {
            arrayPlayers.add(s);
        }
        JsonArray arrayKeywords = new JsonArray();
        for (String s : ignoredKeywords) {
            arrayKeywords.add(s);
        }
        JsonArray arrayPatterns = new JsonArray();
        for (String s : ignoredPatterns) {
            arrayPatterns.add(s);
        }
        JsonObject chatObject = new JsonObject();
        for (String s : chatReplacement.keySet()) {
            chatObject.addProperty(s, chatReplacement.get(s));
        }
        JsonObject chatSettings = new JsonObject();
        chatSettings.addProperty("pattern", chatPattern);
        chatSettings.addProperty("new-format", chatReplaceFormat);
        chatSettings.addProperty("prefix-group", prefixGroup);
        chatSettings.addProperty("player-group", nameGroup);
        chatSettings.addProperty("suffix-group", suffixGroup);
        JsonObject suffixObject = new JsonObject();
        for (String s : suffixes.keySet()) {
            suffixObject.addProperty(s, suffixes.get(s));
        }
        chatSettings.add("suffixes", suffixObject);
        json.add("chat-format", chatSettings);
        json.add("ignored-players", arrayPlayers);
        json.add("ignored-keywords", arrayKeywords);
        json.add("ignored-patterns", arrayPatterns);
        json.add("chat-replacement", chatObject);
        Utils.saveFromString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
