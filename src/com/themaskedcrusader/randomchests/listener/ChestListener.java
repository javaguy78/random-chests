/*
 * Copyright 2013 Topher Donovan (themaskedcrusader.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.themaskedcrusader.randomchests.listener;

import com.themaskedcrusader.bukkit.chest.SmartChest;
import com.themaskedcrusader.bukkit.util.TMC;
import com.themaskedcrusader.randomchests.conversation.CreateConversation;
import com.themaskedcrusader.randomchests.data.ChestWand;
import com.themaskedcrusader.randomchests.data.KitChests;
import com.themaskedcrusader.randomchests.data.RandomChest;
import com.themaskedcrusader.randomchests.data.RandomChests;
import com.themaskedcrusader.randomchests.schedule.ChestSchedule;
import com.themaskedcrusader.randomchests.utility.Permissions;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class ChestListener implements Listener {
    private final JavaPlugin plugin;

    public ChestListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void openChest(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            RandomChest chest = new RandomChest(event.getClickedBlock(), plugin);
            Player player = event.getPlayer();

            // #########################################################################
            // #####  Right-Click while holding playerWand = Remove from plugin    #####
            // #########################################################################
            if (canInteract(event, ChestWand.getPlayerWand(player)) &&
                    (RandomChests.isRandomChest(chest.getLocation()) || KitChests.isKit(chest.getLocation()))) {

                removeChestFromPlugin(event); //VERIFIED FOR KITS!

            } else

            // ##################################################################
            // ##### Right-Click while holding BLAZE_ROD = Open Kit Chest   #####
            // ##################################################################
            if (KitChests.isKit(chest.getLocation())) {

                openKitChest(event); // VERIFIED WORKING

            } else

            // ################################################################
            // ##### If isRandomChest, randomize contents before opening  #####
            // ################################################################
            if (RandomChests.isRandomChest(chest.getLocation()) && !RandomChests.hasBeenOpened(chest.getLocation())) {
                RandomChest randomChest = RandomChests.get(chest.getLocation());
                RandomChests.openChest(randomChest.getLocation());
                ItemStack[] randomized = randomChest.getRandomizedInventory();

                if (randomized != null) {
                    ((Chest) event.getClickedBlock().getState()).getInventory().setContents(randomized);
                }
                ChestSchedule.scheduleDespawn(randomChest);
            }
        }
    }

    @EventHandler
    public void punchChest(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST &&
                event.getAction() == Action.LEFT_CLICK_BLOCK) {
            SmartChest chest = new RandomChest(event.getClickedBlock(), plugin);
            Player player = event.getPlayer();

            // #####################################################################
            // #####  Punch kit while holding playerWand = update kit contents #####
            // #####################################################################
            if (canInteract(event, ChestWand.getPlayerWand(player)) && KitChests.isKit(chest.getLocation())) {

                KitChests.updateKitContents(chest); // VERIFIED!!!
                player.sendMessage(TMC.WIZARD + "Updated kit inventory");
                event.setCancelled(true);

            } else

            // ####################################################################
            // #####  Punch chest while holding playerWand = Add to plugin    #####
            // ####################################################################
            if (canInteract(event, ChestWand.getPlayerWand(player)) &&
                    (!KitChests.isKit(chest.getLocation()) || !RandomChests.isRandomChest(chest.getLocation()))) {

                addChestToPlugin(event); // VERIFIED WORKING FOR KITS
                event.setCancelled(true);

            } else

            // #####################################################################
            // #####  Punch chest while holding BLAZE_ROD = echo chest status  #####
            // #####################################################################
            if (canInteract(event, Material.BLAZE_ROD)) {

                echoStatus(event); // VERIFIED WORKING FOR KITS
                event.setCancelled(true);

            } else

            // ############################################################################
            // #####  if isRandomChest: Punch chest will pop chest and drop contents  #####
            // ############################################################################
            if (RandomChests.isRandomChest(chest.getLocation())) {
                RandomChest random = RandomChests.get(chest.getLocation());
                ItemStack[] randomized = random.getRandomizedInventory();

                if (!RandomChests.hasBeenOpened(chest.getLocation())) {
                    ((Chest) event.getClickedBlock().getState()).getInventory().setContents(randomized);
                }
                RandomChests.popChest(chest);
            }
        }
    }

    // todo: test kit doesn't break with explosions
    @EventHandler(priority = EventPriority.HIGHEST)
    public void doNotBreakKitChests(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.CHEST) {
            SmartChest chest = new SmartChest(event.getBlock(),  plugin);
            if (KitChests.isKit(chest.getLocation())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(TMC.STERN + "You cannot break a kit chest");
            }
        }
    }

    //############################################
    //#####   HELPER METHODS                 #####
    //############################################


    public boolean canInteract(PlayerInteractEvent event, Material tool) {
        return (Permissions.hasPermissions(event.getPlayer()) && event.getPlayer().getItemInHand().getType() == tool);
    }

    public void addChestToPlugin(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SmartChest chest = new SmartChest(event.getClickedBlock(), plugin);
        if (KitChests.isKit(chest.getLocation()) ||
                RandomChests.isRandomChest(chest.getLocation())) {
            player.sendMessage(TMC.STERN + "Chest is already added to plugin");
        } else {
            CreateConversation conversation = new CreateConversation(plugin);
            conversation.doConversation(player, chest);
        }
    }

    public void removeChestFromPlugin(PlayerInteractEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        RandomChest chest = new RandomChest(event.getClickedBlock(), plugin);

        if (RandomChests.isRandomChest(chest.getLocation())) {
            RandomChests.remove(chest);

        } else if (KitChests.isKit(chest.getLocation())) {
            if (KitChests.hasChildren(chest.getLocation())){
                player.sendMessage(TMC.STERN + "Cannot Remove Kit - Other chests are attached");
                return;

            } else {
                KitChests.remove(chest.getLocation());
            }
        }
        event.getPlayer().sendMessage(TMC.STERN + "The chest has been deactivated");
    }

    // TODO: Consolidate this with RandomChest.getStatus
    public void echoStatus(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SmartChest chest = new SmartChest(event.getClickedBlock(), plugin);

        if (KitChests.isKit(chest.getLocation())) {
            String kitName = KitChests.getName(chest.getLocation());
            player.sendMessage(TMC.SUCCESS + "Target is a Kit Chest (name: " + kitName + ")");
        } else if (RandomChests.isRandomChest(chest.getLocation())) {
            player.sendMessage(TMC.SUCCESS + "Target is a Randomized Chest");
        } else {
            player.sendMessage(TMC.WIZARD + "Target is a normal, boring chest... blah");
        }
    }

    public void openKitChest(PlayerInteractEvent event) {
        if (!canInteract(event, Material.BLAZE_ROD)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(TMC.STERN + "This kit chest is locked by the admin.");
        }
    }

}
