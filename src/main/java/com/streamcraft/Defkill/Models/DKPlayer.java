package com.streamcraft.Defkill.Models;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.injector.PacketConstructor;
import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.classes.CivilianClass;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Models.interfaces.DKClass;
import com.streamcraft.Defkill.Utils.ColorizeArmor;
import com.streamcraft.Defkill.Utils.DKScoreboard;
import com.streamcraft.Defkill.Utils.L;
import com.updg.CR_API.Bungee.Bungee;
import com.updg.CR_API.DataServer.DSUtils;
import net.minecraft.server.v1_7_R2.PacketPlayOutNamedSoundEffect;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.kitteh.tag.TagAPI;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 24.10.13  0:47
 */
public class DKPlayer {
    private int id = 0;
    private String name;
    private Player bukkitModel;
    private DKClass cls;
    public DKTeam team;
    private double realMoney = 0;
    private DKPlayerStats stats;
    private boolean fallProtect;

    private boolean spectator = false;
    private double exp = 0;

    private int multiply = 1;

    public DKPlayer(Player pl) {
        this.setBukkitModel(pl);
        this.setName(pl.getName());
        this.cls = new CivilianClass();
        this.stats = new DKPlayerStats();
        String[] out = DSUtils.getExpAndMoney(pl);
        this.setExp(Double.parseDouble(out[0]));
        this.setRealMoney(Double.parseDouble(out[1]));
    }

    public String getName() {
        return name.toLowerCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public DKClass getCls() {
        return cls;
    }

    public void setCls(DKClass cls) {
        this.cls = cls;
        DKScoreboard.updateSidebarScoreboard(this);
        DKScoreboard.updateSubTitle(this);
        updateNickColorAndClass();
        this.clearInventory();
        this.setDefaultArmor(team);
        this.takeKitStart();
    }

    public Player getBukkitModel() {
        return bukkitModel;
    }

    public void setBukkitModel(Player bukkitModel) {
        this.bukkitModel = bukkitModel;
    }

    public void sendMessage(String msg) {
        bukkitModel.sendMessage(msg);
    }

    public void setAdventureMode() {
        this.bukkitModel.setGameMode(GameMode.ADVENTURE);
    }

    public void setHealth(double health) {
        this.bukkitModel.setHealth(health);
    }

    public void takeKitStart() {
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME && !this.isSpectator()) {
            this.getBukkitModel().setCompassTarget(DefkillPlugin.game.getSelectedMap().getNexus(this.team));
            for (ItemStack item : this.cls.getStartKit())
                this.bukkitModel.getInventory().addItem(item);
        } else {
            for (ItemStack item : this.getLobbyKit())
                this.bukkitModel.getInventory().addItem(item);
        }
    }

    public void setDefaultArmor() {
        ItemStack chestplate = ColorizeArmor.c(new ItemStack(Material.LEATHER_CHESTPLATE, 1), team);
        this.getBukkitModel().getInventory().setChestplate(chestplate);
        ItemStack boots = ColorizeArmor.c(new ItemStack(Material.LEATHER_BOOTS, 1), team);
        this.getBukkitModel().getInventory().setBoots(boots);
        ItemStack helmet = ColorizeArmor.c(new ItemStack(Material.LEATHER_HELMET, 1), team);
        this.getBukkitModel().getInventory().setHelmet(helmet);
        ItemStack leggins = ColorizeArmor.c(new ItemStack(Material.LEATHER_LEGGINGS, 1), team);
        this.getBukkitModel().getInventory().setLeggings(leggins);
    }

    public void setDefaultArmor(DKTeam team) {
        ItemStack chestplate = ColorizeArmor.c(new ItemStack(Material.LEATHER_CHESTPLATE, 1), team);
        this.getBukkitModel().getInventory().setChestplate(chestplate);
        ItemStack boots = ColorizeArmor.c(new ItemStack(Material.LEATHER_BOOTS, 1), team);
        this.getBukkitModel().getInventory().setBoots(boots);
        ItemStack helmet = ColorizeArmor.c(new ItemStack(Material.LEATHER_HELMET, 1), team);
        this.getBukkitModel().getInventory().setHelmet(helmet);
        ItemStack leggins = ColorizeArmor.c(new ItemStack(Material.LEATHER_LEGGINGS, 1), team);
        this.getBukkitModel().getInventory().setLeggings(leggins);
    }

    public void clearInventory() {
        this.getBukkitModel().setFireTicks(0);
        this.bukkitModel.closeInventory();
        this.bukkitModel.setHealth(this.cls.getStartHealth());
        this.bukkitModel.setExp(this.cls.getStartExp());
        this.bukkitModel.setFoodLevel(20);
        this.bukkitModel.getInventory().clear();
        this.bukkitModel.getInventory().setHelmet(null);
        this.bukkitModel.getInventory().setChestplate(null);
        this.bukkitModel.getInventory().setLeggings(null);
        this.bukkitModel.getInventory().setBoots(null);
    }

    public boolean isLobbyRespawn() {
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
            DKTeam team = DefkillPlugin.game.getPlayerTeam(this);
            if (team.nexusHealth > 0)
                return false;
            else
                return true;
        } else
            return true;
    }

    public void reSpawn() {
        this.getBukkitModel().teleport(getReSpawnLocation());
    }

    public Location getReSpawnLocation() {
        if ((this.team != null || !this.isSpectator()) && (DefkillPlugin.game.getStatus() == GameStatus.INGAME || DefkillPlugin.game.getStatus() == GameStatus.POSTGAME)) {
            DKTeam team = DefkillPlugin.game.getPlayerTeam(this);
            if (team.nexusHealth != 0)
                return DefkillPlugin.game.getPlayerTeam(this).getReSpawnLocation(this);
            else {
                DefkillPlugin.game.addSpectator(this);
                this.setSpectator(true);
                return DefkillPlugin.game.getSelectedMap().spectatorsSpawn;
            }
        } else
            return DefkillPlugin.game.getLobby();
    }

    public void updateNickColorAndClass() {
        this.getBukkitModel().setDisplayName(team.getChatColor() + this.getName() + " (" + this.cls.getName() + ")" + ChatColor.RESET + "");
        TagAPI.refreshPlayer(this.getBukkitModel());
    }

    public double getRealMoney() {
        String[] out = DSUtils.getExpAndMoney(this.getBukkitModel());
        this.setExp(Double.parseDouble(out[0]));
        this.setRealMoney(Double.parseDouble(out[1]));
        return realMoney;
    }

    public void setRealMoney(double realMoney) {
        this.realMoney = realMoney;
    }

    public DKPlayerStats getStats() {
        return stats;
    }

    public ArrayList<ItemStack> getLobbyKit() {
        return new ArrayList<ItemStack>();
    }

    public void playSound(String sound) {
        Player p = this.getBukkitModel();
        PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ(), 1.0F, 1.0F);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public void addFallProtection() {
        this.fallProtect = true;
    }

    public void removeFallProtection() {
        this.fallProtect = false;
    }

    public boolean isFallPtotected() {
        return this.fallProtect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator) {
        if (spectator)
            this.getBukkitModel().setGameMode(GameMode.CREATIVE);
        else
            this.getBukkitModel().setGameMode(GameMode.SURVIVAL);
        this.spectator = spectator;
    }

    public void hideSpectators() {
        for (DKPlayer player : DefkillPlugin.game.spectators.values()) {
            this.getBukkitModel().hidePlayer(player.getBukkitModel());
        }
    }

    public double getExp() {
        String[] out = DSUtils.getExpAndMoney(this.getBukkitModel());
        this.setExp(Double.parseDouble(out[0]));
        this.setRealMoney(Double.parseDouble(out[1]));
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public void withdrawExp(double v) {
        String[] out = DSUtils.withdrawPlayerExpAndMoney(this.getBukkitModel(), v, 0);
        this.setExp(Double.parseDouble(out[0]));
    }

    public void withdrawMoney(double v) {
        String[] out = DSUtils.withdrawPlayerExpAndMoney(this.getBukkitModel(), 0, v);
        this.setRealMoney(Double.parseDouble(out[1]));
    }

    public void addExp(double v) {
        String[] out = DSUtils.addPlayerExpAndMoney(this.getBukkitModel(), v, 0);
        this.setExp(Double.parseDouble(out[0]));
    }

    public void addMoney(double v) {
        String[] out = DSUtils.addPlayerExpAndMoney(this.getBukkitModel(), 0, v);
        this.setRealMoney(Double.parseDouble(out[1]));
    }

    public int getMultiply() {
        return this.multiply;
    }

    public void setMultiply(int m) {
        this.multiply = m;
    }

}
