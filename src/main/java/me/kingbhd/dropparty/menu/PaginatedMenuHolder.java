package me.kingbhd.dropparty.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class PaginatedMenuHolder extends MenuHolder {
    protected int page = 0;
    protected int maxItemsPerPage = 47;
    protected int index = 0;


    public void addMenuBorder() {
        inventory.setItem(45, super.FILLER_GLASS);
        inventory.setItem(46, super.FILLER_GLASS);
        inventory.setItem(47, makeItem(Material.ARROW, ChatColor.GREEN + "Previous"));
        inventory.setItem(48, super.FILLER_GLASS);
        inventory.setItem(49, makeItem(Material.MAP, ChatColor.DARK_RED + "Close"));
        inventory.setItem(50, super.FILLER_GLASS);
        inventory.setItem(51, makeItem(Material.ARROW, ChatColor.GREEN + "Next"));
        inventory.setItem(52, super.FILLER_GLASS);
        inventory.setItem(53, super.FILLER_GLASS);
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

}
