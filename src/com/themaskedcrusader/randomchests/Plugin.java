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

package com.themaskedcrusader.randomchests;

import com.themaskedcrusader.bukkit.Library;
import com.themaskedcrusader.randomchests.command.CommandListener;
import com.themaskedcrusader.randomchests.data.KitChests;
import com.themaskedcrusader.randomchests.data.RandomChests;
import com.themaskedcrusader.randomchests.listener.ChestListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class Plugin extends JavaPlugin {

    public void onEnable() {
        try{
            loadDataFromDisk();
            new ChestListener(this);
            getLogger().info("TMCs Random Chests Activated!");
        } catch (NoClassDefFoundError e) {
            getLogger().log(Level.SEVERE,  "TMC-LIB Library Missing or cannot load: Disabling Plugin.");
            getLogger().log(Level.SEVERE,  "See install instructions at http://dev.bukkit.org/server-mods/tmc-lib/");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadDataFromDisk() {
        Library.checkForNewVersion(getServer().getConsoleSender());
        KitChests.loadKitsFromDisk(this);
        RandomChests.loadChestsFromDisk(this);
    }

    public void onDisable() {
        getLogger().info("TMCs Random Chests Deactivated!");
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CommandListener listener = new CommandListener();
        return listener.exec(commandSender, s, strings, this);
    }
}
