package me.kingbhd.dropparty.gui;

import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.managers.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdminGUI implements InventoryHolder {
    protected static final int perPage = 45;
    private static final Integer getGUISlots = 54;
    private final String getGUITitle;
    protected final DropParty plugin;
    protected final Player player;
    protected final Player target;
    // Paginated
    protected int page = 0;
    protected int index = 0;
    protected Inventory inventory;
    protected List<ItemStack> stacks;
    protected ItemStack FILLER_GLASS = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");

    public AdminGUI(DropParty plugin, Player player, Player target) {
        this.plugin = plugin;
        this.player = player;
        this.target = target;
        this.getGUITitle = MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.dropparty-gui-admin"));
    }

    public void setStacks(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void onClose(InventoryCloseEvent event) {
        List<ItemStack> itemStackList = new ArrayList<>();
        Arrays.stream(event.getInventory().getContents()).filter(Objects::nonNull).filter(i -> {
            ItemMeta im = i.getItemMeta();
            return im != null && im.getDisplayName().isEmpty();
        }).forEach(itemStackList::add);

        List<ItemStack> removedItems = getCurrentPageItems().stream().filter(itemStack -> !itemStackList.contains(itemStack)).collect(Collectors.toList());
        System.out.println(removedItems + " Removed Items!");
    }

    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem.getType() == Material.ARROW) {
            if (clickedItem.getItemMeta() == null) return;
            String escapedItemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (escapedItemName.equalsIgnoreCase("Previous")) {
                if (this.page != 0) {
                    this.page = this.page - 1;
                    this.open();
                }
            } else if (escapedItemName.equalsIgnoreCase("Next")) {
                if (!((index + 1) >= this.stacks.size())) {
                    this.page = this.page + 1;
                    this.open();
                }
            }
        }
    }

    public void open() {
        String playerName;
        if (this.target != null) {
            playerName = this.target.getDisplayName();
        } else {
            playerName = "Global";
        }
        inventory = Bukkit.createInventory(this, getGUISlots, getGUITitle.replace("%player%", playerName));
        this.setMenuItems();
        this.player.openInventory(inventory);
    }

    public ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

    public List<ItemStack> getCurrentPageItems() {
        List<ItemStack> itemsToBePlaced = new ArrayList<>();
        for (int i = 0; i < perPage; i++) {
            index = perPage * page + i;
            if (index >= this.stacks.size()) break;

            if (this.stacks.get(index) != null) {
                itemsToBePlaced.add(this.stacks.get(index));
            }
        }
        return itemsToBePlaced;
    }

    public void setMenuItems() {
        inventory.setItem(45, FILLER_GLASS);
        inventory.setItem(46, FILLER_GLASS);
        inventory.setItem(47, makeItem(Material.ARROW, ChatColor.GREEN + "Previous"));
        inventory.setItem(48, FILLER_GLASS);
        inventory.setItem(49, FILLER_GLASS);
        inventory.setItem(50, FILLER_GLASS);
        inventory.setItem(51, makeItem(Material.ARROW, ChatColor.GREEN + "Next"));
        inventory.setItem(52, FILLER_GLASS);
        inventory.setItem(53, FILLER_GLASS);

        this.setStacks(this.plugin.getDatabase().getStacks(this.target));
        if (this.stacks != null && !this.stacks.isEmpty()) {
            getCurrentPageItems().forEach(this.inventory::addItem);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
