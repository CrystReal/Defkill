package com.streamcraft.Defkill.Models.interfaces;

import com.streamcraft.Defkill.Models.classes.CostOfItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 24.10.13  0:48
 */
public interface DKClass {

    DKClass getNewInstance();

    public ArrayList<ItemStack> getStartKit();

    double getStartHealth();

    float getStartExp();

    CostOfItem getCost();

    /* FOR VISUAL MENU */

    String getName();

    MaterialData getIcon();

    String getSlogan();

    String getDescription();

    /* FOR EVENTS */

    // MINING
    int getDoubleChance(Material m);

    double getMultipleFotExp();

    // MISC
    public int getNexusDamage(ItemStack item);

    boolean canDoubleJump();

    int getHealRadius();

}
