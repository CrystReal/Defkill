package com.streamcraft.Defkill.Models.classes;

import com.streamcraft.Defkill.Models.enums.ItemCostType;
import com.streamcraft.Defkill.Models.interfaces.DKClass;
import com.streamcraft.Defkill.Utils.NBTUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 24.10.13  18:59
 */
public class CivilianClass implements DKClass {
    public ArrayList<ItemStack> startKit = new ArrayList<ItemStack>();
    private CostOfItem cost;

    public CivilianClass() {
        startKit.add(new ItemStack(Material.COMPASS, 1));
        startKit.add(new ItemStack(Material.WOOD_PICKAXE, 1));
        startKit.add(new ItemStack(Material.WOOD_SWORD, 1));
        startKit.add(new ItemStack(Material.WOOD_AXE, 1));
        startKit.add(new ItemStack(Material.WORKBENCH, 1));
        this.cost = new CostOfItem(ItemCostType.EXP, 0);
    }

    public ArrayList<ItemStack> getStartKit() {
        return startKit;
    }

    public String getName() {
        return "Горожанин";
    }

    @Override
    public MaterialData getIcon() {
        MaterialData tmp = new MaterialData(Material.WOOD_SPADE);
        return NBTUtils.removeAttributes(tmp.toItemStack(1)).getData();
    }

    public String getSlogan() {
        return "Ты основа.";
    }

    public String getDescription() {
        return "Добывай ресурсы и подготавливай " + ChatColor.AQUA + "воинов к битве.\n" + ChatColor.WHITE + "Каждый раз при возморжении ты получишь " + ChatColor.WHITE + "деревенный меч, кирку и топор а также " + ChatColor.WHITE + "крафтильню.";
    }

    public CostOfItem getCost() {
        return this.cost;
    }

    @Override
    public DKClass getNewInstance() {
        return new CivilianClass();
    }

    @Override
    public int getDoubleChance(Material m) {
        return 0;
    }

    @Override
    public int getNexusDamage(ItemStack item) {
        return 1;
    }

    @Override
    public double getStartHealth() {
        return 20;
    }

    @Override
    public float getStartExp() {
        return 0;
    }

    @Override
    public double getMultipleFotExp() {
        return 1.0;
    }

    @Override
    public boolean canDoubleJump() {
        return false;
    }

    @Override
    public int getHealRadius() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
