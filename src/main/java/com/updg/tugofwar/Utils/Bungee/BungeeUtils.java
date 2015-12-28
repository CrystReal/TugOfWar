package com.updg.tugofwar.Utils.Bungee;

import com.updg.tugofwar.TOWPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alex
 * Date: 30.10.13  0:05
 */
public class BungeeUtils {
    public static void teleportPlayer(Player p, String url) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(url);
        } catch (IOException eee) {
            Bukkit.getLogger().info("You'll never see me!");
        }
        p.sendPluginMessage(TOWPlugin.getInstance(), "BungeeCord", b.toByteArray());
    }
}
