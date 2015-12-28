package com.streamcraft.Defkill.Events;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.shininet.bukkit.itemrenamer.api.RenamerSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex
 * Date: 25.10.13  20:38
 */
public class BlockCleaner {
    public static void clear(RenamerSnapshot stacks) {
        for (ItemStack stack : stacks) {
            // Only update those stacks that have our flag lore
            if (stack != null && stack.hasItemMeta()) {
                List<String> lore = stack.getItemMeta().getLore();

                if (lore != null && lore.get(0) != null && lore.get(0).startsWith("!ENCH")) {
                    List<String> out = new ArrayList<String>();
                    int i = 0;
                    for (String item : lore) {
                        if (i == 0)
                            out.add(item.substring(5));
                        else
                            out.add(item);
                        i++;
                    }
                    ItemMeta tmp = stack.getItemMeta();
                    tmp.setLore(out);
                    stack.setItemMeta(tmp);
                    NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
                    compound.put(NbtFactory.ofList("AttributeModifiers", new Object[0]));
                }
            }
        }
    }

    public static void hideLore(RenamerSnapshot stacks) {
        for (ItemStack stack : stacks) {
            // Only update those stacks that have our flag lore
            if (stack != null && stack.hasItemMeta()) {
                List<String> lore = stack.getItemMeta().getLore();
                if (lore != null && lore.get(0) != null && (lore.get(0).startsWith("!HIDE") || lore.get(0).startsWith("!LI"))) {
                    ItemMeta tmp = stack.getItemMeta();
                    tmp.setLore(null);
                    stack.setItemMeta(tmp);
                }
            }
        }
    }
}
