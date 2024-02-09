package me.kingbhd.dropparty.listeners;

import me.kingbhd.dropparty.gui.AdminGUI;
import me.kingbhd.dropparty.gui.PlayerGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InventoryListener implements Listener {

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder iHolder = event.getInventory().getHolder();

        // Player GUI Handler
        if (iHolder instanceof PlayerGUI) {
            ((PlayerGUI) iHolder).onClose(event);
        }
        // Admin GUI Close Handler
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder iHolder = inventory.getHolder();

        if (iHolder instanceof AdminGUI && event.getClickedInventory() != null) {
            if (event.getSlot() > 44) event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            ((AdminGUI) iHolder).onClose(event);
        }
    }

    @EventHandler
    private void onEntityPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!e.getItem().getItemStack().hasItemMeta()) return;

            ItemMeta itemMeta = e.getItem().getItemStack().getItemMeta();

            if (itemMeta == null) return;
            if (!itemMeta.hasLore()) return;

            List<String> lore = itemMeta.getLore();
            if (lore != null && !lore.isEmpty()) {
                lore.removeIf(s -> s.contains("dp-dont-stack"));
                lore.removeIf(s -> s.contains("DP Donation:"));
                itemMeta.setLore(lore);
                e.getItem().getItemStack().setItemMeta(itemMeta);
            }
        }
    }
}
