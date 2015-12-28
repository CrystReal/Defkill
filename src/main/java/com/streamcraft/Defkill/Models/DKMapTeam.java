package com.streamcraft.Defkill.Models;

import com.streamcraft.Defkill.Utils.DKConfig;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 24.10.13  3:47
 */
public class DKMapTeam {
    private String name;
    private ArrayList<Location> spawns = new ArrayList<Location>();

    public DKMapTeam(DKMap map, String name, ConfigurationSection conf) {
        this.name = name;
        for (String item : conf.getStringList(name + "Spawns")) {
            spawns.add(DKConfig.stringToLocationWithoutWorld(map.getWorld(), item));
        }
    }

    public void addSpawn(Location loc) {
        this.spawns.add(loc);
    }

    public void removeSpawn(Location loc) {
        this.spawns.remove(loc);
    }

    public ArrayList<Location> getSpawns() {
        return this.spawns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
