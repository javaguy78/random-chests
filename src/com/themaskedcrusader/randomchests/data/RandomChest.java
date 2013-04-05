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

import com.themaskedcrusader.bukkit.Random;
import com.themaskedcrusader.bukkit.chest.SmartChest;
import com.themaskedcrusader.bukkit.serializer.Serializer;
import com.themaskedcrusader.bukkit.util.TMC;
import com.themaskedcrusader.randomchests.conversation.AddConversation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class RandomChest extends SmartChest implements Serializable {
    protected Boolean kit;
    protected String  kitName;
    protected Boolean partOfKit;
    protected Boolean randomContents;
    protected Integer minItems;
    protected Integer maxItems;
    protected Integer percentEmpty;
    protected Boolean allowDuplicates;
    protected Map<String, Object> kitLocation;

    public RandomChest(Block chest, JavaPlugin plugin) {
        super(chest, plugin);
    }

    public RandomChest(Block chest, JavaPlugin plugin, ConversationContext context) {
        super(chest, plugin);
        this.kit = Boolean.parseBoolean(getContext(context, AddConversation.IS_KIT));
        this.kitName = getContext(context, AddConversation.KIT_NAME);
        this.partOfKit = Boolean.parseBoolean(getContext(context, AddConversation.PART_OF_KIT));
        this.randomContents = Boolean.parseBoolean(getContext(context, AddConversation.CONTENTS));
        this.minItems = Integer.parseInt(getContext(context, AddConversation.MIN_ITEMS));
        this.maxItems = Integer.parseInt(getContext(context, AddConversation.MAX_ITEMS));
        this.percentEmpty = Integer.parseInt(getContext(context, AddConversation.EMPTY_PERCENT));
        this.allowDuplicates = Boolean.parseBoolean(getContext(context, AddConversation.DUPLICATES));
    }

    public ItemStack[] getRandomizedInventory() {
        if (partOfKit) {
            RandomChest kit = KitChests.get(kitName);
            return getRandomizedInventoryItemStack(kit);
        } else if (!kit) {
            return getRandomizedInventoryItemStack(this);
        } else {
            return Serializer.unmaskInventory(getInventory());
        }
    }

    private String getContext(ConversationContext context, String contextKey) {
        if (context.getSessionData(contextKey) == null) {
            return "0";
        } else {
            return "" + context.getSessionData(contextKey);
        }
    }

    public Boolean isKit() {
        return kit;
    }

    public void setIsKit(Boolean kit) {
        this.kit = kit;
    }

    public String getKitName() {
        return kitName;
    }

    public Boolean isPartOfKit() {
        return partOfKit;
    }

    public void setPartOfKit(Boolean partOfKit) {
        this.partOfKit = partOfKit;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public Boolean areContentsRandom() {
        return randomContents;
    }

    public void setContentsRandom(Boolean randomContents) {
        this.randomContents = randomContents;
    }

    public Integer getMinItems() {
        return minItems;
    }

    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public Integer getPercentEmpty() {
        return percentEmpty;
    }

    public void setPercentEmpty(Integer percentEmpty) {
        this.percentEmpty = percentEmpty;
    }

    public Boolean allowDuplicates() {
        return allowDuplicates;
    }

    public void setAllowDuplicates(Boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public Location getKitLocation() {
        return Serializer.deserializeLocation(kitLocation);
    }

    public void setKitLocation(Location kitLocation) {
        this.kitLocation = Serializer.serializeLocation(kitLocation);
    }

    public String getStatus() {
        if (isKit()) {
            return TMC.SUCCESS + "This chest is a kit chest";
        } else if (areContentsRandom()) {
            return TMC.SUCCESS + "This chest is a random chest";
        } else {
            return TMC.WIZARD + "This chest is a standard boring chest, blah";
        }
    }

    // This should probably be in a different class, but it makes more sense to put it inside the RandomChest, even
    // if it is pulling it's contents and settings from another RandomChest (the kit) in certain situations.....
    private static ItemStack[] getRandomizedInventoryItemStack(RandomChest info) {
        if (!info.areContentsRandom()) {
            return Serializer.unmaskInventory(info.getInventory());
        }

        Random random = new Random();
        if (random.nextInt(100) > info.getPercentEmpty()) {
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            ItemStack[] allItems = Serializer.unmaskInventory(info.getInventory());
            ArrayList<ItemStack> chosenItems = new ArrayList<ItemStack>();
            int maxItems = random.nextIntBetween(info.getMinItems(), info.getMaxItems());

            // to prevent infinite loop if duplicates aren't allowed
            if (!info.allowDuplicates() && maxItems >= allItems.length) {
                return Serializer.unmaskInventory(info.getInventory());
            }

            for (int i = 1; i < maxItems; i++) {
                int index = random.nextIntBetween(0, allItems.length);

                if (!info.allowDuplicates() && indexes.contains(index)) {
                    i--;
                    continue;
                }

                ItemStack item = allItems[index - 1].clone();
                chosenItems.add(item);
                indexes.add(index);
            }

            ItemStack[] toReturn = new ItemStack[chosenItems.size()];
            for (int i = 0; i < chosenItems.size(); i++) {
                toReturn[i] = chosenItems.get(i);
            }

            return toReturn;

        } else {
            return null;
        }
    }
}
