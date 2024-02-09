package me.kingbhd.dropparty.tasks;

import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.database.DropsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class DropCountdown extends BukkitRunnable {

    private final Location location;
    private final List<Integer> playerIds;
    private final boolean isRunning = false;
    protected DropsDatabase database;
    protected DropParty plugin;
    protected BukkitRunnable dropRunner;
    private int countdown;

    public DropCountdown(DropParty plugin) {
        this.plugin = plugin;
        this.database = DropParty.getDatabase();

        this.countdown = this.plugin.getConfig().getInt("dropparty.timer");
        this.location = this.plugin.getConfig().getLocation("location");

        this.playerIds = database.getPlayerIds();
        Collections.shuffle(playerIds);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public BukkitRunnable getDropRunner() {
        return dropRunner;
    }

    protected void broadcastToPlayers(String title, String subTitle) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subTitle);
        }
    }

    @Override
    public void run() {

        if (--this.countdown != 0) {
            this.broadcastToPlayers(
                    ChatColor.RED + "" + ChatColor.BOLD + "DropParty",
                    ChatColor.GRAY + "Going to start in " + ChatColor.RED + "%time%".replace("%time%", String.valueOf(this.countdown + 1))
            );
            return;
        }

        this.cancel();
        this.broadcastToPlayers(ChatColor.RED + "" + ChatColor.BOLD + "DropParty", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "has started at /warp dp");

        // THE EVENT
    }
}
