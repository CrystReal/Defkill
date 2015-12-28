package com.streamcraft.Defkill.Events;

import com.streamcraft.Defkill.DefkillPlugin;
import com.updg.CR_API.Events.BungeeReturnIdEvent;
import com.updg.CR_API.Events.LobbyUpdateCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Alex
 * Date: 25.01.14  22:43
 */
public class Bungee implements Listener {
    @EventHandler
    public void onGetId(BungeeReturnIdEvent e) {
        DefkillPlugin.game.getPlayer(e.getUsername()).setId(e.getId());
    }

    @EventHandler
    public void updateCheck(LobbyUpdateCheckEvent e) {
        DefkillPlugin.game.send();
    }
}
