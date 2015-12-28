package com.streamcraft.Defkill.Commands;

import com.streamcraft.Defkill.Models.DKPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Alex
 * Date: 24.10.13  4:24
 */
public interface Command {
    public void exec(DKPlayer p, String[] args);
}
