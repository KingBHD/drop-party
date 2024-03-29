package me.kingbhd.dropparty.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.kingbhd.dropparty.DropParty;
import me.kingbhd.dropparty.database.entities.PlayerDrops;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class DropsDatabase {
    private final Dao<PlayerDrops, String> playerDropsDao;
    private final DropParty plugin;

    public DropsDatabase(String path, DropParty plugin) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        TableUtils.createTableIfNotExists(connectionSource, PlayerDrops.class);
        playerDropsDao = DaoManager.createDao(connectionSource, PlayerDrops.class);
        this.plugin = plugin;
    }

    public void removePlayerDrop(Integer pk) {
        try {
            playerDropsDao.deleteById(pk.toString());
        } catch (SQLException exception) {
            Logger.error(exception, "[DropParty] Failed to remove player from database.");
        }
    }

    public void resetDrops() {
        try {
            TableUtils.clearTable(playerDropsDao.getConnectionSource(), PlayerDrops.class);
        } catch (SQLException exception) {
            Logger.error(exception, "[DropParty] Failed to reset drops records.");
        }
    }

    public void addPlayer(Player player, List<ItemStack> itemStackList) {
        try {
            for (ItemStack item : itemStackList) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

                bukkitObjectOutputStream.writeObject(item);
                bukkitObjectOutputStream.flush();

                byte[] rawData = byteArrayOutputStream.toByteArray();
                String encodedData = Base64.getEncoder().encodeToString(rawData);

                // Save to Database
                PlayerDrops playerDrops = new PlayerDrops();
                playerDrops.setUuid(player.getUniqueId().toString());
                playerDrops.setUsername(player.getDisplayName());
                playerDrops.setStack(encodedData);
                playerDropsDao.create(playerDrops);

                bukkitObjectOutputStream.close();
            }
        } catch (SQLException | IOException exception) {
            Logger.error(exception, "[DropParty] Failed to add player in database.");
        }
    }

    public List<ItemStack> getStacks(Player player) {
        List<ItemStack> itemStackList = new ArrayList<>();

        try {
            List<PlayerDrops> playerDropsList;
            if (player == null) {
                playerDropsList = playerDropsDao.queryForAll();
            } else {
                playerDropsList = playerDropsDao.queryForEq("uuid", player.getUniqueId().toString());
            }
            for (PlayerDrops playerDrops : playerDropsList) {

                byte[] rawData = Base64.getDecoder().decode(playerDrops.getStack());
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

                ItemStack itemStack = (ItemStack) bukkitObjectInputStream.readObject();

                ItemMeta im = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (im != null) {
                    if (im.hasLore()) lore = im.getLore();

                    if (lore == null) lore = new ArrayList<>();
                    lore.add("Donor: " + playerDrops.getUsername());
                    im.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "donor"), PersistentDataType.STRING, playerDrops.getUsername());
                    im.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "pk"), PersistentDataType.INTEGER, playerDrops.getId());
                    im.setLore(lore);
                    itemStack.setItemMeta(im);
                }
                itemStackList.add(itemStack);
                bukkitObjectInputStream.close();
            }
            return itemStackList;
        } catch (SQLException | ClassNotFoundException | IOException exception) {
            Logger.error(exception, "[DropParty] Failed to get players stacks from database.");
        }
        return null;
    }

    public PlayerDrops getRandomStack() {
        Random ra = new Random();
        try {
            List<PlayerDrops> playerDropsList = playerDropsDao.queryForAll();
            if (playerDropsList.isEmpty()) return null;

            PlayerDrops playerDrops;
            if (playerDropsList.size() == 1) {
                playerDrops = playerDropsList.get(0);
            } else {
                playerDrops = playerDropsList.get(ra.ints(0, playerDropsList.size() - 1).findFirst().getAsInt());
            }

            byte[] rawData = Base64.getDecoder().decode(playerDrops.getStack());
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            ItemStack itemStack = (ItemStack) bukkitObjectInputStream.readObject();
            playerDrops.setItemStackList(itemStack);

            bukkitObjectInputStream.close();
            return playerDrops;
        } catch (SQLException | ClassNotFoundException | IOException exception) {
            Logger.error(exception, "[DropParty] Failed to get stacks groped by participants.");
        }
        return null;
    }
}
