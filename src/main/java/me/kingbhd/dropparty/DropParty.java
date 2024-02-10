package me.kingbhd.dropparty;

import me.kingbhd.dropparty.database.DropsDatabase;
import me.kingbhd.dropparty.listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.Objects;

public final class DropParty extends JavaPlugin {
    private final PluginDescriptionFile pdfFile = getDescription();
    public String prefix = ChatColor.RED + "" + ChatColor.BOLD + '[' + ChatColor.YELLOW + ChatColor.BOLD + "DropParty" + ChatColor.RED + ChatColor.BOLD + ']';
    public String version = pdfFile.getVersion();
    private DropsDatabase dropsDatabase;

    public DropsDatabase getDatabase() {
        return dropsDatabase;
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerEvents();

        saveDefaultConfig();
        registerDatabase();

        Logger.info(this.prefix + " has been enabled! &fVersion: &l" + version);
        Bukkit.getConsoleSender().sendMessage(prefix + " &eHas been enabled! &fVersion: " + version);
    }

    // Managers
    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("dropparty")).setExecutor(new MainCommand(this));
    }

    public void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
    }

    public void registerDatabase() {
        try {
            if (!getDataFolder().exists()) {
                boolean ignored = getDataFolder().mkdirs();
            }

            dropsDatabase = new DropsDatabase(getDataFolder().getAbsolutePath() + "/dropparty.db", this);
        } catch (SQLException exception) {
            Logger.error(exception, "[DropParty] Failed to create connection.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

}
