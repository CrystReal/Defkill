package com.streamcraft.Defkill.Utils;

import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * Created by Alex
 * Date: 07.01.14  0:55
 */
public class L {
    public static void $(String l) {
        Bukkit.getLogger().log(Level.INFO, l);
    }
}
