package com.streamcraft.Defkill.Menus;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.Classes;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Models.enums.ItemCostType;
import com.streamcraft.Defkill.Models.interfaces.DKClass;
import com.streamcraft.Defkill.Utils.NBTUtils;
import com.streamcraft.Defkill.Utils.StringUtil;
import me.xhawk87.PopupMenuAPI.MenuItem;
import me.xhawk87.PopupMenuAPI.PopupMenu;
import me.xhawk87.PopupMenuAPI.PopupMenuAPI;
import net.minecraft.server.v1_7_R2.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

public class ClassSelectMenu {

    public PopupMenu selectClassMenu;

    public ClassSelectMenu(DKPlayer pIn) {
        ArrayList<DKClass> exp = new ArrayList<DKClass>();
        ArrayList<DKClass> real = new ArrayList<DKClass>();
        for (DKClass item : Classes.classes.values()) {
            if (item.getCost().getType() == ItemCostType.EXP)
                exp.add(item);
            if (item.getCost().getType() == ItemCostType.REAL_MONEY)
                real.add(item);
        }
        selectClassMenu = PopupMenuAPI.createMenu("Выбери класс", 9);
        int i = 0;
        for (final DKClass item : exp) {
            MenuItem menuItem = new MenuItem(item.getName(), item.getIcon()) {

                @Override
                public void onClick(Player player) {
                    DKPlayer p = DefkillPlugin.game.getPlayer(player.getName());
                    if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
                        if (p.getCls().getName().equals(item.getName()))
                            p.sendMessage("Ты уже " + item.getName());
                        else {
                            if (item.getCost().getType() == ItemCostType.EXP) {
                                if (p.getExp() >= item.getCost().howMutch()) {
                                    p.withdrawExp(item.getCost().howMutch());
                                    p.setCls(item.getNewInstance());
                                    p.sendMessage("Теперь ты " + item.getName());
                                } else {
                                    p.sendMessage("У тебя не достаточно опыта для покупки класа.");
                                }
                            } else if (item.getCost().getType() == ItemCostType.REAL_MONEY) {
                                if (p.getRealMoney() >= item.getCost().howMutch()) {
                                    p.withdrawMoney(item.getCost().howMutch());
                                    p.setCls(item.getNewInstance());
                                    p.sendMessage("Теперь ты " + item.getName());
                                } else {
                                    p.sendMessage("У тебя не достаточно денег для покупки класа.");
                                }
                            }
                        }
                    }
                    getMenu().closeMenu(player);
                }
            };
            if (item.getCost().howMutch() > 0)
                menuItem.setDescriptions(StringUtil.wrapWords("!ENCH" + ChatColor.AQUA + "" + ChatColor.BOLD + item.getSlogan() + "\n" + ChatColor.RESET + "" + ChatColor.AQUA + item.getDescription() + "\n\n" + ChatColor.GOLD + "Цена: " + item.getCost().howMutch() + " опыта.", 40));
            else
                menuItem.setDescriptions(StringUtil.wrapWords("!ENCH" + ChatColor.AQUA + "" + ChatColor.BOLD + item.getSlogan() + "\n" + ChatColor.RESET + "" + ChatColor.AQUA + item.getDescription() + "\n\n" + ChatColor.GOLD + "Цена: Бесплатно", 40));
            selectClassMenu.addMenuItem(menuItem, i);
            i++;
        }
        i = (i / 9 + 2) * 9;
        for (final DKClass item : real) {
            MenuItem menuItem = new MenuItem(item.getName(), item.getIcon()) {

                @Override
                public void onClick(Player player) {
                    DKPlayer p = DefkillPlugin.game.getPlayer(player.getName());
                    if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
                        if (p.getCls().getName().equals(item.getName()))
                            p.sendMessage("Ты уже " + item.getName());
                        else {
                            if (item.getCost().getType() == ItemCostType.EXP) {
                                if (p.getBukkitModel().getExp() >= item.getCost().howMutch()) {
                                    p.getBukkitModel().setExp((float) (p.getBukkitModel().getExp() - item.getCost().howMutch()));
                                    p.setCls(item.getNewInstance());
                                    p.sendMessage("Теперь ты " + item.getName());
                                } else {
                                    p.sendMessage("У тебя не достаточно опыта для покупки класа.");
                                }
                            } else if (item.getCost().getType() == ItemCostType.REAL_MONEY) {
                                if (p.getRealMoney() >= item.getCost().howMutch()) {
                                    p.setRealMoney(p.getRealMoney() - item.getCost().howMutch());
                                    p.setCls(item.getNewInstance());
                                    p.sendMessage("Теперь ты " + item.getName());
                                } else {
                                    p.sendMessage("У тебя не достаточно денег для покупки класа.");
                                }
                            }
                        }
                    }
                    getMenu().closeMenu(player);
                }
            };
            if (item.getCost().howMutch() > 0)
                menuItem.setDescriptions(StringUtil.wrapWords("!ENCH" + ChatColor.AQUA + "" + ChatColor.BOLD + item.getSlogan() + "\n" + ChatColor.RESET + "" + ChatColor.AQUA + item.getDescription() + "\n\n" + ChatColor.GOLD + "Цена: " + item.getCost().howMutch() + " руб.", 40));
            else
                menuItem.setDescriptions(StringUtil.wrapWords("!ENCH" + ChatColor.AQUA + "" + ChatColor.BOLD + item.getSlogan() + "\n" + ChatColor.RESET + "" + ChatColor.AQUA + item.getDescription() + "\n\n" + ChatColor.GOLD + "Цена: Бесплатно", 40));
            selectClassMenu.addMenuItem(menuItem, i);
            i++;
        }
        i = (i / 9 + 1) * 9;
        MaterialData tmp = new MaterialData(org.bukkit.Material.BOW);
        MenuItem menuItem = new MenuItem("Баланс", NBTUtils.removeAttributes(tmp.toItemStack(1)).getData()) {

            @Override
            public void onClick(Player player) {
                return;
            }
        };
        menuItem.setDescriptions(StringUtil.wrapWords("!ENCH\n" + ChatColor.AQUA + "Опыт: " + ChatColor.BOLD + pIn.getExp() + "\n" + ChatColor.RESET + "" + ChatColor.AQUA + "Бабло: " + pIn.getRealMoney(), 40));
        selectClassMenu.addMenuItem(menuItem, i);
    }
}