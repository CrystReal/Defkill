package com.streamcraft.Defkill.Models;

import org.bukkit.Material;

import java.util.HashMap;

/**
 * Created by Alex
 * Date: 28.10.13  15:39
 */
public class DKPlayerStats {
    private int exp = 0;
    private int expInGame = 0;
    private int nexusDamage = 0;
    private int deathInGame = 0;
    private int golemInGame = 0;
    private HashMap<Long, DKPlayer> killed = new HashMap<Long, DKPlayer>();
    private long inGameTime = 0;
    private boolean tillFinish = false;
    private HashMap<Material, Integer> minedOres = new HashMap<Material, Integer>();

    public void addTargetKilled(DKPlayer p) {
        killed.put(System.currentTimeMillis() / 1000L, p);
    }

    public HashMap<Long, DKPlayer> getKilled() {
        return this.killed;
    }

    public int getPreGameExp() {
        return this.exp;
    }

    public int getGameExp() {
        return this.expInGame;
    }

    public void addExp(int i) {
        expInGame += i;
    }

    public void removeExp(int i) {
        expInGame -= i;
    }

    public int getGameDeath() {
        return this.deathInGame;
    }

    public void addDeath() {
        this.deathInGame++;
    }

    public int getGameGolem() {
        return this.golemInGame;
    }

    public void addGolem() {
        this.golemInGame++;
    }

    public int getNexusDamage() {
        return nexusDamage;
    }

    public void addNexusDamage(int nexusDamage) {
        this.nexusDamage += nexusDamage;
    }

    public long getInGameTime() {
        return inGameTime;
    }

    public void setInGameTime(long inGameTime) {
        this.inGameTime = inGameTime;
    }

    public void tillFinish() {
        this.tillFinish = true;
    }

    public void addMinedOre(Material m) {
        if (m == Material.LOG || m == Material.IRON_ORE || m == Material.COAL_ORE || m == Material.GOLD_ORE || m == Material.DIAMOND_ORE) {
            int i = 0;
            if (this.minedOres.containsKey(m)) {
                i = this.minedOres.get(m);
                this.minedOres.remove(m);
            }
            i++;
            this.minedOres.put(m, i);
        }
    }

    public int getMinedCount(Material m) {
        if (this.minedOres.containsKey(m)) {
            return this.minedOres.get(m);
        } else {
            return 0;
        }
    }

    public boolean isTillFinish() {
        return tillFinish;
    }
}
