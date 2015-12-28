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
public class HealerClass implements DKClass {
    public ArrayList<ItemStack> startKit = new ArrayList<ItemStack>();
    private CostOfItem cost;

    public HealerClass() {
        startKit.add(new ItemStack(Material.COMPASS, 1));
        startKit.add(new ItemStack(Material.WOOD_PICKAXE, 1));
        startKit.add(new ItemStack(Material.WOOD_SWORD, 1));
        startKit.add(new ItemStack(Material.WOOD_AXE, 1));
        this.cost = new CostOfItem(ItemCostType.EXP, 0);
    }

    public ArrayList<ItemStack> getStartKit() {
        return startKit;
    }

    public String getName() {
        return "Медик";
    }

    @Override
    public MaterialData getIcon() {
        MaterialData tmp = new MaterialData(Material.RED_ROSE);
        return NBTUtils.removeAttributes(tmp.toItemStack(1)).getData();
    }

    public String getSlogan() {
        return "Скорая помощь.";
    }

    public String getDescription() {
        return ChatColor.AQUA + "Каждые 5 секунд все игроки " + ChatColor.AQUA + "в радиусе 5 блоков немного " + ChatColor.AQUA + "восстанавливаются.";
    }

    public CostOfItem getCost() {
        return this.cost;
    }

    @Override
    public DKClass getNewInstance() {
        return new HealerClass();
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
        return 5;
    }
}
