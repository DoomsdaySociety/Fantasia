package top.mrxiaom.fantasia.config;

import java.io.File;

public class HUDConfig extends AbstractConfig {
    @Config("is-hide-scoreboard-number")
    public boolean isHideScoreboardNumber = true;
    @Config("use-fancy-hud")
    public boolean useFancyHUD = true;
    @Config("is-hide-me-from-hud")
    public boolean isHideMeFromHUD = true;
    @Config("is-show-frame-item")
    public boolean isShowFrameItem = false;

    public HUDConfig(File configFile) {
        super(configFile);
    }
}
