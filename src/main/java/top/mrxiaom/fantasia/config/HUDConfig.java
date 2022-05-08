package top.mrxiaom.fantasia.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.mrxiaom.fantasia.Utils;

import java.io.File;
import java.io.FileNotFoundException;

public class HUDConfig extends AbstractConfig {
    Logger logger = LogManager.getLogger("Fantasia-HUD");

    public boolean isHideMeFromScoreboard;
    public boolean isHideScoreboardNumber;
    public boolean useFancyHUD;
    public boolean isHideMeFromHUD;
    public boolean isShowFrameItem;

    public HUDConfig(File configFile) {
        super(configFile);
    }

    @Override
    public void loadDefaultConfig() {
        isHideScoreboardNumber = true;
        isHideMeFromScoreboard = true;
        useFancyHUD = true;
        isShowFrameItem = true;
        isHideMeFromHUD = false;
    }

    public void reloadConfig() {
        try {
            if (!configFile.exists()) throw new FileNotFoundException("找不到配置文件");
            JsonElement jsonElement = new JsonParser().parse(Utils.readAsString(configFile));

            JsonObject json = jsonElement.getAsJsonObject();

            loadDefaultConfig();
            if(json.has("is-hide-scoreboard-number")) isHideScoreboardNumber = json.get("is-hide-scoreboard-number").getAsBoolean();
            if(json.has("is-hide-me-from-scoreboard")) isHideMeFromScoreboard = json.get("is-hide-me-from-scoreboard").getAsBoolean();
            if(json.has("use-fancy-hud")) useFancyHUD = json.get("use-fancy-hud").getAsBoolean();
            if(json.has("is-show-frame-item")) isShowFrameItem = json.get("is-show-frame-item").getAsBoolean();
            if(json.has("is-hide-me-from-hud")) isHideMeFromHUD = json.get("is-hide-me-from-hud").getAsBoolean();
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

    @Override
    public void saveConfig() {
        JsonObject json = new JsonObject();
        json.addProperty("is-hide-scoreboard-number", isHideScoreboardNumber);
        json.addProperty("is-hide-me-from-scoreboard", isHideMeFromScoreboard);
        json.addProperty("use-fancy-hud", useFancyHUD);
        json.addProperty("is-show-frame-item", isShowFrameItem);
        json.addProperty("is-hide-me-from-hud", isHideMeFromHUD);
        Utils.saveFromString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
