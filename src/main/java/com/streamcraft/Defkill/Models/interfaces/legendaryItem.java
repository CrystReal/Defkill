package com.streamcraft.Defkill.Models.interfaces;

import com.streamcraft.Defkill.Models.DKPlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Alex
 * Date: 28.10.13  19:59
 */
public interface legendaryItem {
    int getId();

    String getName();

    void onAtack(DKPlayer attacker, DKPlayer target);

    ItemStack toItemStack();
}
