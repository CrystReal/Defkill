package com.streamcraft.Defkill.Models.Stages;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.interfaces.Stage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by Alex
 * Date: 25.10.13  1:40
 */
public class Stage3 implements Stage {
    @Override
    public void onStart() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Внимание! Началась третья стадия! Алмазы доступны для добычи и големы заспавнены!");
        DefkillPlugin.game.getSelectedMap().spawnDiamonds();
        DefkillPlugin.game.getSelectedMap().spawnBosses();
    }

    @Override
    public void onEnd() {
        return;
    }
}
