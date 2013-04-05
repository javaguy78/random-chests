package com.themaskedcrusader.randomchests.data;

import com.themaskedcrusader.bukkit.util.TMC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ChestWand {

    private static final HashMap<String, Material> chestWands = new HashMap<String, Material>();

    private ChestWand() {}

    public static void addPlayerWand(Player player) {
        chestWands.put(player.getDisplayName(), player.getItemInHand().getType());
    }

    public static void removePlayerWand(Player player) {
        chestWands.remove(player.getDisplayName());
        player.sendMessage(TMC.WIZARD + "Your wand has been disabled.");
    }

    public static Material getPlayerWand(Player player) {
        return chestWands.get(player.getDisplayName());
    }

}
