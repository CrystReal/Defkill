package com.streamcraft.Defkill.Models;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Utils.DKLog;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 24.10.13  21:45
 */
public class DKBlockedBlocks {
    public static ArrayList<Location> blocked = new ArrayList<Location>();
    public static ArrayList<Location> blockedChunk = new ArrayList<Location>();
    public static int chunkRadius = 0;

    public static boolean isBlocked(Location loc) {
        if (blocked.contains(loc))
            return true;
        else {
            for (Location l : blockedChunk) {
                int x1 = l.getBlockX();
                int z1 = l.getBlockZ();
                int radiusSquared = chunkRadius * chunkRadius;
                for (int x = -chunkRadius; x <= chunkRadius; x++) {
                    for (int z = -chunkRadius; z <= chunkRadius; z++) {
                        if ((x * x) + (z * z) <= radiusSquared) {
                            if (x1 + x == loc.getBlockX() && z1 + z == loc.getBlockZ()) {
                                return true;
                            }
                        }
                    }
                }
            }
                /*if (Math.abs(l.getX()) > Math.abs(loc.getX()) && Math.abs(l.getX()) - Math.abs(loc.getX()) <= chunkRadius)
                    return true;
                else if (Math.abs(l.getX()) <= Math.abs(loc.getX()) && Math.abs(loc.getX()) - Math.abs(l.getX()) <= chunkRadius)
                    return true;
                else if (Math.abs(l.getZ()) > Math.abs(loc.getZ()) && Math.abs(l.getZ()) - Math.abs(loc.getZ()) <= chunkRadius)
                    return true;
                else if (Math.abs(l.getZ()) <= Math.abs(loc.getZ()) && Math.abs(Math.abs(loc.getZ()) - Math.abs(l.getZ())) <= chunkRadius)
                    return true;
            }       */
        }
        return false;
    }

    public static void addBlock(Location loc) {
        if (!isBlocked(loc)) {
            blocked.add(loc);
        }
    }

    public static void addChunk(Location loc) {
        if (!isBlocked(loc)) {
            blockedChunk.add(loc);
        }
    }

    public static void removeBlock(Location loc) {
        if (isBlocked(loc)) {
            blocked.remove(loc);
        }
    }

    public static void setAutoRefill(final Material block, final Byte data, final Location loc, Long time) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(DefkillPlugin.pl, new Runnable() {
            public void run() {
                Block tmp = loc.getBlock();
                tmp.setType(block);
                tmp.setData(data);
                removeBlock(loc);
            }
        }, time);
    }
}
