package me.kingbhd.dropparty;

import me.kingbhd.dropparty.database.DropsDatabase;
import me.kingbhd.dropparty.managers.ContainerManager;
import me.kingbhd.dropparty.managers.MessagesManager;
import me.kingbhd.dropparty.menu.gui.AdminMenu;
import me.kingbhd.dropparty.tasks.DropCountdown;
import me.kingbhd.dropparty.tasks.DropRunner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    public DropParty plugin;
    protected DropRunner dropRunner;
    protected DropCountdown dropCountdown;

    public MainCommand(DropParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        if (args.length >= 1) {
            if (!sender.hasPermission("dropparty.admin")) {
                sender.sendMessage(MessagesManager.getColoredMessage("&7You don't have permission to use this command!"));
                return false;
            }

            if (args[0].equalsIgnoreCase("help")) {
                help(sender);
            } else if (args[0].equalsIgnoreCase("show")) {
                if (args.length != 2) {
                    sender.sendMessage(MessagesManager.getColoredMessage("&7Please provide a player to display their DP contents!"));
                    return false;
                }
                String playerName = args[1];

                Player target = Bukkit.getServer().getPlayer(playerName);
                if (target != null) {
//                    ContainerManager cm = new ContainerManager(target);
//                    cm.adminInventory();
                    new AdminMenu(target).open();
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                Player player = (Player) sender;
                Location playerLocation = player.getLocation();

                plugin.getConfig().set("location", playerLocation);
                plugin.saveConfig();
                sender.sendMessage(MessagesManager.getColoredMessage("&aLocation has been set to &2&l&nX:" + playerLocation.getBlockX() + " Y:" + playerLocation.getBlockY() + " Z:" + playerLocation.getBlockZ()));
            } else if (args[0].equalsIgnoreCase("start")) {
                startDrop(sender);
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (dropRunner != null && dropRunner.isCountdownRunning()) {
                    Bukkit.broadcastMessage(ChatColor.RED + "DropParty has been stopped by " + ChatColor.BOLD + "admin");
                    dropRunner.cancel();
                } else {
                    sender.sendMessage(ChatColor.GRAY + "DropParty aren't active at the moment.");
                }
                return false;
            }
        } else {
            Player player = (Player) sender;

            if (!player.hasPermission("dropparty.player")) {
                sender.sendMessage(MessagesManager.getColoredMessage("&7You don't have permission to use this command!"));
                return false;
            }

            ContainerManager cm = new ContainerManager(player);
            cm.openInventory();
        }
        return true;
    }

    // Command Handlers
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

    public void startDrop(CommandSender sender) {
        FileConfiguration config = plugin.getConfig();
        DropsDatabase database = DropParty.getDatabase();

        List<String> playerUuids = database.getPlayerUuids();
        Collections.shuffle(playerUuids);

        int timer = config.getInt("dropparty.timer");

        dropCountdown = new DropCountdown(timer, playerUuids);
        dropCountdown.runTaskTimer(plugin, 0, 20L);


//        List<ItemStack> playerItems = database.getPlayerItemsByUuid();

//        Location location = config.getLocation("location");
//        dropRunner = new DropRunner();
//        dropRunner.setDropLocation(location);
//        dropRunner.setToBeDropped(toBeDropped);
//        dropRunner.runTaskTimer(plugin, 20L, 40L);
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
}
