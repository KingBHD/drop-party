package me.kingbhd.dropparty.gui;

import me.kingbhd.dropparty.DropParty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class AdminGUI implements InventoryHolder {
    protected static final int perPage = 47;
    private static final String getGUITitle = ChatColor.translateAlternateColorCodes('&', "&7DropParty");
    private static final Integer getGUISlots = 54;
    protected final DropParty plugin;
    protected final Player player;
    // Paginated
    protected int page = 0;
    protected int index = 0;
    protected Inventory inventory;
    protected List<ItemStack> stacks;
    protected ItemStack FILLER_GLASS = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");

    public AdminGUI(DropParty plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void setStacks(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void onClose(InventoryClickEvent event) {
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

                if (escapedItemName.equalsIgnoreCase("previous")) {
                    if (this.page != 0) {
                        this.page = this.page - 1;
                        this.open(null);
                    }
                } else if (escapedItemName.equalsIgnoreCase("next")) {
                    if (this.page != 0) {
                        this.page = this.page + 1;
                        this.open(null);
                    }
                }
                break;
        }
    }

    public void open(Player target) {
        inventory = Bukkit.createInventory(this, getGUISlots, getGUITitle);
        this.setMenuItems(target);
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

    public void setMenuItems(Player player) {
        inventory.setItem(45, FILLER_GLASS);
        inventory.setItem(46, FILLER_GLASS);
        inventory.setItem(47, makeItem(Material.ARROW, ChatColor.GREEN + "Previous"));
        inventory.setItem(48, FILLER_GLASS);
        inventory.setItem(49, FILLER_GLASS);
//        inventory.setItem(49, makeItem(Material.MAP, ChatColor.DARK_RED + "Close"));
        inventory.setItem(50, FILLER_GLASS);
        inventory.setItem(51, makeItem(Material.ARROW, ChatColor.GREEN + "Next"));
        inventory.setItem(52, FILLER_GLASS);
        inventory.setItem(53, FILLER_GLASS);

        List<ItemStack> stacks;
        if (player != null) {
            stacks = this.plugin.getDatabase().getStacksByPlayer(player);
            this.setStacks(stacks);
        } else {
            if (this.stacks == null || this.stacks.isEmpty()) {
                stacks = this.plugin.getDatabase().getStacks();
                this.setStacks(stacks);
            }
        }

        if (this.stacks != null && !this.stacks.isEmpty()) {
            for (int i = 0; i < perPage; i++) {
                index = perPage * page + i;
                if (index >= this.stacks.size()) break;

                if (this.stacks.get(index) != null) {
                    inventory.addItem(this.stacks.get(index));
                }
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
