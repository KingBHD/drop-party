package me.kingbhd.dropparty.listeners;

import me.kingbhd.dropparty.managers.ContainerManager;
import me.kingbhd.dropparty.menu.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InventoryListener implements Listener {

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder iHolder = event.getInventory().getHolder();
        if (iHolder instanceof ContainerManager) {
            List<ItemStack> prunedItems = new ArrayList<>();

            Arrays.stream(event.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .forEach(prunedItems::add);

            ((ContainerManager) iHolder).saveToDatabase(prunedItems);
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder iHolder = inventory.getHolder();

        if (event.getClickedInventory() != null) {
            if (iHolder instanceof ContainerManager) {
                if (event.getClickedInventory().getType() == InventoryType.CHEST && !event.getWhoClicked().hasPermission("dropparty.admin")) {
                    event.setCancelled(true);
                }
            } else if (iHolder instanceof MenuHolder) {
                if (event.getSlot() > 44) event.setCancelled(true);
                if (event.getCurrentItem() == null) return;

                ((MenuHolder) iHolder).handleMenu(event);
            }
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
