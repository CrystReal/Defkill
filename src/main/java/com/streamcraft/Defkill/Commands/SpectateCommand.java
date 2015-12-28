package com.streamcraft.Defkill.Commands;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import org.bukkit.ChatColor;

/**
 * Created by Alex
 * Date: 24.10.13  4:24
 */
public class SpectateCommand implements Command {
    @Override
    public void exec(DKPlayer p, String[] args) {
        if (p.getBukkitModel().hasPermission("dk.spectate")) {
            if (p.isSpectator() || ((DefkillPlugin.game.getStatus() == GameStatus.INGAME || DefkillPlugin.game.getStatus() == GameStatus.POSTGAME) && p.team != null && p.team.nexusHealth > 0)) {
                p.sendMessage("Нельзя менять статус во время игры.");
                return;
            }
            if (p.isSpectator() && DefkillPlugin.game.getActivePlayers() >= DefkillPlugin.game.maxPlayers) {
                p.sendMessage("Ошибка смены статуса. Сервер полный.");
                return;
            }
            if (p.isSpectator()) {
                p.setSpectator(false);
                DefkillPlugin.game.removeSpectator(p);
                p.sendMessage("Теперь ты обычный игрок");
            } else {
                p.setSpectator(true);
                DefkillPlugin.game.addSpectator(p);
                p.sendMessage("Теперь ты наблюдающий");
            }
        } else {
            p.sendMessage(ChatColor.RED + "Не достаточно прав");
        }
    }
}
