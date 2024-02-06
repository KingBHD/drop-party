package me.kingbhd.dropparty.managers;

import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.database.DropsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ContainerManager implements InventoryHolder {

    protected static String title = ChatColor.RED + "" + ChatColor.BOLD + '[' + ChatColor.YELLOW + ChatColor.BOLD + "DropParty" + ChatColor.RED + ChatColor.BOLD + ']';
    protected final Player player;
    protected Inventory inventory;

    public ContainerManager(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void saveToDatabase(List<ItemStack> itemStacks) {
        DropsDatabase database = DropParty.getDatabase();

        database.savePlayerItems(this.player, itemStacks);
    }

    public void openInventory() {
        this.inventory = Bukkit.createInventory(this, 54, title);
        player.openInventory(inventory);
    }

    public void adminInventory() {
        this.inventory = Bukkit.createInventory(player, 54, title + " " + ChatColor.RESET + ChatColor.DARK_GREEN + "[Admin]");

        DropsDatabase database = DropParty.getDatabase();
        List<ItemStack> playerItems = database.getPlayerItemsByUuid(player.getUniqueId().toString());
        if (playerItems != null && !playerItems.isEmpty()) playerItems.forEach(this.inventory::addItem);

        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

}
