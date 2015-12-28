package com.streamcraft.Defkill.Models.Stages;

import com.streamcraft.Defkill.Models.interfaces.Stage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by Alex
 * Date: 25.10.13  1:40
 */
public class Stage5 implements Stage {
    @Override
    public void onStart() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Внимание! Началась 5я последняя стадия! Урон нексусам теперь умножается на 2!");
    }

    @Override
    public void onEnd() {
        return;
    }
}
