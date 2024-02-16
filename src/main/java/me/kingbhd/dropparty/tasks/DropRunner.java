package me.kingbhd.dropparty.tasks;

import me.kingbhd.dropparty.DropParty;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropRunner extends BukkitRunnable {
    private final DropParty plugin;
    private final Location location;
    List<Integer> playerPKs;
    private BukkitTask task;

    public DropRunner(DropParty plugin) {
        this.plugin = plugin;
        this.location = this.plugin.getConfig().getLocation("location");
    }

    public void reset() {
        if (this.task != null && this.task.isCancelled()) {
            this.task.cancel();
        }
        this.task = null;
    }

    public void start() {
        if (this.task != null) return;

        this.playerPKs = this.plugin.getDatabase().getPlayerDropPrimaryKeys();
        this.task = this.runTask(plugin);
    }

    public boolean stop() {
        if (this.task == null) return false;
        this.task.cancel();
        return true;
    }

    @Override
    public void run() {
        for (Integer pk : this.playerPKs) {
            List<ItemStack> toBeDropped = this.plugin.getDatabase().getStacksByPK(pk);
            if (toBeDropped == null || toBeDropped.isEmpty()) continue;

            for (ItemStack dropItem : toBeDropped) {
                (new BukkitRunnable() {
                    int itemStackCount = dropItem.getAmount() - 1;

                    @Override
                    public void run() {
                        if (this.itemStackCount == 0) this.cancel();

                        ItemMeta im = dropItem.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        if (im != null && im.hasLore()) lore = im.getLore();

                        Random ra = new Random();
                        lore.add("dp-dont-stack-" + ra.nextInt(999999999) + '\u0001');
                        im.setLore(lore);
                        dropItem.setItemMeta(im);
                        dropItem.setAmount(1);

                        Item item = location.getWorld().dropItem(location, dropItem);

                        double xI = Math.random() * 0.5D;
                        double zI = Math.random() * 0.5D;

                        Random random = new Random();

                        int x = random.nextInt(2) * 2 - 1;
                        int y = random.nextInt(2) * 2 - 1;
                        item.setVelocity(new Vector(xI * (double) x, 0.5D, zI * (double) y));
                        --this.itemStackCount;
                    }
                }).runTaskTimer(this.plugin, 20L, 40L);
            }
        }
    }
}
