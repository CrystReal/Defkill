package com.streamcraft.Defkill.Utils;

import com.streamcraft.Defkill.Models.DKTeam;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Created by Alex
 * Date: 24.10.13  19:10
 */
public class ColorizeArmor {
    public static ItemStack c(ItemStack item, DKTeam team) {
        LeatherArmorMeta lam1 = (LeatherArmorMeta) item.getItemMeta();
        if (team.getId().equals("red"))
            lam1.setColor(Color.RED);
        if (team.getId().equals("blue"))
            lam1.setColor(Color.BLUE);
        if (team.getId().equals("green"))
            lam1.setColor(Color.GREEN);
        if (team.getId().equals("yellow"))
            lam1.setColor(Color.YELLOW);
        item.setItemMeta(lam1);
        return item;
    }
}
