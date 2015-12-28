package com.updg.tugofwar.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Alex
 * Date: 24.10.13  1:02
 */
public class TOWConfig {
    public static Location stringToLocationWithoutWorld(World world, String str) {
        String[] loc = str.split("\\|");
        Double x = Double.parseDouble(loc[0]);
        Double y = Double.parseDouble(loc[1]);
        Double z = Double.parseDouble(loc[2]);
        float yaw = Float.parseFloat(loc[3]);
        float pitch = Float.parseFloat(loc[4]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Location stringToLocation(String str) {
        String[] loc = str.split("\\|");
        World world = Bukkit.getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);
        float yaw = Float.parseFloat(loc[4]);
        float pitch = Float.parseFloat(loc[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }
}
