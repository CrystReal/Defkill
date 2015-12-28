package com.streamcraft.Defkill.Events;

import com.streamcraft.Defkill.Defkill;
import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Utils.DKScoreboard;
import com.streamcraft.Defkill.Utils.lobbySigns;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Alex
 * Date: 24.10.13  2:21
 */
public class JoinQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Player user = event.getPlayer();
        final boolean isNew = DefkillPlugin.game.addPlayer(user);
        final DKPlayer p = DefkillPlugin.game.getPlayer(user.getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        final DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        DefkillPlugin.game.afterLogin(p);
        DKScoreboard.updateSidebarScoreboard(DefkillPlugin.game.getPlayer(event.getPlayer().getName()));
        DefkillPlugin.game.refreshTimer();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player user = event.getPlayer();
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME)
            DefkillPlugin.game.kickPlayer(user);
        else
            DefkillPlugin.game.removePlayer(user);
        DefkillPlugin.game.refreshTimer();
        lobbySigns.updateSigns();
        event.setQuitMessage(null);
    }
}
