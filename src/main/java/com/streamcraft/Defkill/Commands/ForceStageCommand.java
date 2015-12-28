package com.streamcraft.Defkill.Commands;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import org.bukkit.ChatColor;

/**
 * Created by Alex
 * Date: 24.10.13  4:24
 */
public class ForceStageCommand implements Command {
    @Override
    public void exec(DKPlayer p, String[] args) {
        if (p.getBukkitModel().hasPermission("dk.forceStage")) {
            if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
                DefkillPlugin.game.upStage();
            } else {
                p.sendMessage(ChatColor.RED + "Avail only in game");
            }
        } else {
            p.sendMessage(ChatColor.RED + "Не достаточно прав");
        }
    }
}
