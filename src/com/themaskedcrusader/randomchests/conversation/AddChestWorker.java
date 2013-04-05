package com.themaskedcrusader.randomchests.conversation;

import com.themaskedcrusader.bukkit.chest.SmartChest;
import com.themaskedcrusader.bukkit.serializer.Serializer;
import com.themaskedcrusader.bukkit.util.TMC;
import com.themaskedcrusader.randomchests.data.KitChests;
import com.themaskedcrusader.randomchests.data.RandomChest;
import com.themaskedcrusader.randomchests.data.RandomChests;
import org.bukkit.block.Block;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AddChestWorker {

    public static void addChest(ConversationContext context) {
        SmartChest sc = (SmartChest) context.getSessionData(AddConversation.CHEST);
        Block originalChest = Serializer.getBlockAtSerializedLocation(sc.getLocation());
        JavaPlugin plugin =Serializer.deserializePlugin(sc.getPlugin());
        RandomChest chest = new RandomChest(originalChest, plugin, context);
        Player player = (Player) context.getSessionData(AddConversation.PLAYER);

        if (chest.isKit()) {
            boolean success = KitChests.add(chest);
            if (success) {
                player.sendMessage(TMC.SUCCESS + "Kit Chest Successfully Added!");
            } else {
                player.sendMessage(TMC.STERN + "An error occurred while adding kit, Please try again.");
            }

        } else {
            boolean success = RandomChests.add(chest);
            if (success) {
                player.sendMessage(TMC.SUCCESS + "Chest Successfully Added!");
            } else {
                player.sendMessage(TMC.STERN + "An error occurred while adding chest, Please try again.");
            }
        }
    }
}
