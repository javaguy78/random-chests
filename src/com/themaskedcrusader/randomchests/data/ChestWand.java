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
