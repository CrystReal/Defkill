package com.streamcraft.Defkill.Models;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Utils.DKConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Alex
 * Date: 24.10.13  0:49
 */
public class DKMap {
    private String name;
    private int id;
    private World world;
    private HashMap<String, DKMapTeam> mapTeams = new HashMap<String, DKMapTeam>();
    private HashMap<String, Location> teamsNexus = new HashMap<String, Location>();
    public ArrayList<Location> diamonds = new ArrayList<Location>();
    private boolean storm;
    private boolean thunder;
    private ArrayList<Location> bossesSpawns = new ArrayList<Location>();
    public Location spectatorsSpawn;

    public DKMap(ConfigurationSection conf) {
        this.name = conf.getString("name");
        this.id = conf.getInt("id");
        this.storm = conf.getBoolean("storm", false);
        this.thunder = conf.getBoolean("thunder", false);
        this.world = Bukkit.getServer().createWorld(new WorldCreator(conf.getString("world")));
        this.spectatorsSpawn = DKConfig.stringToLocationWithoutWorld(this.world, conf.getString("spectatorsSpawn"));
        this.mapTeams.put("red", new DKMapTeam(this, "red", conf));
        this.mapTeams.put("blue", new DKMapTeam(this, "blue", conf));
        this.mapTeams.put("green", new DKMapTeam(this, "green", conf));
        this.mapTeams.put("yellow", new DKMapTeam(this, "yellow", conf));
        this.teamsNexus.put("red", DKConfig.stringToLocationWithoutWorld(this.world, conf.getString("redNexus")));
        this.teamsNexus.put("blue", DKConfig.stringToLocationWithoutWorld(this.world, conf.getString("blueNexus")));
        this.teamsNexus.put("green", DKConfig.stringToLocationWithoutWorld(this.world, conf.getString("greenNexus")));
        this.teamsNexus.put("yellow", DKConfig.stringToLocationWithoutWorld(this.world, conf.getString("yellowNexus")));
        for (Location loc : this.teamsNexus.values()) {
            DKBlockedBlocks.addChunk(loc);
        }
        for (String item : conf.getStringList("bossesSpawns")) {
            this.bossesSpawns.add(DKConfig.stringToLocationWithoutWorld(this.world, item));
        }
        Location l = DKConfig.stringToLocationWithoutWorld(this.world, conf.getString("diamondsPoint"));
        int range = conf.getInt("diamondsRange");
        int minX = l.getBlockX() - range / 2;
        int minY = l.getBlockY() - range / 2;
        int minZ = l.getBlockZ() - range / 2;

        for (int x = minX; x < minX + range; x++) {
            for (int y = minY; y < minY + range; y++) {
                for (int z = minZ; z < minZ + range; z++) {
                    Block b = world.getBlockAt(x, y, z);
                    if (b.getType() == Material.DIAMOND_ORE) {
                        DKBlockedBlocks.addBlock(b.getLocation());
                        this.diamonds.add(b.getLocation());
                        b.setType(Material.STONE);
                    }
                }
            }
        }
    }

    public void blockRadius(Location loc, int range) {
        int minX = loc.getBlockX() - range / 2;
        int minY = loc.getBlockY() - range / 2;
        int minZ = loc.getBlockZ() - range / 2;

        for (int x = minX; x < minX + range; x++) {
            for (int y = minY; y < minY + range; y++) {
                for (int z = minZ; z < minZ + range; z++) {
                    Block b = world.getBlockAt(x, y, z);
                    DKBlockedBlocks.addBlock(b.getLocation());
                }
            }
        }
        DKBlockedBlocks.removeBlock(loc);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public HashMap<String, DKMapTeam> getTeams() {
        return this.mapTeams;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String isNexus(Location loc) {
        if (this.teamsNexus.containsValue(loc)) {
            Iterator it = this.teamsNexus.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                if (((Location) pairs.getValue()).getBlockX() == loc.getBlockX() && ((Location) pairs.getValue()).getBlockY() == loc.getBlockY() && ((Location) pairs.getValue()).getBlockZ() == loc.getBlockZ())
                    return (String) pairs.getKey();
            }
        }
        return null;
    }

    public Location getNexus(DKTeam team) {
        if (this.teamsNexus.containsKey(team.getId())) {
            return this.teamsNexus.get(team.getId());
        }
        return null;
    }

    public void spawnDiamonds() {
        for (Location item : this.diamonds) {
            item.getBlock().setType(Material.DIAMOND_ORE);
            DKBlockedBlocks.removeBlock(item);
        }
    }

    public double getNexus(DKTeam team, Location player) {
        return this.teamsNexus.get(team.getId()).distance(player);
    }

    public void resetWeather() {
        this.getWorld().setStorm(this.storm);
        this.getWorld().setThundering(this.thunder);
        this.getWorld().setThunderDuration(999999);
        this.getWorld().setWeatherDuration(999999);
    }

    public void spawnBosses() {
        for (Location item : this.bossesSpawns) {
            item.getBlock().getChunk().load();
            this.world.spawnEntity(item, EntityType.IRON_GOLEM);
        }
    }
}
