package me.kingbhd.dropparty.gui;

import me.kingbhd.dropparty.DropParty;
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
    private static final String getGUITitle = ChatColor.translateAlternateColorCodes('&', "&7DropParty");
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
    }

    public void setStacks(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void onClose(InventoryCloseEvent event) {
//        System.out.println("Displayed: " + getCurrentPageItems() + " Player:" + this.target);

        List<ItemStack> itemStackList = new ArrayList<>();
        Arrays.stream(event.getInventory().getContents()).filter(Objects::nonNull).filter(i -> {
            ItemMeta im = i.getItemMeta();
            return im != null && im.getDisplayName().isEmpty();
        }).forEach(itemStackList::add);

//        System.out.println("AfterClosed: " + itemStackList);

        List<ItemStack> removedItems = getCurrentPageItems().stream().filter(itemStack -> !itemStackList.contains(itemStack)).collect(Collectors.toList());
        System.out.println(removedItems + " Removed Items!");
    }

    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        switch (clickedItem.getType()) {
            case MAP:
                // Todo: Close Inventory?
                break;
            case ARROW:
                if (clickedItem.getItemMeta() == null) break;
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
                break;
        }
    }

    public void open() {
        inventory = Bukkit.createInventory(this, getGUISlots, getGUITitle);
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

        if (this.target != null) {
            this.setStacks(this.plugin.getDatabase().getStacksByPlayer(this.target));
        } else {
            this.setStacks(this.plugin.getDatabase().getStacks());
        }

        if (this.stacks != null && !this.stacks.isEmpty()) {
            getCurrentPageItems().forEach(this.inventory::addItem);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
