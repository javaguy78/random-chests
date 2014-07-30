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

package com.themaskedcrusader.randomchests.command;

import com.themaskedcrusader.bukkit.util.TMC;
import com.themaskedcrusader.randomchests.data.ChestWand;
import com.themaskedcrusader.randomchests.data.RandomChests;
import com.themaskedcrusader.randomchests.utility.Permissions;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandListener {

    public boolean exec(CommandSender sender, String command, String[] args, JavaPlugin plugin) {
        if (command.equalsIgnoreCase("chestWand")) { return chestWand(sender, args);       }
        if (command.equalsIgnoreCase("rc"))        { return fadeChest(sender, args, plugin); }

        sender.sendMessage(TMC.STERN + "Command Not Found...");
        return false;
    }

    private boolean chestWand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Permissions.hasPermissions(player)) {
                if (args.length > 0 && args[0].equalsIgnoreCase("unbind")) {
                    ChestWand.removePlayerWand(player);
                }
                bindWand(player);
                return true;
            } else {
                return unauthorized(sender);
            }
        } else {
            sender.sendMessage(TMC.STERN + "Command can only be used by a player in-game");
            return false;
        }
    }

    private void bindWand(Player player) {
        if (player.getItemInHand().getType() == Material.BLAZE_ROD) {
            player.sendMessage(TMC.STERN + "You cannot use a Blaze Rod as a wand");
        } else if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(TMC.STERN + "You cannot set your own hand as a wand");
        } else {
            ChestWand.addPlayerWand(player);
            player.sendMessage(TMC.SUCCESS + "Wand Set!");
        }
    }

    private boolean fadeChest(CommandSender sender, String[] args, JavaPlugin plugin) {
        if (args.length > 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!Permissions.hasPermissions(player)) {
                    return unauthorized(sender);
                }
            }

            if (args[0].equalsIgnoreCase("despawn")) {
                RandomChests.popAllOpenedChests();
                return true;
            }

            if (args[0].equalsIgnoreCase("respawn")) {
                RandomChests.loadChestsFromDisk(plugin);
                return true;
            }
        }

        sender.sendMessage(TMC.STERN + "Invalid Parameters. Please try again.");
        return false;
    }

    private boolean unauthorized(CommandSender sender) {
        sender.sendMessage(TMC.STERN + "You do not have permissions to run that command");
        return false;
    }
}
