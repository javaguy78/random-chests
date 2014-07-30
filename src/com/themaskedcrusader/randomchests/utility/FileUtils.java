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

package com.themaskedcrusader.randomchests.utility;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.themaskedcrusader.bukkit.config.SaveFile;
import com.themaskedcrusader.randomchests.data.RandomChest;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtils {
    static final String DATA = ".data";
    static final String INFO = ".info";
    static final String WARNING = ".warning";
    static final String WARNING_TEXT = "DO NOT MODIFY THIS FILE BY HAND.";
    static final String ENCODING = "base64";

    public static HashMap<String, RandomChest> readObjectFromDisk(String folder,
                                                                  String fileKey,
                                                                  JavaPlugin plugin) {
        HashMap<String, RandomChest> toReturn = new HashMap<String, RandomChest>();

        File dir = new File(plugin.getDataFolder() + "/" + folder);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (File file : dir.listFiles()) {
            String fileName = file.getName();
            SaveFile saveFile = new SaveFile(plugin, getFileName(folder, fileName));

            try {
                String objects = saveFile.getConfig().getString(fileKey + ".data");

                ByteArrayInputStream bis = new ByteArrayInputStream(objects.getBytes());
                GZIPInputStream in = (GZIPInputStream) MimeUtility.decode(bis, ENCODING);
                ObjectInputStream ois = new ObjectInputStream(in);

                RandomChest chest = (RandomChest) ois.readObject();

                toReturn.put(chest.getChestKey(), chest);

            } catch (Exception e) {
                plugin.getLogger().info("Error Reading Chest file " + fileName);
                plugin.getLogger().info("File Is Corrupt! Please reconfigure the chest");
            }
        }

        return toReturn;
    }

    public static boolean saveFileToDisk(String folder, String fileName, String fileKey, String infoText, Object toSave,
                                         JavaPlugin plugin) {

        SaveFile saveFile = new SaveFile(plugin, getFileName(folder, fileName));
        saveFile.getConfig().set(fileKey + INFO, infoText);
        saveFile.getConfig().set(fileKey + WARNING, WARNING_TEXT);

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream os = (GZIPOutputStream) MimeUtility.encode(bos, ENCODING);
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(toSave);
            out.flush();

            String objects = bos.toString();

            out.close();
            os.close();
            bos.close();

            saveFile.getConfig().set(fileKey + DATA, objects);
            saveFile.saveConfig();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void removeFileFromDisk(String folder, String fileName, JavaPlugin plugin) {
        SaveFile saveFile = new SaveFile(plugin, getFileName(folder, fileName));
        saveFile.delete();
    }

    private static String getFileName(String folder, String fileName) {
        if ("".equals(folder)) {
            return fileName;
        } else {
            return folder + "/" + fileName;
        }
    }
}

