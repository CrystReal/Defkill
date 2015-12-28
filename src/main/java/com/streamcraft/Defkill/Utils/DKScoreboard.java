package com.streamcraft.Defkill.Utils;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.DKTeam;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

/**
 * Created by Alex
 * Date: 25.10.13  2:46
 */
public class DKScoreboard {
    public static void updateSidebarScoreboard(DKPlayer player) {
        DKScoreboard.updateSidebarScoreboard(player, true, null);
    }

    public static void updateSidebarScoreboard(DKPlayer player, DKTeam damageTeam) {
        DKScoreboard.updateSidebarScoreboard(player, true, damageTeam);
    }

    public static void updateSidebarScoreboard(DKPlayer player, boolean distance) {
        DKScoreboard.updateSidebarScoreboard(player, distance, null);
    }

    public static void updateSidebarScoreboard(DKPlayer player, boolean distance, DKTeam damageTeam) {
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME || DefkillPlugin.game.getStatus() == GameStatus.RELOAD || DefkillPlugin.game.getStatus() == GameStatus.POSTGAME) {
            DKTeam team = DefkillPlugin.game.getPlayerTeam(player);
            Scoreboard board;
            Objective objective;
            board = player.getBukkitModel().getScoreboard();
            objective = board.getObjective("stats");
            if (objective == null) {
                player.getBukkitModel().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                board = player.getBukkitModel().getScoreboard();
                objective = board.registerNewObjective("stats", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            Score score;
            if (distance && !player.isSpectator() && player.team != null) {
                objective.setDisplayName(team.getChatColor() + "Нексус:     " + (int) DefkillPlugin.game.getSelectedMap().getNexus(team, player.getBukkitModel().getLocation()));
            } else {
                objective.setDisplayName(ChatColor.RESET + " ");
            }
            for (DKTeam item : DefkillPlugin.game.getTeams().values()) {
                if (damageTeam != null && item.equals(damageTeam))
                    score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + item.getName(true) + ":"));
                else
                    score = objective.getScore(Bukkit.getOfflinePlayer(item.getChatColor() + item.getName(true) + ":"));
                if (item.nexusHealth > 0) {
                    score.setScore(item.nexusHealth);
                } else {
                    score.setScore(0);
                }
            }
            score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + "" + ChatColor.LIGHT_PURPLE + "Стадия:"));
            score.setScore(DefkillPlugin.game.getStage());
        } else {
            Scoreboard board;
            Objective objective;
            board = player.getBukkitModel().getScoreboard();
            objective = board.getObjective("lobby");
            if (objective == null) {
                player.getBukkitModel().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                board = player.getBukkitModel().getScoreboard();
                objective = board.registerNewObjective("lobby", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            Score score;
            objective.setDisplayName(ChatColor.GREEN + "Выбор карты: /vote [имя карты]");
            score = objective.getScore(Bukkit.getOfflinePlayer("Beta Test Map"));
            score.setScore(1);
        }
    }

    public static void updateSubTitle(DKPlayer player) {
        //TODO: FIX SUB TITLE
       /* if (!player.isSpectator() && player.team != null) {
            Scoreboard board;
            board = player.getBukkitModel().getScoreboard();
            Objective objective;
            objective = board.getObjective("team");
            if (objective == null) {
                objective = board.registerNewObjective("team", "dummy");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } else {
                objective.unregister();
                objective = board.registerNewObjective("team", "dummy");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            }
            L.$(player.getCls().getName());
            objective.setDisplayName(player.getCls().getName() + " 0" + ChatColor.RESET + "");
            objective.getScore(player.getBukkitModel()).setScore(1);
            objective.getScore(player.getBukkitModel()).setScore(0);
            player.getBukkitModel().setScoreboard(board);
        }   */
    }
}
