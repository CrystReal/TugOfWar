package com.updg.tugofwar.Models;

import com.updg.tugofwar.TOWPlugin;
import com.updg.tugofwar.Utils.TOWConfig;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alex
 * Date: 17.12.13  15:22
 */
public class TOWTeam {
    private String code;
    private String name;
    private String name1;
    private int id;
    private ArrayList<Location> spawns = new ArrayList<Location>();

    private int score = 30;
    private DyeColor woolColor;
    private double max;

    public TOWTeam(ConfigurationSection c, int id, String code, String name, String name1) {
        this.id = id;
        this.code = code;
        this.woolColor = DyeColor.valueOf(code.toUpperCase());
        this.name = name;
        this.name1 = name1;
        for (String item : c.getStringList(name + "Spawns")) {
            spawns.add(TOWConfig.stringToLocation(item));
        }
    }

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public Location getSpawn(int i) {
        try {
            return spawns.get(i - 1);
        } catch (IndexOutOfBoundsException e) {
            return spawns.get(0);
        }
    }

    public void provideWool(TOWPlayer p, int count) {
        Wool wool = new Wool(DyeColor.BROWN);
        ItemStack stack = wool.toItemStack(count);
        p.getBukkitModel().getInventory().addItem(stack);
    }

    public int getScore() {
        return score;
    }

    public void addScore() {
        score += TOWPlugin.game.getStage();
    }

    public void addScore(int i) {
        score += i;
    }

    public void subScore() {
        score -= TOWPlugin.game.getStage();
    }

    public void subScore(int i) {
        score -= i;
    }

    public Location getSpawn() {
        Random r = new Random();
        return this.spawns.get(r.nextInt(this.spawns.size() - 1));
    }

    public DyeColor getWoolColor() {
        return woolColor;
    }

    public double getMax() {
        //TODO
        return max;
    }

    public boolean isTeamTerritory(double x) {
        if (this.id == 1) {
            return x <= getMax();
        } else {
            return x >= getMax();
        }
    }
}
