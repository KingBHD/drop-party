package me.kingbhd.dropparty;

import me.kingbhd.dropparty.managers.ContainerManager;
import me.kingbhd.dropparty.managers.MessagesManager;
import me.kingbhd.dropparty.menu.gui.AdminMenu;
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
    public DropParty plugin;
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
                    new AdminMenu(target).open((Player) sender);
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
                if (dropCountdown != null) {
                    BukkitRunnable dropRunner = dropCountdown.getDropRunner();
                    if (dropRunner != null && dropCountdown.isRunning()) {
                        Bukkit.broadcastMessage(ChatColor.RED + "DropParty has been stopped by " + ChatColor.BOLD + "admin");
                        dropRunner.cancel();
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.GRAY + "DropParty aren't active at the moment.");
                return true;
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


        dropCountdown = new DropCountdown(plugin);
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
