package me.kingbhd.dropparty.database.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "player_drops")
public class PlayerDrops {

    @DatabaseField(id = true)
    private Integer id;

    @DatabaseField(canBeNull = false)
    private String uuid;

    @DatabaseField(canBeNull = false)
    private String username;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private String stack;

    public List<ItemStack> getItemStackList() {
        return itemStackList;
    }

    public void setItemStackList(ItemStack itemStackList) {
        this.itemStackList.add(itemStackList);
    }

    public List<ItemStack> itemStackList = new ArrayList<>();

    public PlayerDrops() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

}
