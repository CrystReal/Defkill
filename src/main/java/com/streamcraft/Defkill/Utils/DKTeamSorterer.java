package com.streamcraft.Defkill.Utils;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.DKTeam;

import java.util.HashMap;

/**
 * Created by Alex
 * Date: 28.10.13  20:18
 */
public class DKTeamSorterer {
    public static void sort(HashMap<String, DKPlayer> players, HashMap<String, DKTeam> teams) {
        for (DKPlayer p : players.values()) {
            DKTeam team = null;
            int min = 1000;
            for (DKTeam t : teams.values()) {
                if (t.getPlayers().size() < min) {
                    min = t.getPlayers().size();
                    team = t;
                }
            }
            if (team != null)
                DefkillPlugin.game.addPlayerToTeam(p, team.getId());
            else {
                for (DKTeam t1 : teams.values()) {
                    DefkillPlugin.game.addPlayerToTeam(p, t1.getId());
                    break;
                }
            }
        }
    }
}
