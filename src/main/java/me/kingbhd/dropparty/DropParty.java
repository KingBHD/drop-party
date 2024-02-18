package me.kingbhd.dropparty;

import me.kingbhd.dropparty.database.DropsDatabase;
import me.kingbhd.dropparty.listeners.InventoryListener;
import me.kingbhd.dropparty.managers.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.Objects;

public final class DropParty extends JavaPlugin {
    private final PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();

    private DropsDatabase database;

    public DropsDatabase getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerEvents();

        saveDefaultConfig();
        registerDatabase();

        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&7(&b/Dropparty&7) has been enabled! &cVersion: " + version));
    }

    // Managers
    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("dropparty")).setExecutor(new MainCommand(this));
    }

    public void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InventoryListener(this), this);
    }

    public void registerDatabase() {
        try {
            if (!getDataFolder().exists()) {
                boolean ignored = getDataFolder().mkdirs();
            }

            database = new DropsDatabase(getDataFolder().getAbsolutePath() + "/dropparty.db", this);
        } catch (SQLException exception) {
            Logger.error(exception, "[DropParty] Failed to create connection.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

}
