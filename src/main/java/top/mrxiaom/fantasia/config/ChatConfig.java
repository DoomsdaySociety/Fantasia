package top.mrxiaom.fantasia.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatConfig extends AbstractConfig {
    /*
     * 采样(&应替换为§): &7[&b&l‣&是&7] &f<&7[&6开荒者&7]&eLittleCatX&7[&6&l✓&7]&f> &fa.a
     */
    @Config("chat-format.pattern")
    public String chatPattern = "§7\\[(.*)§7] §f<(.*)§.([A-Za-z0-9_]+)(.*)?§f> §?f?(.*)";
    @Config("chat-format.tag-group")
    public int tagGroup = 1;
    @Config("chat-format.prefix-group")
    public int prefixGroup = 2;
    @Config("chat-format.name-group")
    public int nameGroup = 3;
    @Config("chat-format.suffix-group")
    public int suffixGroup = 4;
    @Config("chat-format.suffixes")
    public Map<String, String> suffixes = defaultSuffixes();
    @Config("chat-format.new-format")
    public String chatReplaceFormat = "$prefix§e$player$suffix §7>> §f";
    @Config("chat-format.special-formats")
    public Map<String, String> specialFormats = defaultSpecialFormats();
    @Config("chat-format.enable-text-layout-engine")
    public boolean enableTextLayoutEngine = true;

    @Config("chat-replacement")
    public Map<String, String> chatReplacement = new HashMap<>();
    @Config("ignorePlayers")
    public List<String> ignoredPlayers = new ArrayList<>();
    @Config("ignored-keywords")
    public List<String> ignoredKeywords = new ArrayList<>();
    @Config("ignored-patterns")
    public List<String> ignoredPatterns = new ArrayList<>();

    public Pattern compiledChatPattern;
    public ChatConfig(File configFile) {
        super(configFile);
    }

    public void reloadConfig() {
        super.reloadConfig();
        this.compiledChatPattern = Pattern.compile(chatPattern);
    }

    private static Map<String, String> defaultSuffixes() {
        Map<String, String> suffixes = new HashMap<>();
        suffixes.put("none", "§7§l✓");
        suffixes.put("vip", "§b§l✓");
        suffixes.put("svip", "§6§l✓");
        suffixes.put("mvp", "§c§l✓");
        return suffixes;
    }

    private static Map<String, String> defaultSpecialFormats() {
        Map<String, String> specialFormats = new HashMap<>();
        specialFormats.put("TRepeater1", "§7[§2复读机§7] §a");
        return specialFormats;
    }
}
