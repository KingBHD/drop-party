package me.kingbhd.dropparty;

import me.kingbhd.dropparty.gui.AdminGUI;
import me.kingbhd.dropparty.gui.PlayerGUI;
import me.kingbhd.dropparty.managers.MessagesManager;
import me.kingbhd.dropparty.tasks.DropRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    public final DropParty plugin;
    public DropRunnable runnable;


    public MainCommand(DropParty plugin) {
        this.plugin = plugin;
        this.runnable = new DropRunnable(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("help")) {
                help(player);
                return true;
            }

            if (!sender.hasPermission("dropparty.admin")) {
                sender.sendMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.missing-permission-admin")));
                return false;
            }

            if (args[0].equalsIgnoreCase("set")) set(player);
            else if (args[0].equalsIgnoreCase("reset")) reset(player);
            else if (args[0].equalsIgnoreCase("start")) start(player);
            else if (args[0].equalsIgnoreCase("cancel")) cancel(player);
            else if (args[0].equalsIgnoreCase("show")) {
                if (args.length != 2) show(player, null);
                else {
                    String playerUsername = args[1];
                    Player target = Bukkit.getServer().getPlayer(playerUsername);
                    if (target == null) {
                        sender.sendMessage(MessagesManager.getColoredMessage("&7(&b/Dropparty&7) Provided username does not exists."));
                        return true;
                    }
                    show(player, target);
                }
            } else
                sender.sendMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.missing-permission-player")));
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
            commands.add("cancel");
            commands.add("reset");
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
            player.sendMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.missing-permission-player")));
        }

        new PlayerGUI(this.plugin, player).open();
    }

    public void show(Player sender, Player target) {
        new AdminGUI(this.plugin, sender, target).open();
    }

    public void help(Player sender) {
        sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &8[&aDropParty&8] &7] ]"));
        sender.sendMessage(MessagesManager.getColoredMessage(" "));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp &8To participate in drop-party."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp show <username> &8To see what a player has donated."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp cancel &8To cancel the running event."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp reset &8To reset DropParty and to clear the donated items."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp set &8Set location for drop party."));
        sender.sendMessage(MessagesManager.getColoredMessage("&6/dp help &8Shows this message."));
        sender.sendMessage(MessagesManager.getColoredMessage(" "));
        sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &8[&aDropParty&8] &7] ]"));
    }

    public void set(Player sender) {
        Location playerLocation = sender.getLocation();

        plugin.getConfig().set("location", playerLocation);
        plugin.saveConfig();

        sender.sendMessage(MessagesManager.getColoredMessage(
                "&aLocation has been set to &2&l&nX:"
                        + playerLocation.getBlockX() + " Y:"
                        + playerLocation.getBlockY() + " Z:"
                        + playerLocation.getBlockZ()
        ));
    }

    public void start(Player sender) {
        this.runnable.start();
        sender.sendMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.dropparty-start")));
    }

    public void cancel(Player sender) {
        boolean isClosed = this.runnable.stop();
        if (isClosed) {
            Bukkit.broadcastMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.dropparty-cancel-success").replace("%admin%", sender.getDisplayName())));
            this.runnable = new DropRunnable(plugin);
        } else {
            sender.sendMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.dropparty-cancel-failed")));
        }
    }

    public void reset(Player sender) {
        this.runnable = new DropRunnable(plugin);
        this.plugin.getDatabase().resetDrops();
        sender.sendMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.dropparty-reset")));
    }

}
