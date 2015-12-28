package com.streamcraft.Defkill.Events;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKBlockedBlocks;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Models.interfaces.legendaryItem;
import com.streamcraft.Defkill.Utils.DKScoreboard;
import com.streamcraft.Defkill.Utils.EconomicSettings;
import com.streamcraft.Defkill.Utils.L;
import com.updg.CR_API.APIPlugin;
import com.updg.CR_API.Models.APIPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


/**
 * Created by Alex
 * Date: 24.10.13  19:27
 */
public class PlayerEvents implements Listener {
    public Random rand = new Random();

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        if (p.isSpectator() || p.team == null) {
            event.setCancelled(true);
            p.getBukkitModel().updateInventory();
            return;
        }
        if (event.getItemDrop().getItemStack().getItemMeta().getLore() != null && event.getItemDrop().getItemStack().getItemMeta().getLore().get(0) != null && event.getItemDrop().getItemStack().getItemMeta().getLore().get(0).contains("!EXCLUSIVE")) {
            event.setCancelled(true);
            p.getBukkitModel().updateInventory();
        }
        if (event.getItemDrop().getItemStack().getType() == Material.COMPASS || event.getItemDrop().getItemStack().getType() == Material.LEATHER_HELMET || event.getItemDrop().getItemStack().getType() == Material.LEATHER_CHESTPLATE || event.getItemDrop().getItemStack().getType() == Material.LEATHER_LEGGINGS || event.getItemDrop().getItemStack().getType() == Material.LEATHER_BOOTS) {
            event.setCancelled(true);
            p.getBukkitModel().updateInventory();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getEntity().getName());
        for (Iterator iterator = event.getDrops().iterator(); iterator.hasNext(); ) {
            ItemStack item = (ItemStack) iterator.next();
            if ((item.getItemMeta().getLore() != null && item.getItemMeta().getLore().get(0) != null && item.getItemMeta().getLore().get(0).contains("!EXCLUSIVE")) || item.getType() == Material.LEATHER_HELMET || item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_BOOTS || item.getType() == Material.COMPASS || item.getType() == Material.WOOD_AXE || item.getType() == Material.WOOD_PICKAXE || item.getType() == Material.WOOD_SWORD || item.getType() == Material.WORKBENCH) {
                iterator.remove();
            }
        }
        DKPlayer damager = null;
        if (event.getEntity().getKiller() != null)
            damager = DefkillPlugin.game.getPlayer(event.getEntity().getKiller().getName());
        if (damager != null) {
            damager.getStats().addTargetKilled(p);
            damager.addExp(EconomicSettings.kill * damager.getMultiply());
            Bukkit.broadcastMessage(damager.getBukkitModel().getDisplayName() + ChatColor.RESET + " убил " + p.getBukkitModel().getDisplayName());
        } else {
            if (!p.isSpectator())
                Bukkit.broadcastMessage(p.getBukkitModel().getDisplayName() + ChatColor.RESET + " сдох.");
        }

        p.getStats().addDeath();
        event.setDeathMessage(null);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        event.setRespawnLocation(p.getReSpawnLocation());
        p.clearInventory();
        p.takeKitStart();
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME && p.team.nexusHealth > 0) {
            p.setDefaultArmor();
        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        if (p.isSpectator() || p.team == null) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractBlock(PlayerPickupItemEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        if (p.isSpectator() || p.team == null) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        if (p.isSpectator() || p.team == null) {
            event.setCancelled(true);
            return;
        }
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
            Block b = event.getBlock();
            if (b.getType() == Material.DIAMOND_ORE || b.getType() == Material.GRAVEL || b.getType() == Material.MELON_BLOCK || b.getType() == Material.LOG || b.getType() == Material.COAL_ORE || b.getType() == Material.IRON_ORE || b.getType() == Material.GOLD_ORE || b.getType() == Material.WHEAT || b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
                event.setCancelled(true);
                return;
            }
            if (DKBlockedBlocks.isBlocked(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onEntityTarget(PlayerExpChangeEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        event.setAmount((int) (event.getAmount() * p.getCls().getMultipleFotExp()));
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        if (!p.isSpectator() && (p.getCls().canDoubleJump() && (p.getBukkitModel().getGameMode() != GameMode.CREATIVE))) {
            event.setCancelled(true);
            p.getBukkitModel().setAllowFlight(false);
            p.getBukkitModel().setFlying(false);
            p.getBukkitModel().setVelocity(p.getBukkitModel().getLocation().getDirection().multiply(1.6D).setY(1.0D));
        }
        if (p.isSpectator()) {
            p.getBukkitModel().setAllowFlight(true);
            if (p.getBukkitModel().isFlying())
                p.getBukkitModel().setFlying(false);
            else
                p.getBukkitModel().setFlying(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
            DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
            if ((!p.isSpectator() || p.team != null) && DefkillPlugin.game.getPlayerTeam(p) != null && DefkillPlugin.game.getPlayerTeam(p).nexusHealth > 0)
                DKScoreboard.updateSidebarScoreboard(p);
            else
                DKScoreboard.updateSidebarScoreboard(p, false);

            Block up = p.getBukkitModel().getLocation().getBlock();
            if (up.getType() == Material.STONE_PLATE) {
                Block down = up.getRelative(0, -1, 0);
                if (down.getType() == Material.IRON_BLOCK || down.getType() == Material.GOLD_BLOCK) {
                    p.getBukkitModel().setVelocity(p.getBukkitModel().getLocation().getDirection().multiply(4));
                    p.getBukkitModel().setVelocity(new Vector(p.getBukkitModel().getVelocity().getX(), 1.0D, p.getBukkitModel().getVelocity().getZ()));
                    p.addFallProtection();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        APIPlayer pA = APIPlugin.getPlayer(p.getName());
        if (DefkillPlugin.game.getStatus() != GameStatus.INGAME) {
            event.setFormat(pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitModel().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
            return;
        }
        if (p.isSpectator() || p.team == null) {
            for (DKPlayer item : DefkillPlugin.game.getSpectators().values()) {
                item.sendMessage(ChatColor.GRAY + "[Наблюдающий] " + ChatColor.RESET + pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitModel().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
            }
            event.setCancelled(true);
            return;
        }
        if (!event.getMessage().startsWith("!")) {
            event.setCancelled(true);
            if (p.team != null) {
                for (DKPlayer item : p.team.getPlayers().values()) {
                    item.sendMessage(ChatColor.GRAY + "[Команда] " + ChatColor.RESET + pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitModel().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
                }
            } else {
                event.setFormat(pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitModel().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage());
                event.setCancelled(false);
            }
        } else {
            event.setFormat(pA.getPrefix() + ChatColor.RESET + pA.getNickColor() + p.getBukkitModel().getDisplayName() + ChatColor.RESET + pA.getColonColor() + ": " + ChatColor.RESET + pA.getMessageColor() + event.getMessage().substring(1));
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void dmg(final EntityDamageByEntityEvent event) {
        if (DefkillPlugin.game.getStatus() == GameStatus.WAITING || DefkillPlugin.game.getStatus() == GameStatus.PRE_GAME) {
            event.setCancelled(true);
            return;
        }
        Entity e = event.getEntity();
        DKPlayer damager = null;
        if (event.getDamager() instanceof Player)
            damager = DefkillPlugin.game.getPlayer(((Player) event.getDamager()).getName());
        if (damager != null && damager.isSpectator()) {
            event.setCancelled(true);
            return;
        }
        if (e instanceof Player) {
            final DKPlayer p = DefkillPlugin.game.getPlayer(((Player) e).getName());
            if (event.getDamage() >= p.getBukkitModel().getHealth()) {
                if (damager != null) {
                    if (damager.team.equals(p.team)) {
                        damager.sendMessage("Харе бить своих!");
                        return;
                    }
                }
                return;
            }
            if (event.getDamager() instanceof Player) {
                if (p.getBukkitModel().getItemInHand() != null && p.getBukkitModel().getItemInHand().getItemMeta() != null && p.getBukkitModel().getItemInHand().getItemMeta().getDisplayName() != null && p.getBukkitModel().getItemInHand().getItemMeta().getDisplayName().startsWith("!LI")) {
                    legendaryItem tmp = DefkillPlugin.game.legendaryItems.get(Integer.parseInt(p.getBukkitModel().getItemInHand().getItemMeta().getDisplayName().substring(3)));
                    if (tmp != null)
                        tmp.onAtack(damager, p);
                }
            }
        }
        if (e instanceof IronGolem) {
            if (event.getDamager() instanceof Player) {
                DKPlayer d = DefkillPlugin.game.getPlayer(((Player) event.getDamager()).getName());
                if (((IronGolem) e).getHealth() <= event.getDamage()) {
                    d.getStats().addGolem();
                    event.setCancelled(true);
                    e.getLocation().getWorld().dropItemNaturally(e.getLocation(), DefkillPlugin.game.legendaryItems.get(rand.nextInt(DefkillPlugin.game.legendaryItems.size())).toItemStack());
                    Firework firework = e.getLocation().getWorld().spawn(e.getLocation(), Firework.class);
                    FireworkMeta data = firework.getFireworkMeta();
                    data.addEffects(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).build());
                    data.setPower(2);
                    firework.setFireworkMeta(data);
                    e.getLocation().getWorld().playSound(e.getLocation(), Sound.EXPLODE, 1, 1);
                    e.remove();
                }
            }
        }
    }

    @EventHandler
    public void selfSuck(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (e instanceof Player) {
            DKPlayer p = DefkillPlugin.game.getPlayer(((Player) e).getName());
            if (p.isSpectator()) {
                event.setCancelled(true);
                return;
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (p.isFallPtotected()) {
                    p.removeFallProtection();
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onChangeHunger(FoodLevelChangeEvent e) {
        if (DefkillPlugin.game.getStatus() != GameStatus.INGAME) {
            e.setFoodLevel(20);
        }
    }
}
