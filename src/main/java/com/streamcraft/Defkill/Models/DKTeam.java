package com.streamcraft.Defkill.Models;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Utils.EconomicSettings;
import com.streamcraft.Defkill.Utils.lobbySigns;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

/**
 * Created by Alex
 * Date: 24.10.13  1:50
 */
public class DKTeam {
    private String id;
    private int idInt;
    private String name;
    private String name2;
    int playersCount = 0;
    private HashMap<String, DKPlayer> players = new HashMap<String, DKPlayer>();
    public int nexusHealth = 100;
    public DKMapTeam mapteam;

    public DKTeam(int idInt, String id, String name, String name2) {
        this.id = id;
        this.idInt = idInt;
        this.name = name;
        this.name2 = name2;
    }

    public void addPlayer(DKPlayer pl) {
        if (!this.players.containsKey(pl.getName())) {
            this.players.put(pl.getName(), pl);
            this.playersCount++;
        }
    }

    public void removePlayer(DKPlayer pl) {
        if (this.players.containsKey(pl.getName())) {
            this.players.remove(pl.getName());
            this.playersCount--;
        }
    }

    public HashMap<String, DKPlayer> getPlayers() {
        return this.players;
    }

    public int getPlayersCount() {
        return this.playersCount;
    }

    public boolean isPlayerInTeam(DKPlayer pl) {
        return this.players.containsKey(pl.getName());
    }

    public String getName(boolean i) {
        if (!i)
            return this.name2;
        else
            return this.name;
    }

    public String getId() {
        return this.id;
    }

    public void sendMessage(String msg) {
        for (Map.Entry<String, DKPlayer> stringDKPlayerEntry : this.players.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKPlayerEntry;
            DKPlayer player = (DKPlayer) pairs.getValue();
            player.sendMessage(msg);
        }
    }

    public void joinPlayersToMap(DKMap map) {
        ArrayList<Location> used = (ArrayList<Location>) map.getTeams().get(this.id).getSpawns().clone();
        for (Map.Entry<String, DKPlayer> stringDKPlayerEntry : this.players.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKPlayerEntry;
            DKPlayer player = (DKPlayer) pairs.getValue();
            for (Location item : used) {
                player.getBukkitModel().teleport(item);
                player.clearInventory();
                player.setAdventureMode();
                player.setDefaultArmor(this);
                player.takeKitStart();
                player.setHealth(20.0);
                used.remove(item);
                break;
            }
        }
    }

    public void reSpawnPlayer(DKPlayer p) {
        ArrayList<Location> used = DefkillPlugin.game.getSelectedMap().getTeams().get(this.id).getSpawns();
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(used.size());
        if (index > 0)
            index--;
        p.getBukkitModel().teleport(used.get(index));
    }

    public Location getReSpawnLocation(DKPlayer p) {
        ArrayList<Location> used = DefkillPlugin.game.getSelectedMap().getTeams().get(this.id).getSpawns();
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(used.size());
        if (index > 0)
            index--;
        return used.get(index);
    }

    public int damageNexus(int damage) {
        int before = this.nexusHealth;
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME && DefkillPlugin.game.getStage() > 1 && this.nexusHealth > 0) {
            for (DKPlayer item : this.players.values()) {
                item.playSound("random.anvil_land");
            }
            if (this.nexusHealth - damage > 0)
                this.nexusHealth = this.nexusHealth - damage;
            else {
                this.nexusHealth = 0;
                DefkillPlugin.game.checkNexuses();
                this.loose();
            }
        }
        lobbySigns.updateSigns();
        return before - this.nexusHealth;
    }

    public void loose() {
        Bukkit.broadcastMessage(ChatColor.GOLD + name + " команда проиграла!");
        for (DKPlayer item : this.players.values()) {
            item.getStats().tillFinish();
        }
    }

    public void win() {
        Bukkit.broadcastMessage(ChatColor.GOLD + name + " команда выиграла!");
        for (DKPlayer item : this.players.values()) {
            item.getStats().tillFinish();
            item.addExp(EconomicSettings.win);
        }
    }

    public ChatColor getChatColor() {
        if (this.id.equals("red"))
            return ChatColor.RED;
        if (this.id.equals("blue"))
            return ChatColor.BLUE;
        if (this.id.equals("green"))
            return ChatColor.GREEN;
        if (this.id.equals("yellow"))
            return ChatColor.YELLOW;
        return null;
    }

    public void winTech() {
        Bukkit.broadcastMessage(ChatColor.GOLD + name + " команда одержала техническую победу!");
        for (DKPlayer item : this.players.values()) {
            item.getStats().tillFinish();
            item.addExp(EconomicSettings.techWin);
        }
    }

    public int getIdInt() {
        return idInt;
    }

    public HashMap<String, DKPlayer> getActivePlayers() {
        HashMap<String, DKPlayer> out = new HashMap<String, DKPlayer>();
        for (Map.Entry<String, DKPlayer> item : getPlayers().entrySet()) {
            if (!item.getValue().isSpectator())
                out.put(item.getKey(), item.getValue());
        }
        return out;
    }
}
