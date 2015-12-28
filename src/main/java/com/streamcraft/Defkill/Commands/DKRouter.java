package com.streamcraft.Defkill.Commands;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Created by Alex
 * Date: 24.10.13  4:28
 */
public class DKRouter implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда может выполняться только игроком.");
            return false;
        }
        DKPlayer player = DefkillPlugin.game.getPlayer(sender.getName());
        if (args.length > 0) {
            String com = args[0].toLowerCase();

            if (DefkillPlugin.commands.containsKey(com)) {
                DefkillPlugin.commands.get(com).exec(player, args);
            } else DefkillPlugin.commands.get("help").exec(player, args);

            return true;
        }
        DefkillPlugin.commands.get("help").exec(player, args);
        return true;
    }
}
