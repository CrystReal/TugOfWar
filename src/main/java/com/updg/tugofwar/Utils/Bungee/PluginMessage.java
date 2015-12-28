package com.updg.tugofwar.Utils.Bungee;

import com.updg.tugofwar.TOWPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Alex
 * Date: 18.06.13  19:56
 */
public class PluginMessage implements PluginMessageListener {
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        System.out.println(channel.toString() + " :: " + message.toString());
        if (!channel.equals("StreamBungee"))
            return;
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(
                message));

        String channel1 = null;
        try {
            channel1 = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (channel1 != null && channel1.equals("isLoggedBack")) {
            String msg = null;
            Boolean online = false;
            int id = 0;
            try {
                msg = in.readUTF();
                online = in.readBoolean();
                if (online)
                    id = in.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!online) {
                BungeeUtils.teleportPlayer(Bukkit.getPlayer(msg), "lobby");
            } else {
                TOWPlugin.game.getPlayer(msg).setId(id);
            }
        }
    }
}