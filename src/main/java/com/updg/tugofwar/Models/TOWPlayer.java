package com.updg.tugofwar.Models;

import com.updg.tugofwar.Models.enums.GameStatus;
import com.updg.tugofwar.TOWPlugin;
import com.updg.tugofwar.Utils.Bungee.Bungee;
import com.updg.tugofwar.Utils.dataServer.DSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by Alex
 * Date: 15.12.13  13:37
 */
public class TOWPlayer {
    private int id;
    private String name;

    private Player bukkitModel;
    private TOWPlayerStats stats;

    private double exp = 0;

    private boolean wasInGame = false;

    private TOWTeam team;
    private TOWClass cl;

    public TOWPlayer(Player p) {
        this.setBukkitModel(p);
        this.name = p.getName();
        this.stats = new TOWPlayerStats();
        this.getIdFromBungee();
    }

    private void getIdFromBungee() {
        Bungee.isLogged(getBukkitModel(), getName());
    }

    public Player getBukkitModel() {
        return bukkitModel;
    }

    public void setBukkitModel(Player bukkitModel) {
        this.bukkitModel = bukkitModel;
    }

    public TOWPlayerStats getStats() {
        return stats;
    }

    public void provideStaff() {
        this.cl.provideStaff(this);
        this.getTeam().provideWool(this, 32);
    }

    public void sendMessage(String s) {
        getBukkitModel().sendMessage(s);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setSpectator(boolean b) {
        if (b) {
            this.hidePlayer();
            this.bukkitModel.setAllowFlight(true);
        } else {
            this.showPlayer();
            this.bukkitModel.setAllowFlight(false);
        }
    }

    private void hidePlayer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(this.bukkitModel);
        }
    }

    private void showPlayer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(this.bukkitModel);
        }
    }

    public boolean isSpectator() {
        return TOWPlugin.game.isSpectator(this);
    }


    public double getExp() {
        String[] out = DSUtils.getExpAndMoney(this);
        this.setExp(Double.parseDouble(out[0]));
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public void withdrawExp(double v) {
        String[] out = DSUtils.withdrawPlayerExpAndMoney(this, v, 0);
        this.setExp(Double.parseDouble(out[0]));
    }

    public void addExp(double v) {
        String[] out = DSUtils.addPlayerExpAndMoney(this, v, 0);
        this.setExp(Double.parseDouble(out[0]));
    }

    public boolean wasInGame() {
        return wasInGame;
    }

    public void setWasInGame(boolean wasInGame) {
        this.wasInGame = wasInGame;
    }

    public TOWTeam getTeam() {
        return team;
    }

    public void setTeam(TOWTeam team) {
        this.team = team;
    }

    public void respawn() {
        if (TOWPlugin.game.getStatus() == GameStatus.INGAME) {
            if (isSpectator())
                getBukkitModel().teleport(TOWPlugin.game.getLobby());
            else
                getBukkitModel().teleport(TOWPlugin.game.getSpectatorsSpawn());
        }
    }

    public TOWClass getCls() {
        return cl;
    }
}
