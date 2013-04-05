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

package com.themaskedcrusader.randomchests.data;

import com.themaskedcrusader.bukkit.chest.SmartChest;
import com.themaskedcrusader.bukkit.serializer.Serializer;
import com.themaskedcrusader.randomchests.utility.FileUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RandomChests {
    private static final String CHESTS = "chests";
    private static final String FILE = CHESTS + ".dat";
    private static HashMap<String, RandomChest> chests = new HashMap<String, RandomChest>();
    private static ArrayList<Map<String, Object>> opened = new ArrayList<Map<String, Object>>();

    public static boolean add(RandomChest chest) {
        if (!isRandomChest(chest.getLocation())) {
            chests.put(chest.getChestKey(), chest);
            saveRandomChestToDisk(chest);
        }

        return isRandomChest(chest.getLocation());
    }

    public static boolean isRandomChest(Map<String, Object> location) {
        return chests.containsKey(com.themaskedcrusader.bukkit.serializer.Serializer.toChestKey(location));
    }

    public static RandomChest get(Map<String, Object> location) {
        return chests.get(com.themaskedcrusader.bukkit.serializer.Serializer.toChestKey(location));
    }

    public static void remove(RandomChest chest) {
        refillChest(chest);
        chests.remove(chest.getChestKey());
        removeChestFromDisk(chest);
    }

    public static void refillChest(RandomChest chest) {
        Block lBlock = Serializer.getBlockAtSerializedLocation(chest.getLocation());
        if (chest.isDoubleChest()) {
            Block rBlock = Serializer.getBlockAtSerializedLocation(chest.getOtherSideLocation());
            if (rBlock.getType() != Material.CHEST) {
                rBlock.setType(Material.CHEST);
            }
        }

        if (lBlock.getType() != Material.CHEST) {
            lBlock.setType(Material.CHEST);
        }

        ((Chest) lBlock.getState()).getInventory().setContents(com.themaskedcrusader.bukkit.serializer.Serializer.unmaskInventory(chest.getInventory()));
    }

    @SuppressWarnings("unchecked")
    public static void loadChestsFromDisk(JavaPlugin plugin) {
        chests = FileUtils.readObjectFromDisk(CHESTS, CHESTS, plugin);

        if (chests == null) {
            chests = new HashMap<String, RandomChest>();
        }

        restoreAllChests();
    }

    private static void restoreAllChests() {
        for (Map.Entry<String, RandomChest> rc : chests.entrySet() ) {
            RandomChest chest = rc.getValue();

            restoreBlock(chest.getLocation(), chest.getChestFacing());
            if (chest.isDoubleChest()) {
                restoreBlock(chest.getOtherSideLocation(), chest.getChestFacing());
            }
        }
    }

    public static void restoreBlock(Map<String, Object> loc, byte data) {
        Location location = Serializer.deserializeLocation(loc);
        World world = location.getWorld();
        Block block = world.getBlockAt(location);
        if (block.getType() != Material.CHEST) {
            block.setType(Material.CHEST);
        }
        block.setData(data);
    }

    private static void saveRandomChestToDisk(RandomChest chest) {
        String infoText = "This file contains the map's Random Chest Data";
        JavaPlugin plugin = Serializer.deserializePlugin(chest.getPlugin());
        String fileName = chest.getChestKey() + ".dat";
        FileUtils.saveFileToDisk(CHESTS,  fileName, CHESTS, infoText, chest, plugin);
    }

    private static void removeChestFromDisk(RandomChest chest) {
        String fileName = chest.getChestKey() + ".dat";
        JavaPlugin plugin = Serializer.deserializePlugin(chest.getPlugin());
        FileUtils.removeFileFromDisk(CHESTS, fileName, plugin);
    }

    public static boolean attachedTo(String kitName) {
        for (Map.Entry<String, RandomChest> chest : chests.entrySet()) {
            if (chest.getValue().getKitName().equals(kitName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAttachedToKit(SmartChest chest) {
        RandomChest info = get(chest.getLocation());
        return info.isPartOfKit();
    }

    public static String getKitName(SmartChest chest) {
        if (isAttachedToKit(chest)) {
        }
        RandomChest info = get(chest.getLocation());
        return info.getKitName();
    }

    public static void popChest(SmartChest chest) {
        Location location = Serializer.deserializeLocation(chest.getLocation());
        opened.remove(chest.getLocation());

        World world = location.getWorld();
        world.getBlockAt(location).setType(Material.AIR);
        if (chest.isDoubleChest()) {
            location = Serializer.deserializeLocation(chest.getOtherSideLocation());
            world.getBlockAt(location).setType(Material.AIR);
        }
    }

    public static boolean hasBeenOpened(Map<String, Object> location) {
        return opened.contains(location);
    }

    public static void openChest(Map<String, Object> location) {
        opened.add(location);
    }

    public static void popAllOpenedChests() {
        try {
            for (Map<String, Object> location : opened) {
                RandomChest chest = get(location);
                popChest(chest);
            }
        } catch (Exception ignored) {
            // Only CME caught, we can ignore
        }
    }
}
