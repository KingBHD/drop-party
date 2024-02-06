package me.kingbhd.dropparty.managers;

import org.bukkit.entity.Player;

public class PlayerManager {

    private Player owner;

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

}
