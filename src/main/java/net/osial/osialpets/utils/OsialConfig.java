package net.osial.osialpets.utils;

import lombok.Getter;
import net.osial.osialpets.OsialPets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class OsialConfig {

    /*
    Used for the storage of playerdata in .yml files.
    Uses Bukkit's YamlConfiguration API.
     */

    @Getter private YamlConfiguration config;
    @Getter private File confFile;

    public OsialConfig(String name) {
        OsialPets plugin = JavaPlugin.getPlugin(OsialPets.class);
        File pluginFolder = plugin.getDataFolder();
        File configFile = new File(pluginFolder, name + ".yml");
        confFile = configFile;
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try {
                configFile.createNewFile();
            } catch (Exception e) {}
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (Exception e) {
            String consoleRed = "\u001B[31m";
            String consoleReset = "\u001B[0m";
            Bukkit.getLogger().severe(consoleRed + "Error loading " + name + ".yml" + consoleReset);
        }
    }

    public void save() {
        try {
            config.save(confFile);
        } catch (Exception e) {
            String consoleRed = "\u001B[31m";
            String consoleReset = "\u001B[0m";
            Bukkit.getLogger().severe(consoleRed + "Error saving " + confFile.getName() + consoleReset);
        }
    }

}
