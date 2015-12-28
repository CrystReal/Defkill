package com.streamcraft.Defkill.Events;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKTeam;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * Created by Alex
 * Date: 25.10.13  4:06
 */
public class TagApi implements Listener {
    @EventHandler
    public void onNameTag(PlayerReceiveNameTagEvent event) {

        if (DefkillPlugin.game.getPlayer(event.getNamedPlayer().getName()) != null) {
            DKTeam team = DefkillPlugin.game.getPlayer(event.getNamedPlayer().getName()).team;
            if (team != null)
                event.setTag(team.getChatColor() + event.getNamedPlayer().getName());
        }
    }
}
