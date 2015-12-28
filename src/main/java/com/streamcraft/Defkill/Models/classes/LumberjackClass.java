package com.streamcraft.Defkill.Models.classes;

import com.streamcraft.Defkill.Models.enums.ItemCostType;
import com.streamcraft.Defkill.Models.interfaces.DKClass;
import com.streamcraft.Defkill.Utils.NBTUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

/**
 * Created by Alex
 * Date: 24.10.13  18:59
 */
public class LumberjackClass implements DKClass {
    private final CostOfItem cost;
    public ArrayList<ItemStack> startKit = new ArrayList<ItemStack>();

    public LumberjackClass() {
        startKit.add(new ItemStack(Material.COMPASS, 1));
        startKit.add(new ItemStack(Material.WOOD_PICKAXE, 1));
        startKit.add(new ItemStack(Material.WOOD_SWORD, 1));
        ItemStack tmp = new ItemStack(Material.STONE_AXE, 1);
        tmp.addEnchantment(Enchantment.DURABILITY, 1);
        startKit.add(NBTUtils.setExclusive(tmp));
        this.cost = new CostOfItem(ItemCostType.EXP, 0);
    }

    public ArrayList<ItemStack> getStartKit() {
        return startKit;
    }

    public String getName() {
        return "Дровосек";
    }

    @Override
    public MaterialData getIcon() {
        MaterialData tmp = new MaterialData(Material.STONE_AXE);
        return NBTUtils.removeAttributes(tmp.toItemStack(1)).getData();
    }

    public String getSlogan() {
        return "Ты добытчик.";
    }

    public String getDescription() {
        return "При добыче дерева есть шанс что " + ChatColor.AQUA + "выпадет 2 блока из одного.\n" + ChatColor.AQUA + "Также твой топор с Эффективностью 2й " + ChatColor.AQUA + "спетени помогает быстро добывать " + ChatColor.AQUA + "дерево.";
    }

    public CostOfItem getCost() {
        return this.cost;
    }

    @Override
    public DKClass getNewInstance() {
        return new LumberjackClass();
    }

    @Override
    public int getDoubleChance(Material m) {
        switch (m) {
            case LOG:
                return 50;
            default:
                return 0;
        }
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
