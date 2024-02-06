package me.kingbhd.dropparty.menu.gui;

import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.database.DropsDatabase;
import me.kingbhd.dropparty.menu.PaginatedMenuHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AdminMenu extends PaginatedMenuHolder {

    public AdminMenu(Player player) {
        super.player = player;
    }

    @Override
    public String getMenuName() {
        return ChatColor.RED + "" + ChatColor.BOLD + '[' + ChatColor.YELLOW + ChatColor.BOLD + "DropParty" + ChatColor.RED + ChatColor.BOLD + ']';
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        DropsDatabase database = DropParty.getDatabase();
        List<ItemStack> playerItems = database.getPlayerItemsByUuid(player.getUniqueId().toString());

        if (e.getCurrentItem().getType().equals(Material.MAP)) {
            //close inventory
            p.closeInventory();

        } else if (e.getCurrentItem().getType().equals(Material.ARROW)) {
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Previous")) {
                if (page != 0) {
                    page = page - 1;
                    super.open();
                }
            } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Next")) {
                if (!((index + 1) >= playerItems.size())) {
                    page = page + 1;
                    super.open();
                }
            }
        }
    }

    @Override
    public void setMenuItems(Player player) {

        addMenuBorder();

        DropsDatabase database = DropParty.getDatabase();
        List<ItemStack> playerItems = database.getPlayerItemsByUuid(player.getUniqueId().toString());

        if (playerItems != null && !playerItems.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= playerItems.size()) break;
                if (playerItems.get(index) != null) {

                    inventory.addItem(playerItems.get(index));

                }
            }
        }
    }

}
