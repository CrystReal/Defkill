package com.streamcraft.Defkill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamcraft.Defkill.Commands.*;
import com.streamcraft.Defkill.DataServerStats.gameStats;
import com.streamcraft.Defkill.DataServerStats.playerStats;
import com.streamcraft.Defkill.DataServerStats.playerStatsKills;
import com.streamcraft.Defkill.DataServerStats.playerStatsMinedOres;
import com.streamcraft.Defkill.Events.*;
import com.streamcraft.Defkill.Models.DKBlockedBlocks;
import com.streamcraft.Defkill.Models.DKMap;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.classes.*;
import com.streamcraft.Defkill.Models.Classes;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Utils.DKConfig;
import com.streamcraft.Defkill.Utils.DKLog;
import com.streamcraft.Defkill.Utils.FileUtils;
import com.updg.CR_API.DataServer.DSUtils;
import com.updg.CR_API.MQ.senderStatsToCenter;
import me.xhawk87.PopupMenuAPI.PopupMenuAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.shininet.bukkit.itemrenamer.api.ItemsListener;
import org.shininet.bukkit.itemrenamer.api.RenamerAPI;
import org.shininet.bukkit.itemrenamer.api.RenamerPriority;
import org.shininet.bukkit.itemrenamer.api.RenamerSnapshot;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;


/**
 * Created by Alex
 * Date: 24.10.13  0:39
 */
public class DefkillPlugin extends JavaPlugin {
    public static DefkillPlugin pl;
    public static Defkill game;
    public static HashMap<String, Command> commands = new HashMap<String, Command>();
    public long autoRefill = 0;
    public static Random rand = new Random();

    public int serverId = 0;
    public long autoRefillDiam;

    public void onEnable() {
        pl = this;
        this.saveDefaultConfig();


        this.serverId = this.getConfig().getInt("serverId");
        DKBlockedBlocks.chunkRadius = this.getConfig().getInt("chunkBlockRadius", 15);

        game = new Defkill();
        this.generateLobby();
        this.generateMaps();
        this.registerEvents();
        this.registerCommands();
        game.startAcceptPlayers();
        this.registerClases();

        this.autoRefill = getConfig().getLong("autoRefill", 100L);
        this.autoRefillDiam = getConfig().getLong("autoRefillDiam", 200L);
        DefkillPlugin.game.ready();
        DefkillPlugin.game.send();
        DKLog.getInstance().l("Plugin enabled");
    }


    public void onDisable() {
        DefkillPlugin.game.setStatus(GameStatus.RELOAD);
        DefkillPlugin.game.send();
        DKLog.getInstance().l("Plugin disabled");
    }

    private void generateLobby() {
        game.setLobby(DKConfig.stringToLocation(getConfig().getString("lobby")));
    }

    private void generateMaps() {
        DKLog.getInstance().l("Loading maps");
        for (String a : getConfig().getConfigurationSection("maps").getKeys(false)) {
            game.addMap(new DKMap(getConfig().getConfigurationSection("maps." + a)));
        }
        DKLog.getInstance().l("Maps loaded");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Bungee(), this);
        getServer().getPluginManager().registerEvents(new JoinQuit(), this);
        getServer().getPluginManager().registerEvents(new Signs(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
        getServer().getPluginManager().registerEvents(new TagApi(), this);
        getServer().getPluginManager().registerEvents(new PopupMenuAPI(), this);

        RenamerAPI.getAPI().addListener(this, RenamerPriority.POST_NORMAL,
                new ItemsListener() {
                    @Override
                    public void onItemsSending(Player player, RenamerSnapshot snapshot) {
                        BlockCleaner.clear(snapshot);
                        BlockCleaner.hideLore(snapshot);
                    }
                });
    }

    private void registerCommands() {
        commands.put("start", new StartCommand());
        commands.put("spectate", new SpectateCommand());
        commands.put("force", new ForceStageCommand());
        commands.put("help", new HelpCommand());
        getCommand("dk").setExecutor(new DKRouter());
    }

    public void registerClases() {
        Classes.classes.put("civilian", new CivilianClass());
        Classes.classes.put("lumberjack", new LumberjackClass());
        Classes.classes.put("miner", new MinerClass());
        Classes.classes.put("archer", new ArcherClass());
        Classes.classes.put("warrior", new WarriorClass());
        Classes.classes.put("healer", new HealerClass());
    }

    public void reloadServer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("Сервер перезагрузится через 15 секунд.");
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage("Сервер перезагрузится через 10 секунд.");
                    }
                    Thread.sleep(5000);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage("Сервер перезагрузится через 5 секунд.");
                    }
                    Thread.sleep(5000);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage("Сервер перезагружается.");
                    }
                    DefkillPlugin.game.setStatus(GameStatus.RELOAD);
                    DefkillPlugin.game.send();
                    sendStats();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public void sendStats() {
        gameStats game = new gameStats();
        game.setServerId(this.serverId);
        game.setWinner(DefkillPlugin.game.winner.getIdInt());
        game.setWinType(DefkillPlugin.game.isTechWin() ? 1 : 0);
        game.setStart(DefkillPlugin.game.timeGameStart);
        game.setEnd(DefkillPlugin.game.timeGameEnd);
        game.setMap(DefkillPlugin.game.getSelectedMap().getId());
        List<playerStats> players = new ArrayList<playerStats>();
        List<playerStatsKills> tmpKillsA;
        playerStats tmpPlayer;
        playerStatsKills tmpKills;
        playerStatsMinedOres tmpMined;
        for (DKPlayer p : DefkillPlugin.game.getActivePlayersArray()) {
            tmpPlayer = new playerStats();
            tmpPlayer.setPlayerId(p.getId());
            tmpPlayer.setNexusDamage(p.getStats().getNexusDamage());
            tmpPlayer.setKilledGolems(p.getStats().getGameGolem());
            tmpPlayer.setDeaths(p.getStats().getGameDeath());
            tmpPlayer.setTimeInGame(p.getStats().getInGameTime());
            tmpPlayer.setTillFinish(p.getStats().isTillFinish());
            if (p.team == null)
                tmpPlayer.setTeam(0);
            else tmpPlayer.setTeam(p.team.getIdInt());
            tmpKillsA = new ArrayList<playerStatsKills>();
            for (Map.Entry entry : p.getStats().getKilled().entrySet()) {
                tmpKills = new playerStatsKills();
                tmpKills.setTime((Long) entry.getKey());
                tmpKills.setVictim(((DKPlayer) entry.getValue()).getId());
                tmpKillsA.add(tmpKills);
            }
            tmpPlayer.setVictims(tmpKillsA);
            tmpMined = new playerStatsMinedOres();
            tmpMined.setCoal(p.getStats().getMinedCount(Material.COAL_ORE));
            tmpMined.setIron(p.getStats().getMinedCount(Material.IRON_ORE));
            tmpMined.setWood(p.getStats().getMinedCount(Material.LOG));
            tmpMined.setGold(p.getStats().getMinedCount(Material.GOLD_ORE));
            tmpMined.setDiamonds(p.getStats().getMinedCount(Material.DIAMOND_ORE));
            tmpPlayer.setMinedOres(tmpMined);
            players.add(tmpPlayer);
        }
        for (DKPlayer p : DefkillPlugin.game.getLeavedPlayersArray().values()) {
            tmpPlayer = new playerStats();
            tmpPlayer.setPlayerId(p.getId());
            tmpPlayer.setNexusDamage(p.getStats().getNexusDamage());
            tmpPlayer.setKilledGolems(p.getStats().getGameGolem());
            tmpPlayer.setDeaths(p.getStats().getGameDeath());
            tmpPlayer.setTimeInGame(p.getStats().getInGameTime());
            tmpPlayer.setTillFinish(p.getStats().isTillFinish());
            if (p.team == null)
                tmpPlayer.setTeam(0);
            else tmpPlayer.setTeam(p.team.getIdInt());
            tmpKillsA = new ArrayList<playerStatsKills>();
            for (Map.Entry entry : p.getStats().getKilled().entrySet()) {
                tmpKills = new playerStatsKills();
                tmpKills.setTime((Long) entry.getKey());
                tmpKills.setVictim(((DKPlayer) entry.getValue()).getId());
                tmpKillsA.add(tmpKills);
            }
            tmpPlayer.setVictims(tmpKillsA);
            tmpMined = new playerStatsMinedOres();
            tmpMined.setCoal(p.getStats().getMinedCount(Material.COAL_ORE));
            tmpMined.setIron(p.getStats().getMinedCount(Material.IRON_ORE));
            tmpMined.setWood(p.getStats().getMinedCount(Material.LOG));
            tmpMined.setGold(p.getStats().getMinedCount(Material.GOLD_ORE));
            tmpMined.setDiamonds(p.getStats().getMinedCount(Material.DIAMOND_ORE));
            tmpPlayer.setMinedOres(tmpMined);
            players.add(tmpPlayer);
        }
        for (DKPlayer p : DefkillPlugin.game.getSpectators().values()) {
            tmpPlayer = new playerStats();
            tmpPlayer.setPlayerId(p.getId());
            tmpPlayer.setNexusDamage(p.getStats().getNexusDamage());
            tmpPlayer.setKilledGolems(p.getStats().getGameGolem());
            tmpPlayer.setDeaths(p.getStats().getGameDeath());
            tmpPlayer.setTimeInGame(p.getStats().getInGameTime());
            tmpPlayer.setTillFinish(p.getStats().isTillFinish());
            if (p.team == null)
                tmpPlayer.setTeam(0);
            else tmpPlayer.setTeam(p.team.getIdInt());
            tmpKillsA = new ArrayList<playerStatsKills>();
            for (Map.Entry entry : p.getStats().getKilled().entrySet()) {
                tmpKills = new playerStatsKills();
                tmpKills.setTime((Long) entry.getKey());
                tmpKills.setVictim(((DKPlayer) entry.getValue()).getId());
                tmpKillsA.add(tmpKills);
            }
            tmpPlayer.setVictims(tmpKillsA);
            tmpMined = new playerStatsMinedOres();
            tmpMined.setCoal(p.getStats().getMinedCount(Material.COAL_ORE));
            tmpMined.setIron(p.getStats().getMinedCount(Material.IRON_ORE));
            tmpMined.setWood(p.getStats().getMinedCount(Material.LOG));
            tmpMined.setGold(p.getStats().getMinedCount(Material.GOLD_ORE));
            tmpMined.setDiamonds(p.getStats().getMinedCount(Material.DIAMOND_ORE));
            tmpPlayer.setMinedOres(tmpMined);
            players.add(tmpPlayer);
        }
        game.setPlayers(players);
        try {
            String stat = new ObjectMapper().writeValueAsString(game);
            senderStatsToCenter.send("defkill", stat);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
