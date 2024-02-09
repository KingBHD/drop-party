package me.kingbhd.dropparty.gui;

import me.kingbhd.dropparty.DropParty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerGUI implements InventoryHolder {
    private static final String getGUITitle = ChatColor.translateAlternateColorCodes('&', "&7DropParty");
    private static final Integer getGUISlots = 54;
    protected final DropParty plugin;
    protected final Player player;
    protected Inventory inventory;

    public PlayerGUI(DropParty plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void onClose(InventoryCloseEvent event) {
        List<ItemStack> itemStackList = new ArrayList<>();
        Arrays.stream(event.getInventory().getContents()).filter(Objects::nonNull).forEach(itemStackList::add);
        if (itemStackList.isEmpty()) return;

        this.plugin.getDatabase().addPlayer((Player) event.getPlayer(), itemStackList);
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this, getGUISlots, getGUITitle);
        this.player.openInventory(this.inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
