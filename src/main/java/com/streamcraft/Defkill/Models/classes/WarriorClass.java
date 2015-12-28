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
public class WarriorClass implements DKClass {
    private final CostOfItem cost;
    public ArrayList<ItemStack> startKit = new ArrayList<ItemStack>();

    public WarriorClass() {
        startKit.add(new ItemStack(Material.COMPASS, 1));
        startKit.add(new ItemStack(Material.WOOD_AXE, 1));
        startKit.add(new ItemStack(Material.WOOD_PICKAXE, 1));
        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        sword.addEnchantment(Enchantment.KNOCKBACK, 1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        startKit.add(NBTUtils.setExclusive(sword));
        Potion potion = new Potion(PotionType.INSTANT_HEAL);
        potion.setLevel(1);
        startKit.add(NBTUtils.setExclusive(potion.toItemStack(1)));
        this.cost = new CostOfItem(ItemCostType.EXP, 0);
    }

    public ArrayList<ItemStack> getStartKit() {
        return startKit;
    }

    public String getName() {
        return "Воин";
    }

    @Override
    public MaterialData getIcon() {
        MaterialData tmp = new MaterialData(Material.STONE_SWORD);
        return NBTUtils.removeAttributes(tmp.toItemStack(1)).getData();
    }

    public String getSlogan() {
        return "Мочи гадов!";
    }

    public String getDescription() {
        return "Получи на старте каменный \n" + ChatColor.AQUA + "меч с эффектом отбрасывания и зелье жизни и сразу отправляйся в бой.";
    }

    public CostOfItem getCost() {
        return this.cost;
    }

    @Override
    public DKClass getNewInstance() {
        return new WarriorClass();
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
        return 0;
    }
}
