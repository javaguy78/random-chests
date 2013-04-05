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

package com.themaskedcrusader.randomchests.schedule;

import com.themaskedcrusader.bukkit.chest.SmartChest;
import com.themaskedcrusader.bukkit.serializer.Serializer;
import com.themaskedcrusader.bukkit.util.TMC;
import com.themaskedcrusader.bukkit.util.WorldUtils;
import com.themaskedcrusader.randomchests.data.RandomChests;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;


public class ChestSchedule {

    public static void scheduleDespawn(final SmartChest chest) {
        final JavaPlugin plugin = Serializer.deserializePlugin(chest.getPlugin());
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                RandomChests.popChest(chest);
                ChestSchedule.scheduleRespawn(chest);
            }
        }, 200L);
    }

    public static void scheduleRespawn(final SmartChest chest) {
        final JavaPlugin plugin = Serializer.deserializePlugin(chest.getPlugin());
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (RandomChests.isRandomChest(chest.getLocation())) {
                    Location location = Serializer.deserializeLocation(chest.getLocation());
                    Collection<Entity> nearbyPlayers = WorldUtils.getNearbyPlayers(location, 10);
                    if (nearbyPlayers.size() == 0) {
                        RandomChests.restoreBlock(chest.getLocation(), chest.getChestFacing());
                        if (chest.isDoubleChest()) {
                            RandomChests.restoreBlock(chest.getOtherSideLocation(), chest.getChestFacing());
                        }

                    } else {
                        for (Entity entity : nearbyPlayers) {
                            ((Player) entity).sendMessage(TMC.STERN + "Chests can't respawn with players nearby...");
                        }
                        ChestSchedule.scheduleRespawn(chest);
                    }
                }
            }
        }, 6000L);
    }
}
