package me.kingbhd.dropparty;

import me.kingbhd.dropparty.database.DropsDatabase;
import me.kingbhd.dropparty.listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

public final class DropParty extends JavaPlugin {
    private static DropsDatabase database;
    private final PluginDescriptionFile pdfFile = getDescription();
    public String prefix = ChatColor.RED + "" + ChatColor.BOLD + '[' + ChatColor.YELLOW + ChatColor.BOLD + "DropParty" + ChatColor.RED + ChatColor.BOLD + ']';
    public String version = pdfFile.getVersion();
    private Logger logger;

    public static DropsDatabase getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        this.logger = LoggerFactory.getLogger(DropParty.class);

        registerCommands();
        registerEvents();

        saveDefaultConfig();
        registerDatabase();

        this.logger.info(this.prefix + " has been enabled! &fVersion: &l" + version);
        Bukkit.getConsoleSender().sendMessage(prefix + " &eHas been enabled! &fVersion: " + version);
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch (SQLException sqlException) {
            this.logger.error(sqlException.toString());
        }
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

            database = new DropsDatabase(getDataFolder().getAbsolutePath() + "/dropparty.db");
        } catch (SQLException exception) {
            this.logger.error(exception.toString());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

}
