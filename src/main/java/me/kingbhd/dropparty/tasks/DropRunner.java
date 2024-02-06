package me.kingbhd.dropparty.tasks;

import me.kingbhd.dropparty.DropParty;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropRunner extends BukkitRunnable {

    private Location dropLocation;
    private int totalPlayer;
    private List<String> playerUuids;

    @Override
    public void run() {
        if (totalPlayer == 0) this.cancel();

        String playerUuid = playerUuids.get(totalPlayer);
        ItemStack droppingStack = (ItemStack) DropParty.getDatabase().getPlayerItemsByUuid(playerUuid);

        for (int i = 0; i < droppingStack.getAmount(); i++) {
            ItemStack dropItem = new ItemStack(droppingStack);

            ItemMeta im = dropItem.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (im != null && im.hasLore()) lore = im.getLore();

            Random ra = new Random();
            lore.add("dp-dont-stack-" + ra.nextInt(999999999) + '\u0001');
            im.setLore(lore);
            dropItem.setItemMeta(im);
            dropItem.setAmount(1);

            Item item = dropLocation.getWorld().dropItem(dropLocation, dropItem);

            double xI = Math.random() * 0.5D;
            double zI = Math.random() * 0.5D;

            Random random = new Random();

            int x = random.nextInt(2) * 2 - 1;
            int y = random.nextInt(2) * 2 - 1;
            item.setVelocity(new Vector(xI * (double) x, 0.5D, zI * (double) y));
        }
        --totalPlayer;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    public void setToBeDropped(List<ItemStack> toBeDropped) {
//        this.toBeDropped = toBeDropped;
        this.totalPlayer = toBeDropped.size() - 1;
    }

    public boolean isCountdownRunning() {
        return !isCancelled();
    }
}
