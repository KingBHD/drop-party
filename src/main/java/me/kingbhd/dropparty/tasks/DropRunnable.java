package me.kingbhd.dropparty.tasks;

import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.database.entities.PlayerDrops;
import me.kingbhd.dropparty.managers.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DropRunnable extends BukkitRunnable {

    private final DropParty plugin;
    private final Location dropLocation;
    private BukkitTask task;

    public DropRunnable(DropParty plugin) {
        this.plugin = plugin;
        this.dropLocation = this.plugin.getConfig().getLocation("location");
    }

    public void start() {
        if (this.task != null) return;
//        this.task = this.runTask(this.plugin);
        this.task = this.runTaskTimer(this.plugin, 20L, 40L);
    }

    public boolean stop() {
        if (this.task == null) return false;
        this.task.cancel();
        return true;
    }

    @Override
    public void run() {
        PlayerDrops playerDrops = this.plugin.getDatabase().getRandomStack();
        if (playerDrops == null) {
            Bukkit.broadcastMessage(MessagesManager.getColoredMessage(this.plugin.getConfig().getString("message.dropparty-ended")));
            this.cancel();
            return;
        }

        List<ItemStack> participantsDrop = playerDrops.getItemStackList();
        for (ItemStack itemStack : participantsDrop) {
            for (int i = 0; i < itemStack.getAmount(); ++i) {
                ItemStack itemSingle = new ItemStack(itemStack);

                ItemMeta im = itemSingle.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (im != null && im.hasLore()) lore = im.getLore();

                Random ra = new Random();
                if (im != null && lore != null) {
                    lore.add("dp-dont-stack-" + ra.nextInt(999999999) + '\u0001');
                    im.setLore(lore);
                }
                itemSingle.setItemMeta(im);
                itemSingle.setAmount(1);

                Item item = Objects.requireNonNull(dropLocation.getWorld()).dropItem(dropLocation, itemSingle);

                double xI = Math.random() * 0.5D;
                double zI = Math.random() * 0.5D;

                int x = ra.nextInt(2) * 2 - 1;
                int y = ra.nextInt(2) * 2 - 1;
                item.setVelocity(new Vector(xI * (double) x, 0.5D, zI * (double) y));
            }
            this.plugin.getDatabase().removePlayerDrop(playerDrops.getId());
        }
    }
}
