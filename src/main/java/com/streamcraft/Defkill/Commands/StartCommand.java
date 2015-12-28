package com.streamcraft.Defkill.Commands;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import org.bukkit.ChatColor;

/**
 * Created by Alex
 * Date: 24.10.13  4:24
 */
public class StartCommand implements Command {
    @Override
    public void exec(DKPlayer p, String[] args) {
        if (p.getBukkitModel().hasPermission("dk.start")) {
            if (DefkillPlugin.game.isAvailableToStart()) {
                if (DefkillPlugin.game.tillGame < 120)
                    DefkillPlugin.game.tillGame = 20;
                else
                    DefkillPlugin.game.hardStart();
            } else {
                p.getBukkitModel().sendMessage("Не достаточно игроков");
            }
        } else {
            p.sendMessage(ChatColor.RED + "Не достаточно прав");
        }
    }
}
