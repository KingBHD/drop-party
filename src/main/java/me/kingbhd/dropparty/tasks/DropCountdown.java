package me.kingbhd.dropparty.tasks;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DropCountdown extends BukkitRunnable {

    private final List<String> playerUuid;
    private int countdown;
//    protected DropRunner dropRunner;

    public DropCountdown(int countdown, List<String> playerUuid) {
        this.countdown = countdown;
        this.playerUuid = playerUuid;

//        this.dropRunner = new DropRunner();
//        this.dropRunner.setToBeDropped();
    }

    @Override
    public void run() {
        if (--this.countdown == 0) {
            this.cancel();

            // THE EVENT
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "DropParty", ChatColor.GRAY + "Going to start in " + ChatColor.RED + "%time%".replace("%time%", String.valueOf(this.countdown + 1)));
        }
    }
}
