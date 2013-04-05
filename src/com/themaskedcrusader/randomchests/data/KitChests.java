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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KitChests {
    private static final String KITS = "kits";
    private static final String FILE = KITS + ".dat";
    private static HashMap<String, RandomChest> kits = new HashMap<String, RandomChest>();

    public static boolean add(RandomChest chest) {
        if (!isKit(chest.getLocation())) {
            kits.put(chest.getChestKey(), chest);
            saveKitToDisk(chest);
        }

        return isKit(chest.getKitName());
    }

    public static boolean isKit(String kitName) {
        return get(kitName) != null;
    }

    public static boolean hasChildren(Map<String, Object> location) {
        RandomChest kit = kits.get(com.themaskedcrusader.bukkit.serializer.Serializer.toChestKey(location));
        return RandomChests.attachedTo(kit.getKitName());
    }

    public static boolean isKit(Map<String, Object> location) {
        return kits.containsKey(com.themaskedcrusader.bukkit.serializer.Serializer.toChestKey(location));
    }

    public static RandomChest get(String name) {
        for (Map.Entry<String, RandomChest> chest : kits.entrySet() ) {
            if (chest.getValue().getKitName().equals(name.toUpperCase())) {
                return chest.getValue();
            }
        }
        return null;
    }

    public static RandomChest get(Map<String, Object> location) {
        return kits.get(com.themaskedcrusader.bukkit.serializer.Serializer.toChestKey(location));
    }

    private ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for( Map.Entry<String, RandomChest> kit : kits.entrySet() ) {
            names.add(kit.getValue().getKitName());
        }
        return names;
    }

    public static boolean isValidKitName(String kitName) {
        Pattern p = Pattern.compile("[\\\\/:?*\"<>| ]");
        Matcher m = p.matcher(kitName);
        return !m.matches() || kitName.length() > 14;
    }

    public static void remove(Map<String, Object> location) {
        RandomChest toRemove = kits.get(com.themaskedcrusader.bukkit.serializer.Serializer.toChestKey(location));
        remove(toRemove);
    }

    private static void remove(RandomChest kit) {
        kits.remove(kit.getChestKey());
        removeKitFromDisk(kit);
    }

    @SuppressWarnings("unchecked")
    public static void loadKitsFromDisk(JavaPlugin plugin) {
        kits = FileUtils.readObjectFromDisk(KITS, KITS, plugin);

        if (kits == null) {
            kits = new HashMap<String, RandomChest>();
        } else {
            for (Map.Entry<String, RandomChest> entry : kits.entrySet()) {
                RandomChest chest = entry.getValue();
                Block block = Serializer.getBlockAtSerializedLocation(chest.getLocation());
                if (block.getType() != Material.CHEST) {
                    block.setType(Material.CHEST);
                }
                ItemStack[] items = Serializer.unmaskInventory(chest.getInventory());
                ((Chest) block.getState()).getInventory().setContents(items);
            }
        }
    }

    private static void saveKitToDisk(RandomChest kit) {
        String infoText = "This file contains encoded kit data for a Random Chests";
        String fileName = kit.getChestKey() + ".dat";
        JavaPlugin plugin = Serializer.deserializePlugin(kit.getPlugin());
        FileUtils.saveFileToDisk(KITS, fileName, KITS, infoText, kit, plugin);
    }

    private static void removeKitFromDisk(RandomChest kit) {
        String fileName = kit.getChestKey() + ".dat";
        JavaPlugin plugin =Serializer.deserializePlugin(kit.getPlugin());
        FileUtils.removeFileFromDisk(KITS, fileName,  plugin);
    }

    public static ItemStack[] getInventory(String kitName) {
        RandomChest kit = get(kitName);
        return Serializer.unmaskInventory(kit.getInventory());
    }

    public static String getName(Map<String, Object> location) {
        RandomChest chest = get(location);
        return (chest != null) ? chest.getKitName() : null;
    }

    public static void updateKitContents(SmartChest chest) {
        RandomChest kit = KitChests.get(chest.getLocation());
        KitChests.remove(chest.getLocation());
        kit.setInventory(com.themaskedcrusader.bukkit.serializer.Serializer.unmaskInventory(chest.getInventory()));
        KitChests.add(kit);
    }

}
