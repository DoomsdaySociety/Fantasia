package top.mrxiaom.fantasia.config;

import java.io.File;

public abstract class AbstractConfig {
    public final File configFile;

    public AbstractConfig(File configFile) {
        this.configFile = configFile;
    }

    public abstract void loadDefaultConfig();

    public abstract void reloadConfig();

    public abstract void saveConfig();
}
