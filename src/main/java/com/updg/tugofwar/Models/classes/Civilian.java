package com.updg.tugofwar.Models.classes;

import com.updg.tugofwar.Models.TOWClass;
import com.updg.tugofwar.Models.TOWPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Alex
 * Date: 17.12.13  15:37
 */
public class Civilian implements TOWClass {
    public void provideStaff(TOWPlayer p) {
        p.getBukkitModel().getInventory().setItem(0, new ItemStack(Material.BOW, 1));
        p.getBukkitModel().getInventory().setItem(10, new ItemStack(Material.ARROW, 1));
    }

    public boolean canMoveEverywhere() {
        return false;
    }
}
