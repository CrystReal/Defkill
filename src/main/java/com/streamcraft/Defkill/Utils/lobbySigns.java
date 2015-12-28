package com.streamcraft.Defkill.Utils;

import com.streamcraft.Defkill.DefkillPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

/**
 * Created by Alex
 * Date: 09.11.13  4:15
 */
public class lobbySigns {
    public static void updateSigns() {
        int tmp = 0;
        Sign red = (Sign) DefkillPlugin.game.redLobbySign.getBlock().getState();
        red.setLine(0, ChatColor.RED + "Красная");
        red.setLine(1, "");
        tmp = DefkillPlugin.game.getTeam("red").getPlayers().size();
        red.setLine(2, StringUtil.plural(tmp, tmp + " игрок", tmp + " игрока", tmp + " игроков"));
        red.setLine(3, "Нексус: " + DefkillPlugin.game.getTeam("red").nexusHealth);
        red.update();

        Sign blue = (Sign) DefkillPlugin.game.blueLobbySign.getBlock().getState();
        blue.setLine(0, ChatColor.BLUE + "Синяя");
        blue.setLine(1, "");
        tmp = DefkillPlugin.game.getTeam("blue").getPlayers().size();
        blue.setLine(2, StringUtil.plural(tmp, tmp + " игрок", tmp + " игрока", tmp + " игроков"));
        blue.setLine(3, "Нексус: " + DefkillPlugin.game.getTeam("blue").nexusHealth);
        blue.update();

        Sign green = (Sign) DefkillPlugin.game.greenLobbySign.getBlock().getState();
        green.setLine(0, ChatColor.GREEN + "Зеленая");
        green.setLine(1, "");
        tmp = DefkillPlugin.game.getTeam("green").getPlayers().size();
        green.setLine(2, StringUtil.plural(tmp, tmp + " игрок", tmp + " игрока", tmp + " игроков"));
        green.setLine(3, "Нексус: " + DefkillPlugin.game.getTeam("green").nexusHealth);
        green.update();

        Sign yellow = (Sign) DefkillPlugin.game.yellowLobbySign.getBlock().getState();
        yellow.setLine(0, ChatColor.YELLOW + "Желтая");
        yellow.setLine(1, "");
        tmp = DefkillPlugin.game.getTeam("yellow").getPlayers().size();
        yellow.setLine(2, StringUtil.plural(tmp, tmp + " игрок", tmp + " игрока", tmp + " игроков"));
        yellow.setLine(3, "Нексус: " + DefkillPlugin.game.getTeam("yellow").nexusHealth);
        yellow.update();
    }
}
