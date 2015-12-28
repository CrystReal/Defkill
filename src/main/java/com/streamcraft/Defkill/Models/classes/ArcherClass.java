package com.streamcraft.Defkill.Models.classes;

import com.streamcraft.Defkill.Models.enums.ItemCostType;
import com.streamcraft.Defkill.Models.interfaces.DKClass;
import com.streamcraft.Defkill.Utils.NBTUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex
 * Date: 24.10.13  18:59
 */
public class ArcherClass implements DKClass {
    private final CostOfItem cost;
    public ArrayList<ItemStack> startKit = new ArrayList<ItemStack>();

    public ArcherClass() {
        startKit.add(new ItemStack(Material.COMPASS, 1));
        startKit.add(new ItemStack(Material.WOOD_SWORD, 1));
        startKit.add(new ItemStack(Material.WOOD_AXE, 1));
        startKit.add(new ItemStack(Material.WOOD_PICKAXE, 1));
        startKit.add(new ItemStack(Material.WOOD_SPADE, 1));
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
        startKit.add(NBTUtils.setExclusive(bow));
        startKit.add(new ItemStack(Material.ARROW, 16));
        Potion potion = new Potion(PotionType.INSTANT_HEAL);
        potion.setLevel(1);
        startKit.add(NBTUtils.setExclusive(potion.toItemStack(1)));
        this.cost = new CostOfItem(ItemCostType.EXP, 0);
    }

    public ArrayList<ItemStack> getStartKit() {
        return startKit;
    }

    public String getName() {
        return "Робин Гуд";
    }

    @Override
    public MaterialData getIcon() {
        MaterialData tmp = new MaterialData(Material.BOW);
        return NBTUtils.removeAttributes(tmp.toItemStack(1)).getData();
    }

    public String getSlogan() {
        return "Ты лучник.";
    }

    public String getDescription() {
        return "Уничтожай противника \n" + ChatColor.AQUA + "с большого расстояния.";
    }

    public CostOfItem getCost() {
        return this.cost;
    }

    @Override
    public DKClass getNewInstance() {
        return new ArcherClass();
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
        return true;
    }

    @Override
    public int getHealRadius() {
        return 0;
    }
}
