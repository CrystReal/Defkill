package com.streamcraft.Defkill.threads;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.enums.GameStatus;

/**
 * Created by Alex
 * Date: 11.11.13  0:45
 */
public class HealerThread extends Thread implements Runnable {
    public void run() {
        while (true) {
            if (DefkillPlugin.game.getStatus() == GameStatus.INGAME)
                for (DKPlayer item : DefkillPlugin.game.getActivePlayersArray()) {
                    if (item.getCls().getHealRadius() > 0) {
                        for (DKPlayer item2 : DefkillPlugin.game.getActivePlayersArray()) {
                            if (item.getBukkitModel().getLocation().distance(item2.getBukkitModel().getLocation()) <= item.getCls().getHealRadius()) {
                                if (item.team.getId().equals(item2.team.getId()) && item2.getBukkitModel().getHealth() != 20) {
                                    if (item2.getBukkitModel().getHealth() + 1 <= 20)
                                        item2.getBukkitModel().setHealth(item2.getBukkitModel().getHealth() + 1);
                                    else
                                        item2.getBukkitModel().setHealth(20);
                                }
                            }
                        }
                    }
                }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
