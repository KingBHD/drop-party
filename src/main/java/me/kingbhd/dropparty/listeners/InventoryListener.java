package me.kingbhd.dropparty.listeners;

import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.gui.AdminGUI;
import me.kingbhd.dropparty.gui.PlayerGUI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InventoryListener implements Listener {

    protected DropParty plugin;

    public InventoryListener(DropParty plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();

        // Player GUI Handler
        if (inventoryHolder instanceof PlayerGUI) {
            ((PlayerGUI) inventoryHolder).onClose(event);
        }
        // Todo (Future): AdminGUI save onClose event
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();

        if (inventoryHolder instanceof AdminGUI && event.getClickedInventory() != null) {
            if (event.getCurrentItem() == null) return;
//            if (event.getSlot() > 44) event.setCancelled(true); # To cancel any interaction with the last row
//            if (event.getClickedInventory().getType() == InventoryType.PLAYER) event.setCancelled(true);

            event.setCancelled(true);
            ((AdminGUI) inventoryHolder).onClick(event);
        }
    }

    @EventHandler
    private void onEntityPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!e.getItem().getItemStack().hasItemMeta()) return;

            ItemMeta itemMeta = e.getItem().getItemStack().getItemMeta();

            if (itemMeta == null) return;
            if (!itemMeta.hasLore()) return;

            itemMeta.getPersistentDataContainer().remove(new NamespacedKey(plugin, "pk"));
            itemMeta.getPersistentDataContainer().remove(new NamespacedKey(plugin, "donor"));
            List<String> lore = itemMeta.getLore();
            if (lore != null && !lore.isEmpty()) {
                lore.removeIf(s -> s.contains("dp-dont-stack"));
                lore.removeIf(s -> s.contains("Donor:"));
                itemMeta.setLore(lore);
                e.getItem().getItemStack().setItemMeta(itemMeta);
            }
        }
    }
}
