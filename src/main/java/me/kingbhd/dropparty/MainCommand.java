package me.kingbhd.dropparty;

import me.kingbhd.dropparty.gui.AdminGUI;
import me.kingbhd.dropparty.gui.PlayerGUI;
import me.kingbhd.dropparty.managers.MessagesManager;
import me.kingbhd.dropparty.tasks.DropCountdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    public final DropParty plugin;

    protected DropCountdown dropCountdown;

    public MainCommand(DropParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (args.length >= 1) {
            if (!sender.hasPermission("dropparty.admin")) {
                sender.sendMessage(MessagesManager.getColoredMessage("&7You don't have permission to use this command!"));
                return false;
            }

            if (args[0].equalsIgnoreCase("help")) help(sender);
            else if (args[0].equalsIgnoreCase("set")) set(player);
            else if (args[0].equalsIgnoreCase("start")) start(sender);
            else if (args[0].equalsIgnoreCase("cancel")) cancel(player);
            else if (args[0].equalsIgnoreCase("show")) {
                if (args.length != 2) show(player, null);
                else {
                    String playerUsername = args[1];
                    Player target = Bukkit.getServer().getPlayer(playerUsername);
                    if (target == null) {
                        sender.sendMessage(MessagesManager.getColoredMessage("&7Provided username does not exists."));
                        return true;
                    }
                    show(player, target);
                }
            } else sender.sendMessage(MessagesManager.getColoredMessage("&7Invalid command."));
        } else open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("dropparty.admin")) return null;

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> commands = new ArrayList<>();
            commands.add("show");
            commands.add("start");
            commands.add("reset");
            commands.add("cancel");
            commands.add("set");
            commands.add("help");
            for (String c : commands) {
                if (args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
                    completions.add(c);
                }
            }
            return completions;
        }

        return null;
    }

    // Command Handlers
    public void open(Player player) {
        if (!player.hasPermission("dropparty.player")) {
            player.sendMessage(MessagesManager.getColoredMessage("&7You don't have permission to use this command!"));
        }

        new PlayerGUI(this.plugin, player).open();
    }

    public void show(Player player, Player target) {
        new AdminGUI(this.plugin, player).open(target);
    }

    public void help(CommandSender sender) {
        sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &8[&aDropParty&8] &7] ]"));
        sender.sendMessage(MessagesManager.getColoredMessage(" "));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp &8To participate in drop-party."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp set &8Set location for drop party."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp show <username> &8To see what a player has donated."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp help &8Shows this message."));
        sender.sendMessage(MessagesManager.getColoredMessage(" "));
        sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &8[&aDropParty&8] &7] ]"));
    }

    public void set(Player player) {
        Location playerLocation = player.getLocation();

        plugin.getConfig().set("location", playerLocation);
        plugin.saveConfig();

        player.sendMessage(MessagesManager.getColoredMessage(
                "&aLocation has been set to &2&l&nX:"
                        + playerLocation.getBlockX() + " Y:"
                        + playerLocation.getBlockY() + " Z:"
                        + playerLocation.getBlockZ()
        ));
    }

    public void start(CommandSender sender) {
        dropCountdown = new DropCountdown(plugin);
        dropCountdown.runTaskTimer(plugin, 0, 20L);
    }

    public void cancel(Player player) {
        if (dropCountdown != null) {
            BukkitRunnable dropRunner = dropCountdown.getDropRunner();
            if (dropRunner != null && dropCountdown.isRunning()) {
                Bukkit.broadcastMessage(ChatColor.RED + "DropParty has been stopped by " + ChatColor.BOLD + "admin");
                dropRunner.cancel();
                return;
            }
        }
        player.sendMessage(ChatColor.GRAY + "DropParty aren't active at the moment.");
    }

}
