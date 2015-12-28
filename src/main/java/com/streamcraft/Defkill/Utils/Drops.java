package com.streamcraft.Defkill.Utils;

import org.bukkit.Material;

/**
 * Created by Alex
 * Date: 02.11.13  0:15
 */
public class Drops {
    public static Material dropsFromOre(Material m) {
        switch (m) {
            case IRON_ORE:
                return Material.IRON_ORE;
            case GOLD_ORE:
                return Material.GOLD_ORE;
            case COAL_ORE:
                return Material.COAL;
            case DIAMOND_ORE:
                return Material.DIAMOND;
            case MELON_BLOCK:
                return Material.MELON;
            case WHEAT:
                return Material.WHEAT;
        }
        return m;
    }
}
