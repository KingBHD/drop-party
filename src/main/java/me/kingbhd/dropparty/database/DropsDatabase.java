package me.kingbhd.dropparty.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.kingbhd.dropparty.database.entities.PlayerDrops;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DropsDatabase {
    private static final Logger logger = LoggerFactory.getLogger(DropsDatabase.class);
    private final Dao<PlayerDrops, String> playerDropsDao;

    public DropsDatabase(String path) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        TableUtils.createTableIfNotExists(connectionSource, PlayerDrops.class);
        playerDropsDao = DaoManager.createDao(connectionSource, PlayerDrops.class);
    }

    public void removePlayer(String pk) {
        try {
            playerDropsDao.deleteById(pk);
        } catch (SQLException exception) {
            logger.error(exception.toString());

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
            logger.error(exception.toString());
        }
    }

    public List<ItemStack> getStacksByPlayer(Player player) {
        List<ItemStack> itemStackList = new ArrayList<>();

        try {
            List<PlayerDrops> playerDropsList = playerDropsDao.queryForEq("uuid", player.getUniqueId().toString());
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
                    im.setLore(lore);
                    itemStack.setItemMeta(im);
                }
                itemStackList.add(itemStack);
                bukkitObjectInputStream.close();
            }
            return itemStackList;
        } catch (SQLException | ClassNotFoundException | IOException exception) {
            logger.error(exception.toString());
        }
        return null;
    }

    public List<ItemStack> getStacks() {
        List<ItemStack> itemStackList = new ArrayList<>();

        try {
            List<PlayerDrops> playerDropsList = playerDropsDao.queryForAll();
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
                    im.setLore(lore);
                    itemStack.setItemMeta(im);
                }
                itemStackList.add(itemStack);
                bukkitObjectInputStream.close();
            }
            return itemStackList;
        } catch (SQLException | ClassNotFoundException | IOException exception) {
            logger.error(exception.toString());
        }
        return null;
    }

}
