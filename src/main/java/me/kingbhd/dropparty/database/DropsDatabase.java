package me.kingbhd.dropparty.database;

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
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class DropsDatabase {
    private final Connection conn;

    public DropsDatabase(String path) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = conn.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS drops (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT NOT NULL, username TEXT NOT NULL, items TEXT NOT NULL)");
        }
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) conn.close();
    }

    public List<String> getPlayerUuids() {
        List<String> ids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT uuid FROM drops;")) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) ids.add(rs.getString("uuid"));
            return ids;
        } catch (SQLException exception) {
            Logger logger = LoggerFactory.getLogger(DropsDatabase.class);
            logger.error(exception.toString());
        }
        return null;
    }

    public List<Integer> getPlayerIds() {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM drops;")) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) ids.add(rs.getInt("uuid"));
            return ids;
        } catch (SQLException exception) {
            Logger logger = LoggerFactory.getLogger(DropsDatabase.class);
            logger.error(exception.toString());
        }
        return null;
    }

    public List<ItemStack> getPlayerItemsByUuid(String uuid) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id, username, items FROM drops WHERE uuid = ?;")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();

            List<ItemStack> items = new ArrayList<>();
            while (rs.next()) {
                String encodedItem = rs.getString("items");

                byte[] rawData = Base64.getDecoder().decode(encodedItem);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

                ItemStack itemStack = (ItemStack) bukkitObjectInputStream.readObject();

                ItemMeta im = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (im != null) {
                    if (im.hasLore()) lore = im.getLore();
                    String username = rs.getString("username");

                    if (lore == null) lore = new ArrayList<>();
                    lore.add("DP Donation: " + username);
                    im.setLore(lore);
                    itemStack.setItemMeta(im);
                }
                items.add(itemStack);

                bukkitObjectInputStream.close();
            }
            return items;
        } catch (IOException | ClassNotFoundException | SQLException exception) {
            Logger logger = LoggerFactory.getLogger(DropsDatabase.class);
            logger.error(exception.toString());
        }
        return null;
    }

    // Insert to ItemStack
    public void addPlayerItem(Player player, String encodedItem) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO drops (uuid, username, items) VALUES (?, ?, ?)")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setString(3, encodedItem);
            ps.executeUpdate();
        }
    }

    public void savePlayerItems(Player player, List<ItemStack> items) {
        try {
            // Encode Serialized ItemStack
            for (ItemStack item : items) {
                System.out.println("Stack: " + item);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

                bukkitObjectOutputStream.writeObject(item);
                bukkitObjectOutputStream.flush();

                byte[] rawData = byteArrayOutputStream.toByteArray();

                // Save to Database
                String encodedData = Base64.getEncoder().encodeToString(rawData);
                addPlayerItem(player, encodedData);

                bukkitObjectOutputStream.close();
            }
        } catch (SQLException | IOException exception) {
            Logger logger = LoggerFactory.getLogger(DropsDatabase.class);
            logger.error(exception.toString());
        }

    }
}
