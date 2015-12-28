package com.updg.tugofwar.Utils.Bungee;

import com.updg.tugofwar.TOWPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;

public class PluginMessageTask extends BukkitRunnable {
    private final ByteArrayOutputStream bytes;
    private final Player player;

    public PluginMessageTask(Player player, ByteArrayOutputStream bytes) {
        this.bytes = bytes;
        this.player = player;
    }

    public void run() {
        this.player.sendPluginMessage(TOWPlugin.getInstance(), "StreamBungee", this.bytes.toByteArray());
    }
}