package me.kingbhd.dropparty.tasks;

import com.j256.ormlite.stmt.query.In;
import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.database.DropsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DropCountdown extends BukkitRunnable {

    protected DropParty plugin;
    private int countdown;

    public DropCountdown(DropParty plugin) {
        this.plugin = plugin;
        this.countdown = this.plugin.getConfig().getInt("countdown");
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

        plugin.getRunner().start();
    }
}
