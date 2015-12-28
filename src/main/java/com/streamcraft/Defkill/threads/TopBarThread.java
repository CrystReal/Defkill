package com.streamcraft.Defkill.threads;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Utils.L;
import com.streamcraft.Defkill.Utils.StringUtil;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Alex
 * Date: 06.12.13  16:14
 */
public class TopBarThread extends Thread implements Runnable {
    public void run() {
        while (true) {
            try {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (DefkillPlugin.game.getStatus() == GameStatus.PRE_GAME || DefkillPlugin.game.getStatus() == GameStatus.WAITING) {
                        if (DefkillPlugin.game.tillGame != 120)
                            BarAPI.setMessage(p, ChatColor.GREEN + "До игры" + StringUtil.plural(DefkillPlugin.game.tillGame, " осталась " + DefkillPlugin.game.tillGame + " секунда", " осталось " + DefkillPlugin.game.tillGame + " секунды", " осталось " + DefkillPlugin.game.tillGame + " секунд") + ".", (float) DefkillPlugin.game.tillGame / (120F / 100F));
                        else if (DefkillPlugin.game.getActivePlayers() < DefkillPlugin.game.minCountPlayersToGame) {
                            float perc = ((float) DefkillPlugin.game.minCountPlayersToGame / 100F);
                            BarAPI.setMessage(p, ChatColor.GREEN + "Ожидаем игроков.", (float) DefkillPlugin.game.getActivePlayers() / (float) perc);
                        } else
                            BarAPI.setMessage(p, ChatColor.GREEN + "Ожидаем игроков.", 100F);
                    }
                    if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
                        BarAPI.setMessage(p, ChatColor.GREEN + "Стадия " + DefkillPlugin.game.getStage(), (float) (DefkillPlugin.game.tillNextStage / (600F / 100F)));
                    }
                    if (DefkillPlugin.game.getStatus() == GameStatus.POSTGAME) {
                        BarAPI.setMessage(p, DefkillPlugin.game.winner.getChatColor() + "Выиграла " + DefkillPlugin.game.winner.getName(true) + " команда.", 100);
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
