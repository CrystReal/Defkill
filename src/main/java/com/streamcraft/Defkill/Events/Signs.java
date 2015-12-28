package com.streamcraft.Defkill.Events;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Menus.ClassSelectMenu;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Utils.lobbySigns;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Alex
 * Date: 24.10.13  1:29
 */
public class Signs implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        if (p.isSpectator()) {
            return;
        }
        if ((DefkillPlugin.game.getStatus() == GameStatus.WAITING || (DefkillPlugin.game.getStatus() == GameStatus.INGAME && DefkillPlugin.game.getStage() < 3 && DefkillPlugin.game.getActivePlayers() < DefkillPlugin.game.maxPlayers)) && event.getClickedBlock() != null) {
            Material clickedBlock = event.getClickedBlock().getType();
            if (clickedBlock == Material.WALL_SIGN || clickedBlock == Material.SIGN_POST) {
                if (this.eqLocation(event.getClickedBlock().getLocation(), DefkillPlugin.game.redLobbySign)) {
                    DefkillPlugin.game.addPlayerToTeam(p, "red");
                    lobbySigns.updateSigns();
                    return;
                }
                if (this.eqLocation(event.getClickedBlock().getLocation(), DefkillPlugin.game.blueLobbySign)) {
                    DefkillPlugin.game.addPlayerToTeam(p, "blue");
                    lobbySigns.updateSigns();
                    return;
                }
                if (this.eqLocation(event.getClickedBlock().getLocation(), DefkillPlugin.game.greenLobbySign)) {
                    DefkillPlugin.game.addPlayerToTeam(p, "green");
                    lobbySigns.updateSigns();
                    return;
                }
                if (this.eqLocation(event.getClickedBlock().getLocation(), DefkillPlugin.game.yellowLobbySign)) {
                    DefkillPlugin.game.addPlayerToTeam(p, "yellow");
                    lobbySigns.updateSigns();
                    return;
                }
            }
        }
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
            if (event.getClickedBlock() != null) {
                Material clickedBlock = event.getClickedBlock().getType();
                if (clickedBlock == Material.WALL_SIGN || clickedBlock == Material.SIGN_POST) {
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    if (sign.getLine(1).equals("[Class]")) {
                        new ClassSelectMenu(p).selectClassMenu.openMenu(p.getBukkitModel());
                    }
                }
            }
        }
    }

    private boolean eqLocation(Location loc1, Location loc2) {
        if (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ())
            return true;
        else
            return false;
    }

}
