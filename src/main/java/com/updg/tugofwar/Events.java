package com.updg.tugofwar;

import com.sk89q.worldedit.blocks.BlockType;
import com.updg.tugofwar.Models.TOWPlayer;
import com.updg.tugofwar.Models.enums.GameStatus;
import com.updg.tugofwar.Utils.MQ.senderUpdatesToCenter;
import com.updg.tugofwar.Utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

/**
 * Created by Alex
 * Date: 17.06.13  19:46
 */
public class Events implements Listener {

    int tid = 0;
    int count = 10;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Player user = event.getPlayer();
        TOWPlayer p = TOWPlugin.game.getPlayer(user.getName());
        if (p == null) {
            p = new TOWPlayer(user);
            if (TOWPlugin.game.getStatus() == GameStatus.WAITING) {
                if (TOWPlugin.game.getActivePlayers() < TOWPlugin.game.getMaxPlayers())
                    TOWPlugin.game.addPlayer(p);
                else
                    TOWPlugin.game.addSpectator(p);
            } else {
                TOWPlugin.game.addSpectator(p);
            }
        }
        while (true) {
            if (p.getId() == 0)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            else
                break;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        TOWPlayer p = TOWPlugin.game.getPlayer(e.getPlayer().getName());
        e.getPlayer().teleport(TOWPlugin.game.getLobby());
        if (TOWPlugin.game.getStatus() != GameStatus.WAITING) {
            p.sendMessage(TOWPlugin.prefix + "Игра уже началась.");
        } else {
            if (p.isSpectator()) {
                p.sendMessage(TOWPlugin.prefix + "В игре нет свободных мест. Вы зашли как налюбдающий!");
            } else {
                e.setJoinMessage(TOWPlugin.prefix + e.getPlayer().getName() + " вошел на арену. " + Bukkit.getOnlinePlayers().length + "/" + TOWPlugin.game.getMinPlayers());
                if (TOWPlugin.game.isAbleToStart()) {
                    TOWPlugin.game.preGame();
                } else {
                    senderUpdatesToCenter.send();
                    e.getPlayer().sendMessage(TOWPlugin.prefix + "Игра начнется когда наберется " + TOWPlugin.game.getMinPlayers() + " " + StringUtil.plural(TOWPlugin.game.getMinPlayers(), "игрок", "игрока", "игроков"));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        /*if (TOWPlugin.game.getPlayers().containsKey(e.getPlayer().getName())) {
            TOWPlugin.game.killPlayer(e.getPlayer());
        } */
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();

            Player player = (Player) e.getEntity();
            if (e.getDamager() instanceof Arrow) {
                if (TOWPlugin.game.getPlayer(player.getName()).getTeam().getId() == TOWPlugin.game.getPlayer(p.getName()).getTeam().getId()) {
                    TOWPlugin.game.getPlayer(p.getName()).sendMessage(TOWPlugin.prefix + "Нелья наносить урон своим!");
                } else {
                    TOWPlugin.game.getPlayer(player.getName()).respawn();
                    TOWPlugin.game.getPlayer(player.getName()).getTeam().subScore();
                    TOWPlugin.game.getPlayer(p.getName()).getTeam().addScore();
                    TOWPlugin.game.getPlayer(p.getName()).getStats().addKill();
                    TOWPlugin.game.rebuidField();
                    Bukkit.broadcastMessage(TOWPlugin.prefix + p.getDisplayName() + " убил " + player.getDisplayName() + ".");
                }
            } else {
                if (p.isDead()) {
                    TOWPlugin.game.getPlayer(player.getName()).respawn();
                    TOWPlugin.game.getPlayer(player.getName()).getTeam().subScore();
                    TOWPlugin.game.getPlayer(p.getName()).getTeam().addScore();
                    TOWPlugin.game.getPlayer(p.getName()).getStats().addKill();
                    TOWPlugin.game.rebuidField();
                    Bukkit.broadcastMessage(TOWPlugin.prefix + p.getDisplayName() + " убил " + player.getDisplayName() + ".");
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
        e.getPlayer().updateInventory();
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        TOWPlayer p = TOWPlugin.game.getPlayer(e.getPlayer().getName());
        p.respawn();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
        TOWPlayer p = TOWPlugin.game.getPlayer(e.getPlayer().getName());
        if (e.getBlock().getType() == Material.WOOL)
            if (TOWPlugin.game.getStatus() == GameStatus.INGAME) {
                if (TOWPlugin.game.isBuilding() && ((Wool) e.getBlock()).getColor() != p.getTeam().getWoolColor()) {
                    e.setCancelled(true);
                    p.sendMessage(TOWPlugin.prefix + "Нельзя разбивать блоки чужой команды в период стройки!");
                }
            } else
                e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (TOWPlugin.game.getStatus() == GameStatus.PRE_GAME) {
            e.setCancelled(true);
            return;
        }
        TOWPlayer p = TOWPlugin.game.getPlayer(e.getPlayer().getName());
        if (TOWPlugin.game.getStatus() == GameStatus.INGAME && !p.isSpectator()) {
            if (!p.getTeam().isTeamTerritory(e.getTo().getX())) {
                if (!TOWPlugin.game.isBuilding()) {
                    if (!p.getCls().canMoveEverywhere()) {
                        p.getBukkitModel().setVelocity(p.getTeam().getSpawn(1).getDirection());
                        p.getBukkitModel().setVelocity(new Vector(p.getBukkitModel().getVelocity().getX(), 1.0D, p.getBukkitModel().getVelocity().getZ()));
                        p.sendMessage(TOWPlugin.prefix + "Ты не можешь заходить на территорию противника!");
                    }
                } else {
                    p.getBukkitModel().setVelocity(p.getTeam().getSpawn(1).getDirection());
                    p.getBukkitModel().setVelocity(new Vector(p.getBukkitModel().getVelocity().getX(), 1.0D, p.getBukkitModel().getVelocity().getZ()));
                    p.sendMessage(TOWPlugin.prefix + "Ты не можешь заходить на территорию противника во период стройки!");
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        if (TOWPlugin.game.getPlayer(((Player) event.getEntity().getShooter()).getName()).isSpectator()) {
            event.setCancelled(true);
            return;
        }
        if (TOWPlugin.game.isBuilding()) {
            event.setCancelled(true);
            TOWPlugin.game.getPlayer(((Player) event.getEntity().getShooter()).getName()).sendMessage(TOWPlugin.prefix + "Нелья стрелять во время постройки!");
        }

        ((Player) event.getEntity().getShooter()).getInventory().addItem(new ItemStack(Material.ARROW, 1));
        ((Player) event.getEntity().getShooter()).updateInventory();
        TOWPlugin.game.getPlayer(((Player) event.getEntity().getShooter()).getName()).getStats().addShot();
    }

    @EventHandler
    public void inventory(InventoryDragEvent e) {
        if (TOWPlugin.game.getPlayer(((Player) e.getWhoClicked()).getName()).isSpectator()) {
            e.setCancelled(true);
        }
    }
}
