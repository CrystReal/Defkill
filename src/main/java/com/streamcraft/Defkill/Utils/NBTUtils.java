package com.streamcraft.Defkill.Utils;

import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.server.v1_7_R2.NBTTagList;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex
 * Date: 25.10.13  19:16
 */
public class NBTUtils {
    public static ItemStack removeAttributes(ItemStack item) {
        net.minecraft.server.v1_7_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        } else {
            tag = nmsStack.getTag();
        }
        NBTTagList am = new NBTTagList();
        tag.set("AttributeModifiers", am);
        nmsStack.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack renameItem(ItemStack is, String name) {
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(name);
        is.setItemMeta(m);
        return is;
    }

    public static ItemStack setExclusive(ItemStack is) {
        ItemMeta i = is.getItemMeta();
        if (i.getLore() == null || !i.getLore().get(0).contains("!EXCLUSIVE")) {
            List<String> meta = new ArrayList<String>() {{
                add("!EXCLUSIVE");
            }};
            i.setLore(meta);
            is.setItemMeta(i);
        }
        return is;
    }
}
