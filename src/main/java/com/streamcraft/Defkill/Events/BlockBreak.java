package com.streamcraft.Defkill.Events;

import com.streamcraft.Defkill.DefkillPlugin;
import com.streamcraft.Defkill.Models.DKBlockedBlocks;
import com.streamcraft.Defkill.Models.DKPlayer;
import com.streamcraft.Defkill.Models.DKTeam;
import com.streamcraft.Defkill.Models.enums.GameStatus;
import com.streamcraft.Defkill.Utils.DKLog;
import com.streamcraft.Defkill.Utils.DKScoreboard;
import com.streamcraft.Defkill.Utils.Drops;
import com.streamcraft.Defkill.Utils.EconomicSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Created by Alex
 * Date: 01.11.13  23:49
 */
public class BlockBreak implements Listener {
    public Random rand = new Random();

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        DKPlayer p = DefkillPlugin.game.getPlayer(event.getPlayer().getName());
        if (DefkillPlugin.game.getStatus() == GameStatus.WAITING || DefkillPlugin.game.getStatus() == GameStatus.PRE_GAME) {
            event.setCancelled(true);
            return;
        }
        if (p.isSpectator() || p.team == null) {
            event.setCancelled(true);
            return;
        }
        if (this.hasSign(event.getBlock()) || event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
            event.setCancelled(true);
            return;
        }
        if (DefkillPlugin.game.getStatus() == GameStatus.INGAME) {
            final Block b = event.getBlock();
            if (event.getBlock() != null && event.getBlock().getType() == Material.ENDER_STONE) {
                DKTeam pTeam = DefkillPlugin.game.getPlayerTeam(p);
                String nexusTeam = DefkillPlugin.game.getSelectedMap().isNexus(event.getBlock().getLocation());
                if (nexusTeam != null) {
                    if (!nexusTeam.equals(pTeam.getId())) {
                        if (DefkillPlugin.game.getStage() == 1) {
                            p.sendMessage("Нельзя атаковать нексусы на первой стадии!");
                            event.setCancelled(true);
                            return;
                        }
                        DKTeam nexusTeamModel = DefkillPlugin.game.getTeam(nexusTeam);
                        int dmg = p.getCls().getNexusDamage(p.getBukkitModel().getItemInHand());
                        if (DefkillPlugin.game.getStage() == 5)
                            dmg = dmg * 2;
                        int damage = nexusTeamModel.damageNexus(dmg);
                        p.getStats().addNexusDamage(damage);
                        p.addExp(damage * EconomicSettings.nexusDamage * p.getMultiply());
                        for (DKPlayer item : DefkillPlugin.game.getPlayers().values()) {
                            DKScoreboard.updateSidebarScoreboard(item);
                            item.sendMessage("Игрок " + p.getBukkitModel().getDisplayName() + ChatColor.RESET + " атаковал нексус " + nexusTeamModel.getChatColor() + nexusTeamModel.getName(true) + " команды.");
                        }
                        if (nexusTeamModel.nexusHealth > 0) {
                            event.setCancelled(true);
                        } else {
                            event.setCancelled(true);
                            p.addExp(EconomicSettings.nexusDestroy * p.getMultiply());
                            DKBlockedBlocks.addBlock(event.getBlock().getLocation());
                            event.getBlock().setType(Material.AIR);
                        }
                    } else {
                        p.sendMessage("Нельзя нанести урон своему нексусу!");
                        event.setCancelled(true);
                    }
                    event.setCancelled(true);
                } else {
                    DKLog.getInstance().l("Как так? Нексус не найден.");
                }
            }
            if (DKBlockedBlocks.isBlocked(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
            if (b.getType() == Material.DIAMOND_ORE || b.getType() == Material.COAL_ORE || b.getType() == Material.IRON_ORE || b.getType() == Material.GOLD_ORE) {
                if (b.getType() == Material.DIAMOND_ORE)
                    DKBlockedBlocks.setAutoRefill(b.getType(), b.getData(), b.getLocation(), DefkillPlugin.pl.autoRefillDiam);
                else
                    DKBlockedBlocks.setAutoRefill(b.getType(), b.getData(), b.getLocation(), DefkillPlugin.pl.autoRefill);
                DKBlockedBlocks.addBlock(b.getLocation());
                p.getStats().addMinedOre(b.getType());
                // DOUBLE
                event.setCancelled(true);
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Drops.dropsFromOre(b.getType())));
                if (p.getCls().getDoubleChance(b.getType()) > 0) {
                    if (rand.nextInt(99) <= p.getCls().getDoubleChance(Drops.dropsFromOre(b.getType())) - 1)
                        b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Drops.dropsFromOre(b.getType())));
                }
                // END DOUBLE
                this.spawnOrb(b.getLocation(), b.getType());
                final Location loc = b.getLocation();
                Bukkit.getScheduler().scheduleSyncDelayedTask(DefkillPlugin.pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(Material.COBBLESTONE);
                    }
                }, 1L);

            }
            if (b.getType() == Material.MELON_BLOCK || b.getType() == Material.WHEAT) {
                DKBlockedBlocks.setAutoRefill(b.getType(), b.getData(), b.getLocation(), DefkillPlugin.pl.autoRefill);
                DKBlockedBlocks.addBlock(b.getLocation());
                // DOUBLE
                event.setCancelled(true);
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Drops.dropsFromOre(b.getType()), 8));
                if (p.getCls().getDoubleChance(b.getType()) > 0) {
                    if (rand.nextInt(99) <= p.getCls().getDoubleChance(Drops.dropsFromOre(b.getType())) - 1)
                        b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Drops.dropsFromOre(b.getType()), 8));
                }
                // END DOUBLE
                final Location loc = b.getLocation();
                Bukkit.getScheduler().scheduleSyncDelayedTask(DefkillPlugin.pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(Material.AIR);
                    }
                }, 1L);
            }
            if (b.getType() == Material.LOG) {
                DKBlockedBlocks.setAutoRefill(b.getType(), b.getData(), b.getLocation(), DefkillPlugin.pl.autoRefill);
                DKBlockedBlocks.addBlock(b.getLocation());
                p.getStats().addMinedOre(b.getType());
                // DOUBLE
                event.setCancelled(true);
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Drops.dropsFromOre(b.getType())));
                if (p.getCls().getDoubleChance(Material.LOG) > 0) {
                    if (rand.nextInt(99) <= p.getCls().getDoubleChance(Drops.dropsFromOre(b.getType())) - 1)
                        b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Drops.dropsFromOre(b.getType())));
                }
                // END DOUBLE
                final Location loc = b.getLocation();
                Bukkit.getScheduler().scheduleSyncDelayedTask(DefkillPlugin.pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(Material.AIR);
                    }
                }, 1L);
            }

            if (b.getType() == Material.GRAVEL) {
                DKBlockedBlocks.setAutoRefill(b.getType(), b.getData(), b.getLocation(), DefkillPlugin.pl.autoRefill);
                DKBlockedBlocks.addBlock(b.getLocation());
                event.setCancelled(true);
                if (rand.nextInt(50) > 20)
                    b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.STRING));
                else
                    b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.FLINT));
                if (rand.nextInt(50) > 20)
                    b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.ARROW, 2));
                else
                    b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.FEATHER));
                if (rand.nextInt(50) > 15)
                    b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.BONE));
                final Location loc = b.getLocation();
                Bukkit.getScheduler().scheduleSyncDelayedTask(DefkillPlugin.pl, new Runnable() {
                    public void run() {
                        loc.getBlock().setType(Material.COBBLESTONE);
                    }
                }, 1L);
            }
        }
    }

    public void spawnOrb(Location loc, Material m) {
        switch (m) {
            case IRON_ORE:
                ((ExperienceOrb) loc.getWorld().spawn(loc, ExperienceOrb.class)).setExperience(1);
            case GOLD_ORE:
                ((ExperienceOrb) loc.getWorld().spawn(loc, ExperienceOrb.class)).setExperience(1);
            case COAL_ORE:
                ((ExperienceOrb) loc.getWorld().spawn(loc, ExperienceOrb.class)).setExperience(1);
            case DIAMOND_ORE:
                ((ExperienceOrb) loc.getWorld().spawn(loc, ExperienceOrb.class)).setExperience(2);
        }
    }

    public boolean hasSign(Block block) {
        if (this.isConnectedSign(block.getRelative(BlockFace.NORTH), block)) return true;
        if (this.isConnectedSign(block.getRelative(BlockFace.SOUTH), block)) return true;
        if (this.isConnectedSign(block.getRelative(BlockFace.EAST), block)) return true;
        if (this.isConnectedSign(block.getRelative(BlockFace.WEST), block)) return true;
        if (this.isConnectedSign(block.getRelative(BlockFace.UP), block)) return true;
        return false;
    }

    public boolean isConnectedSign(Block m, Block t) {
        if (m.getType() == Material.SIGN || m.getType() == Material.SIGN_POST || m.getType() == Material.WALL_SIGN) {
            org.bukkit.material.Sign s = (org.bukkit.material.Sign) m.getState().getData();
            if (m.getRelative(s.getAttachedFace()) == t.getLocation())
                return true;
        }
        return false;
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            event.setCancelled(true);
        }
    }

}
