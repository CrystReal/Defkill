package com.streamcraft.Defkill.Models.legendaryItems;

import com.google.common.collect.Lists;
import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.interfaces.legendaryItem;
import com.streamcraft.Defkill.Utils.NBTUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Alex
 * Date: 28.10.13  20:02
 */
public class legendarySwordConfusion implements legendaryItem {
    @Override
    public int getId() {
        return DefkillPlugin.game.legendaryItems.indexOf(this);
    }

    @Override
    public String getName() {
        return "Неверояный меч рассеивания";
    }

    @Override
    public void onAtack(DKPlayer p, DKPlayer target) {
        if (DefkillPlugin.rand.nextInt(3) == 1)
            target.getBukkitModel().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
        if (DefkillPlugin.rand.nextInt(3) == 1)
            target.getBukkitModel().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
    }


    @Override
    public ItemStack toItemStack() {
        ItemStack is = new ItemStack(Material.GOLD_SWORD, 1);
        is.addEnchantment(Enchantment.DURABILITY, 3);
        is = NBTUtils.renameItem(is, this.getName());
        ItemMeta tmp = is.getItemMeta();
        tmp.setLore(Lists.newArrayList("!LI" + this.getId()));
        is.setItemMeta(tmp);
        return is;
    }
}
