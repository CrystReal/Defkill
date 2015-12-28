package com.streamcraft.Defkill;

import com.streamcraft.Defkill.Models.DKMap;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.DKTeam;
import com.streamcraft.Defkill.Models.Stages.*;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Models.interfaces.legendaryItem;
import com.streamcraft.Defkill.Models.legendaryItems.legendarySwordConfusion;
import com.streamcraft.Defkill.Utils.*;
import com.streamcraft.Defkill.threads.HealerThread;
import com.streamcraft.Defkill.threads.TopBarThread;
import com.updg.CR_API.MQ.senderUpdatesToCenter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Alex
 * Date: 24.10.13  1:18
 */
public class Defkill {
    private GameStatus status = GameStatus.RELOAD;
    private int stage = 0;

    private Location lobby;
    private DKMap chosenMap = null;
    private ArrayList<DKMap> maps = new ArrayList<DKMap>();

    private HashMap<String, DKPlayer> allPlayers = new HashMap<String, DKPlayer>();
    public HashMap<String, DKPlayer> spectators = new HashMap<String, DKPlayer>();
    private HashMap<String, DKPlayer> leavedPlayers = new HashMap<String, DKPlayer>();
    private HashMap<String, DKTeam> teams = new HashMap<String, DKTeam>();

    public ArrayList<legendaryItem> legendaryItems = new ArrayList<legendaryItem>();


    private int middleCountPlayers = 0;
    public int minCountPlayersToGame = 0;
    public int maxPlayers = 0;
    public int tillGame = 120;
    public int tillGameShedule = 0;
    public int tillNextStage = 600;
    public DKTeam winner;

    public long timeGameStart = 0;
    public long timeGameEnd = 0;
    private boolean techWin = false;
    private int stageTask = 0;

    public Location redLobbySign;
    public Location blueLobbySign;
    public Location greenLobbySign;
    public Location yellowLobbySign;

    public Defkill() {
        FileConfiguration c = DefkillPlugin.pl.getConfig();
        this.maxPlayers = c.getInt("maxPlayers", 100);
        this.minCountPlayersToGame = c.getInt("minPlayers");
        this.redLobbySign = DKConfig.stringToLocation(c.getString("lobbySigns.red"));
        this.blueLobbySign = DKConfig.stringToLocation(c.getString("lobbySigns.blue"));
        this.greenLobbySign = DKConfig.stringToLocation(c.getString("lobbySigns.green"));
        this.yellowLobbySign = DKConfig.stringToLocation(c.getString("lobbySigns.yellow"));
        this.teams.put("red", new DKTeam(1, "red", "Красная", "Красной"));
        this.teams.put("blue", new DKTeam(2, "blue", "Синяя", "Синей"));
        this.teams.put("green", new DKTeam(3, "green", "Зеленая", "Зеленой"));
        this.teams.put("yellow", new DKTeam(4, "yellow", "Желтая", "Желтой"));
        this.addLegendaryItems();
    }

    public void send() {
        String s = GameStatus.WAITING.toString();
        if (DefkillPlugin.game.maxPlayers <= DefkillPlugin.game.getActivePlayers())
            s = "IN_GAME";
        if (DefkillPlugin.game.getStatus() == GameStatus.WAITING) {
            if (DefkillPlugin.game.tillGame < 120)
                senderUpdatesToCenter.send(DefkillPlugin.pl.serverId + ":" + s + ":" + "В ОЖИДАНИИ" + ":" + DefkillPlugin.game.getActivePlayers() + ":" + DefkillPlugin.game.maxPlayers + ":До игры " + DefkillPlugin.game.tillGame + " c.");
            else
                senderUpdatesToCenter.send(DefkillPlugin.pl.serverId + ":" + s + ":" + "В ОЖИДАНИИ" + ":" + DefkillPlugin.game.getActivePlayers() + ":" + DefkillPlugin.game.maxPlayers + ":Набор игроков");
        } else if (DefkillPlugin.game.getStatus() == GameStatus.PRE_GAME)
            senderUpdatesToCenter.send(DefkillPlugin.pl.serverId + ":" + s + ":" + "НАЧАЛО" + ":" + DefkillPlugin.game.getActivePlayers() + ":" + DefkillPlugin.game.maxPlayers);
        else if (DefkillPlugin.game.getStatus() == GameStatus.POSTGAME) {
            senderUpdatesToCenter.send(DefkillPlugin.pl.serverId + ":IN_GAME:" + "ИГРА ОКОНЧЕНА" + ":" + DefkillPlugin.game.getActivePlayers() + ":" + DefkillPlugin.game.maxPlayers + ":Победила " + DefkillPlugin.game.winner.getChatColor() + DefkillPlugin.game.winner.getName(true) + ChatColor.RESET + " команда");
        } else if (DefkillPlugin.game.getStatus() == GameStatus.INGAME && DefkillPlugin.game.getStage() < 3)
            senderUpdatesToCenter.send(DefkillPlugin.pl.serverId + ":" + GameStatus.WAITING.toString() + ":" + "ИГРА" + ":" + DefkillPlugin.game.getActivePlayers() + ":" + DefkillPlugin.game.maxPlayers + ":Стадия " + DefkillPlugin.game.getStage());
        else if (DefkillPlugin.game.getStatus() == GameStatus.INGAME || DefkillPlugin.game.getStatus() == GameStatus.POSTGAME)
            senderUpdatesToCenter.send(DefkillPlugin.pl.serverId + ":IN_GAME:" + "ИГРА" + ":" + DefkillPlugin.game.getActivePlayers() + ":" + DefkillPlugin.game.maxPlayers + ":Стадия " + DefkillPlugin.game.getStage());
        else if (DefkillPlugin.game.getStatus() == GameStatus.RELOAD)
            senderUpdatesToCenter.send(DefkillPlugin.pl.serverId + ":DISABLED:" + "ОФФЛАЙН" + ":0:0:");

    }

    private void addLegendaryItems() {
        this.legendaryItems.add(new legendarySwordConfusion());
    }


    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public DKMap getMap(int name) {
        return maps.get(name);
    }

    public void addMap(DKMap map) {
        DKLog.getInstance().l("Try to add new map " + map.getName());
        if (!maps.contains(map)) {
            maps.add(map);
        }
    }

    public void removeMap(int name) {
        maps.remove(name);
    }

    public DKTeam getTeam(String name) {
        if (teams.containsKey(name))
            return teams.get(name);
        else
            return null;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public int getStage() {
        return stage;
    }

    public boolean addPlayerToTeam(DKPlayer player, String Team) {
        if (this.status == GameStatus.WAITING || this.status == GameStatus.PRE_GAME) {
            if (this.teams.get(Team).isPlayerInTeam(player)) {
                player.sendMessage(ChatColor.RED + "Ты уже в " + this.teams.get(Team).getName(false).toLowerCase() + " командe.");
                return false;
            }
            if (this.middleCountPlayers >= this.teams.get(Team).getPlayersCount()) {
                this.removePlayerFromAllTeams(player);
                this.teams.get(Team).addPlayer(player);
                this.middleCountPlayers = (this.teams.get("red").getPlayersCount() + this.teams.get("blue").getPlayersCount() + this.teams.get("green").getPlayersCount() + this.teams.get("yellow").getPlayersCount()) / 4;
                player.team = this.teams.get(Team);
                player.updateNickColorAndClass();
                player.sendMessage(ChatColor.GREEN + "Добро пожаловать в " + this.teams.get(Team).getName(true).toLowerCase() + " команду!");
            } else {
                player.sendMessage(ChatColor.RED + "В " + this.teams.get(Team).getName(true).toLowerCase() + " команде нет мест.");
                return false;
            }
            DefkillPlugin.game.refreshTimer();
        } else {
            if (this.status == GameStatus.INGAME && this.stage < 3) {
                if (this.middleCountPlayers >= this.teams.get(Team).getPlayersCount()) {
                    this.removePlayerFromAllTeams(player);
                    this.teams.get(Team).addPlayer(player);
                    this.middleCountPlayers = (this.teams.get("red").getPlayersCount() + this.teams.get("blue").getPlayersCount() + this.teams.get("green").getPlayersCount() + this.teams.get("yellow").getPlayersCount()) / 4;
                    player.team = this.teams.get(Team);
                    player.updateNickColorAndClass();
                    player.sendMessage(ChatColor.GREEN + "Добро пожаловать в " + this.teams.get(Team).getName(true).toLowerCase() + " команду!");
                } else {
                    player.sendMessage(ChatColor.RED + "В " + this.teams.get(Team).getName(true).toLowerCase() + " команде нет мест.");
                    return false;
                }
                player.reSpawn();
                player.clearInventory();
                player.takeKitStart();
            }
        }
        return false;
    }

    public void removePlayerFromAllTeams(DKPlayer name) {
        for (Map.Entry<String, DKTeam> stringDKTeamEntry : this.teams.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKTeamEntry;
            DKTeam team = (DKTeam) pairs.getValue();
            team.removePlayer(name);
        }
    }

    public boolean isPlayerInAnyTeam(DKPlayer name) {
        for (Map.Entry<String, DKTeam> stringDKTeamEntry : this.teams.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKTeamEntry;
            DKTeam team = (DKTeam) pairs.getValue();
            if (team.isPlayerInTeam(name))
                return true;
        }
        return false;
    }

    public boolean addPlayer(Player pl) {
        if (!this.allPlayers.containsKey(pl.getName().toLowerCase())) {
            if (this.leavedPlayers.containsKey(pl.getName().toLowerCase())) {

                return false;
            } else {
                DKPlayer player = new DKPlayer(pl);
                this.allPlayers.put(pl.getName().toLowerCase(), player);
                send();
                return true;
            }
        }
        return false;
    }

    public void afterLogin(DKPlayer player) {
        if (this.leavedPlayers.containsKey(player.getBukkitModel().getName())) {
            if (this.getStatus() == GameStatus.INGAME) {
                this.allPlayers.put(this.leavedPlayers.get(player.getName()).getName(), this.leavedPlayers.get(player.getName()));
                this.leavedPlayers.remove(player.getName());
                this.allPlayers.get(player.getName()).setDefaultArmor();
                this.allPlayers.get(player.getName()).reSpawn();
            } else {
                player.getBukkitModel().kickPlayer("Игра уже закончилась.");
            }
        } else {
            player.getBukkitModel().setGameMode(GameMode.ADVENTURE);
            if ((this.getStatus() == GameStatus.INGAME && this.stage > 2) || this.getStatus() == GameStatus.POSTGAME) {
                player.getBukkitModel().teleport(this.getSelectedMap().spectatorsSpawn);
                this.spectators.put(player.getBukkitModel().getName(), player);
                player.setSpectator(true);
            } else
                player.getBukkitModel().teleport(this.lobby);
            player.clearInventory();
            player.takeKitStart();
            player.hideSpectators();
            player.sendMessage(ChatColor.DARK_RED + "============== " + ChatColor.WHITE + "DefKill " + ChatColor.DARK_RED + "==============");
            player.sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.WHITE + "Добро пожаловать на DefKill " + ChatColor.DARK_RED + "====");
            player.sendMessage(ChatColor.DARK_RED + "= " + ChatColor.WHITE + "Выбери команду ударив по табличке " + ChatColor.DARK_RED + "=");
            player.sendMessage(ChatColor.DARK_RED + "=====================================");
            player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Игра начнется когда на сервере будет минимум " + this.minCountPlayersToGame + " " + StringUtil.plural(this.minCountPlayersToGame, "игрок", "игрока", "игроков") + ".");

        }
    }

    public void removePlayer(Player pl) {
        if (this.allPlayers.containsKey(pl.getName().toLowerCase())) {
            DKPlayer p = this.allPlayers.get(pl.getName().toLowerCase());
            this.removePlayerFromAllTeams(p);
            if (this.status == GameStatus.INGAME) {
                if (!p.isSpectator()) {
                    p.getStats().setInGameTime(System.currentTimeMillis() / 1000L - this.timeGameStart);
                    this.leavedPlayers.put(p.getName(), p);
                    this.allPlayers.remove(pl.getName().toLowerCase());
                }
            } else if (this.getStatus() == GameStatus.PRE_GAME || this.getStatus() == GameStatus.WAITING) {
                this.allPlayers.remove(pl.getName().toLowerCase());
            }
            this.removeSpectator(p);
        }
        send();
    }

    public DKPlayer getPlayer(String name) {
        if (this.allPlayers.containsKey(name.toLowerCase())) {
            return this.allPlayers.get(name.toLowerCase());
        }
        DefkillPlugin.pl.getLogger().log(Level.INFO, "Как так? Игрок не найден!");
        return null;
    }

    public void kickPlayer(Player pl) {
        if (this.allPlayers.containsKey(pl.getName()) && this.isPlayerInAnyTeam(this.allPlayers.get(pl.getName()))) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Игрок " + pl.getName() + " покинул сервер.");
            if (this.getStatus() == GameStatus.INGAME && this.getPlayerTeam(this.getPlayer(pl.getName())).nexusHealth != 0)
                this.removePlayer(pl);
            this.checkTeams();
        }
    }

    public boolean isAvailableToStart() {
        return this.getActivePlayers() >= this.minCountPlayersToGame;
        //return (this.teams.get("red").getPlayersCount() + this.teams.get("blue").getPlayersCount() + this.teams.get("green").getPlayersCount() + this.teams.get("yellow").getPlayersCount() >= this.minCountPlayersToGame);
    }

    public void hardStart() {
        if (this.tillGameShedule == 0 && (this.status == GameStatus.WAITING || this.status == GameStatus.PRE_GAME))
            this.tillGameShedule = Bukkit.getScheduler().scheduleSyncRepeatingTask(DefkillPlugin.pl, new Runnable() {
                public void run() {
                    updateTimer();
                }
            }, 0, 120);
    }

    public void prepareGame() {
        this.status = GameStatus.PRE_GAME;
        DKLog.getInstance().l("Game change status to PRE GAME!");
        Bukkit.broadcastMessage(ChatColor.AQUA + "Игра начнется через 15 секунд. Последний шанс выбрать команды.");
    }

    public void startGame() {
        this.checkPlayersAndSetToCommands();
        this.status = GameStatus.INGAME;
        this.timeGameStart = System.currentTimeMillis() / 1000L;
        send();
        this.chosenMap = this.maps.get(0);
        DKLog.getInstance().l("Game change status to IN GAME!");
        this.chosenMap.resetWeather();
        this.hideSpectators();
        this.spawnSpectators();
        this.joinPlayersToMap();
        this.upStage();
        this.sheduleStageUpper();
        for (DKTeam item : this.teams.values()) {
            item.mapteam = this.chosenMap.getTeams().get(item.getId());
        }
        for (DKPlayer item : DefkillPlugin.game.getPlayers().values()) {
            item.getBukkitModel().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            DKScoreboard.updateSidebarScoreboard(item);
            DKScoreboard.updateSubTitle(item);
        }
    }

    private void spawnSpectators() {
        for (DKPlayer p : spectators.values()) {
            p.getBukkitModel().teleport(this.getSelectedMap().spectatorsSpawn);
        }
    }

    public void upStage() {
        if (this.stage == 5)
            return;
        this.stage++;
        this.tillNextStage = 600;
        switch (this.stage) {
            case 1:
                new Stage1().onStart();
                break;
            case 2:
                new Stage1().onEnd();
                new Stage2().onStart();
                break;
            case 3:
                new Stage2().onEnd();
                new Stage3().onStart();
                break;
            case 4:
                new Stage3().onEnd();
                new Stage4().onStart();
                break;
            case 5:
                new Stage4().onEnd();
                new Stage5().onStart();
                break;
        }
        send();
        for (DKPlayer item : DefkillPlugin.game.getPlayers().values()) {
            if (stage > 1)
                item.playSound("portal.trigger");

            DKScoreboard.updateSidebarScoreboard(item);
        }
    }

    public void sheduleStageUpper() {
        this.stageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(DefkillPlugin.pl, new Runnable() {
            public void run() {
                if (stage != 5) {
                    tillNextStage--;
                    if (tillNextStage == 0)
                        upStage();
                }
            }
        }, 0, 20);
    }

    public void hideSpectators() {
        for (DKPlayer player : this.spectators.values()) {
            for (DKPlayer player_r : this.allPlayers.values()) {
                player_r.getBukkitModel().hidePlayer(player.getBukkitModel());
            }
        }
    }

    public void unhideSpectator(DKPlayer p) {
        for (DKPlayer player_r : this.allPlayers.values()) {
            player_r.getBukkitModel().hidePlayer(p.getBukkitModel());
        }
    }

    public void addSpectator(DKPlayer p) {
        if (!this.spectators.containsKey(p.getName())) {
            this.spectators.put(p.getName(), p);
            this.hideSpectators();
        }
        send();
    }

    public void removeSpectator(DKPlayer p) {
        if (this.spectators.containsKey(p.getName().toLowerCase())) {
            this.spectators.remove(p.getName().toLowerCase());
            this.unhideSpectator(p);
        }
        send();
    }

    public void checkPlayersAndSetToCommands() {
        HashMap<String, DKPlayer> notInTeam = new HashMap<String, DKPlayer>();
        for (DKPlayer player : this.allPlayers.values()) {
            if (!player.isSpectator() && !this.isPlayerInAnyTeam(player)) {
                notInTeam.put(player.getName().toLowerCase(), player);
            }
        }
        DKTeamSorterer.sort(notInTeam, teams);
    }

    public void joinPlayersToMap() {
        for (Map.Entry<String, DKTeam> stringDKTeamEntry : this.teams.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKTeamEntry;
            DKTeam team = (DKTeam) pairs.getValue();
            team.joinPlayersToMap(this.chosenMap);
        }
    }

    public void startAcceptPlayers() {
        this.status = GameStatus.WAITING;
    }

    public DKTeam getPlayerTeam(DKPlayer p) {
        return p.team;
    }

    public DKMap getSelectedMap() {
        return chosenMap;
    }

    public void checkNexuses() {
        int loose = 0;
        DKTeam win = null;
        for (DKTeam item : this.teams.values()) {
            if (item.nexusHealth == 0)
                loose++;
            else
                win = item;
        }
        if (win != null && loose == 3) {
            this.doWin(win);
        }
    }

    public void checkTeams() {
        int loose = 0;
        DKTeam win = null;
        for (DKTeam item : this.teams.values()) {
            if (item.getActivePlayers().size() == 0) {
                loose++;
            } else
                win = item;
        }
        if (win != null && loose == 3) {
            this.doTechWin(win);
        }
    }

    public HashMap<String, DKTeam> getTeams() {
        return teams;
    }

    public HashMap<String, DKPlayer> getPlayers() {
        return this.allPlayers;
    }

    public void refreshTimer() {
        if (this.status != GameStatus.INGAME && this.status != GameStatus.RELOAD) {
            if (isAvailableToStart() && this.tillGame == 120 && this.tillGameShedule == 0) {
                this.tillGameShedule = Bukkit.getScheduler().scheduleSyncRepeatingTask(DefkillPlugin.pl, new Runnable() {
                    public void run() {
                        updateTimer();
                    }
                }, 0, 20);
            }
        }
    }

    private void updateTimer() {
        if (isAvailableToStart()) {
            this.tillGame--;
            if (this.tillGame == 0) {
                Bukkit.getScheduler().cancelTask(this.tillGameShedule);
                for (DKPlayer item : this.allPlayers.values()) {
                    item.playSound("random.levelup");
                }
                this.startGame();
            }
            if (this.tillGame == 15)
                this.prepareGame();
            if (this.tillGame <= 15 && this.tillGame > 0) {
                for (DKPlayer item : this.allPlayers.values()) {
                    item.playSound("random.orb");
                }
            }
            if (this.tillGame == 119) {
                Bukkit.broadcastMessage("Игра начнется через 120 секунд");
            }
            send();
            for (DKPlayer item : this.allPlayers.values()) {
                DKScoreboard.updateSidebarScoreboard(item);
            }
        } else {
            Bukkit.getScheduler().cancelTask(this.tillGameShedule);
            this.tillGameShedule = 0;
            this.tillGame = 120;
            this.status = GameStatus.WAITING;
            send();
            Bukkit.broadcastMessage("Отмена старта. Не достаточно игроков.");
            for (DKPlayer item : this.allPlayers.values()) {
                DKScoreboard.updateSidebarScoreboard(item);
            }
        }
    }

    public void ready() {
        new HealerThread().start();
        new TopBarThread().start();
        lobbySigns.updateSigns();
        this.status = GameStatus.WAITING;
    }

    public void doWin(DKTeam team) {
        Bukkit.getScheduler().cancelTask(this.stageTask);
        for (DKPlayer item : allPlayers.values()) {
            item.getStats().setInGameTime(System.currentTimeMillis() / 1000L - this.timeGameStart);
        }
        team.win();
        this.status = GameStatus.POSTGAME;
        this.winner = team;
        this.timeGameEnd = System.currentTimeMillis() / 1000L;
        send();
        DefkillPlugin.pl.reloadServer();
    }

    private void doTechWin(DKTeam team) {
        Bukkit.getScheduler().cancelTask(this.stageTask);
        for (DKPlayer item : allPlayers.values()) {
            item.getStats().setInGameTime(System.currentTimeMillis() / 1000L - this.timeGameStart);
        }
        team.winTech();
        this.status = GameStatus.POSTGAME;
        this.winner = team;
        this.techWin = true;
        this.timeGameEnd = System.currentTimeMillis() / 1000L;
        send();
        DefkillPlugin.pl.reloadServer();
    }

    public int getActivePlayers() {
        int i = 0;
        for (DKPlayer p : this.allPlayers.values()) {
            if (!p.isSpectator()) {
                i++;
            }
        }
        return i;
    }

    public ArrayList<DKPlayer> getActivePlayersArray() {
        ArrayList<DKPlayer> out = new ArrayList<DKPlayer>();
        for (DKPlayer p : this.allPlayers.values()) {
            if (!p.isSpectator()) {
                out.add(p);
            }
        }
        return out;
    }

    public HashMap<String, DKPlayer> getSpectators() {
        return spectators;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public boolean isTechWin() {
        return techWin;
    }

    public HashMap<String, DKPlayer> getLeavedPlayersArray() {
        return leavedPlayers;
    }
}
