package top.mrxiaom.fantasia.config;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.mrxiaom.fantasia.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatConfig extends AbstractConfig {
    Logger logger = LogManager.getLogger("Fantasia-Chat");

    public List<String> ignoredPlayers = new ArrayList<>();
    public String chatPattern;
    public int tagGroup = 1;
    public int prefixGroup = 2;
    public int nameGroup = 3;
    public int suffixGroup = 4;
    public Map<String, String> suffixes = new HashMap<>();
    public Pattern compiledChatPattern;
    public String chatReplaceFormat;
    public Map<String, String> chatReplacement = new HashMap<>();
    public Map<String, String> specialFormats = new HashMap<>();
    public List<String> ignoredKeywords = new ArrayList<>();
    public List<String> ignoredPatterns = new ArrayList<>();

    public ChatConfig(File configFile) {
        super(configFile);
    }

    public void loadDefaultConfig() {
        ignoredPlayers.clear();
        ignoredKeywords.clear();
        ignoredPatterns.clear();
        chatReplacement.clear();
        suffixes.clear();
        specialFormats.clear();
        /*
            采样(&应替换为§): &7[&b&l‣&是&7] &f<&7[&6开荒者&7]&eLittleCatX&7[&6&l✓&7]&f> &fa.a
        */
        chatPattern = "§7\\[(.*)§7] §f<(.*)§.([A-Za-z0-9_]+)(.*)?§f> §?f?(.*)";
        compiledChatPattern = Pattern.compile(chatPattern);
        chatReplaceFormat = "$prefix§e$player$suffix §7>> §f";
        tagGroup = 1;
        prefixGroup = 2;
        nameGroup = 3;
        suffixGroup = 4;

        specialFormats.put("TRepeater1", "§7[§2复读机§7] §a");

        suffixes.put("none", "§7§l✓");
        suffixes.put("vip", "§b§l✓");
        suffixes.put("svip", "§6§l✓");
        suffixes.put("mvp", "§c§l✓");
    }

    public void reloadConfig() {
        try {
            if (!configFile.exists()) throw new FileNotFoundException("找不到配置文件");
            JsonElement jsonElement = new JsonParser().parse(Utils.readAsString(configFile));

            JsonObject json = jsonElement.getAsJsonObject();

            loadDefaultConfig();

            if(json.has("chat-format")) {
                JsonObject chatSettings = json.get("chat-format").getAsJsonObject();
                if(chatSettings.has("pattern")) chatPattern = chatSettings.get("pattern").getAsString();
                compiledChatPattern = Pattern.compile(chatPattern);
                if(chatSettings.has("new-format")) chatReplaceFormat = chatSettings.get("new-format").getAsString().replace("&", "§");
                if(chatSettings.has("tag-group")) tagGroup = chatSettings.get("tag-group").getAsInt();
                if(chatSettings.has("prefix-group"))prefixGroup = chatSettings.get("prefix-group").getAsInt();
                if(chatSettings.has("player-group"))nameGroup = chatSettings.get("player-group").getAsInt();
                if(chatSettings.has("suffix-group"))suffixGroup = chatSettings.get("suffix-group").getAsInt();
                if(chatSettings.has("suffixes")) {
                    JsonObject suffixObject = chatSettings.get("suffixes").getAsJsonObject();
                    suffixes.clear();
                    for (Map.Entry<String, JsonElement> entry : suffixObject.entrySet()) {
                        suffixes.put(entry.getKey(), entry.getValue().getAsString().replace("&", "§"));
                    }
                }
                if(chatSettings.has("special-formats")) {
                    JsonObject specialFormatsObject = chatSettings.get("special-formats").getAsJsonObject();
                    specialFormats.clear();
                    for (Map.Entry<String, JsonElement> entry : specialFormatsObject.entrySet()) {
                        specialFormats.put(entry.getKey(), entry.getValue().getAsString().replace("&", "§"));
                    }
                }
            }

            if (json.has("ignored-players")) {
                JsonArray players = json.get("ignored-players").getAsJsonArray();
                ignoredPlayers.clear();
                for (int i = 0; i < players.size(); i++) {
                    ignoredPlayers.add(players.get(i).getAsString());
                }
            }
            if(json.has("ignored-keywords")) {
                JsonArray keywords = json.get("ignored-keywords").getAsJsonArray();
                ignoredKeywords.clear();
                for (int i = 0; i < keywords.size(); i++) {
                    ignoredKeywords.add(keywords.get(i).getAsString());
                }
            }
            if (json.has("ignored-patterns")) {
                JsonArray patterns = json.get("ignored-patterns").getAsJsonArray();
                ignoredPatterns.clear();
                for (int i = 0; i < patterns.size(); i++) {
                    ignoredPatterns.add(patterns.get(i).getAsString());
                }
            }
            if (json.has("chat-replacement")) {
                JsonObject chatObject = json.get("chat-replacement").getAsJsonObject();
                chatReplacement.clear();
                for (Map.Entry<String, JsonElement> entry : chatObject.entrySet()) {
                    chatReplacement.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
            saveConfig();
        } catch (Throwable t) {
            logger.warn("无法加载配置文件", t);
            if (configFile.exists()) {
                configFile.renameTo(new File(configFile.getParentFile(), configFile.getName() + ".old"));
            }
            loadDefaultConfig();
            saveConfig();
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
        chatSettings.addProperty("tag-group", tagGroup);
        chatSettings.addProperty("prefix-group", prefixGroup);
        chatSettings.addProperty("player-group", nameGroup);
        chatSettings.addProperty("suffix-group", suffixGroup);
        JsonObject suffixObject = new JsonObject();
        for (String s : suffixes.keySet()) {
            suffixObject.addProperty(s, suffixes.get(s));
        }
        JsonObject specialFormatsObject = new JsonObject();
        for (String s : specialFormats.keySet()) {
            specialFormatsObject.addProperty(s, specialFormats.get(s));
        }
        chatSettings.add("suffixes", suffixObject);
        chatSettings.add("special-formats", specialFormatsObject);
        json.add("chat-format", chatSettings);
        json.add("ignored-players", arrayPlayers);
        json.add("ignored-keywords", arrayKeywords);
        json.add("ignored-patterns", arrayPatterns);
        json.add("chat-replacement", chatObject);
        Utils.saveFromString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
