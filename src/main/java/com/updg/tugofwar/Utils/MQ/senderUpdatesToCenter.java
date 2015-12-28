package com.updg.tugofwar.Utils.MQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.updg.tugofwar.TOWPlugin;
import com.updg.tugofwar.Models.enums.GameStatus;
import com.updg.tugofwar.Utils.L;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Alex
 * Date: 29.10.13  21:51
 */
public class senderUpdatesToCenter {
    private Channel c = null;
    public static senderUpdatesToCenter instance = null;

    public senderUpdatesToCenter() {
        if (instance != null)
            return;
        try {
            c = qConnection.c.createChannel();
        } catch (IOException e) {
            L.$(Level.INFO, "FATAL ERROR: Cant create channel for sender to all servers");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(TOWPlugin.getInstance());
            return;
        }
        try {
            c.queueDeclare("serversUpdates", true, false, false, null);
        } catch (IOException e) {
            L.$(Level.INFO, "FATAL ERROR: cant declare channel for sender to all servers");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(TOWPlugin.getInstance());
        }
    }

    public static void send() {
        String s = GameStatus.WAITING.toString();
        if (TOWPlugin.game.getMaxPlayers() <= TOWPlugin.game.getActivePlayers())
            s = "IN_GAME";
        if (TOWPlugin.game.getStatus() == GameStatus.WAITING) {
            if (TOWPlugin.game.tillGame < TOWPlugin.game.tillGameDefault)
                send(TOWPlugin.getInstance().serverId + ":" + s + ":" + "В ОЖИДАНИИ" + ":" + TOWPlugin.game.getActivePlayers() + ":" + TOWPlugin.game.getMaxPlayers() + ":До игры " + TOWPlugin.game.tillGame + " c.");
            else
                send(TOWPlugin.getInstance().serverId + ":" + s + ":" + "В ОЖИДАНИИ" + ":" + TOWPlugin.game.getActivePlayers() + ":" + TOWPlugin.game.getMaxPlayers() + ":Набор игроков");
        } else if (TOWPlugin.game.getStatus() == GameStatus.PRE_GAME)
            send(TOWPlugin.getInstance().serverId + ":IN_GAME:" + "НАЧАЛО" + ":" + TOWPlugin.game.getActivePlayers() + ":" + TOWPlugin.game.getMaxPlayers());
        else if (TOWPlugin.game.getStatus() == GameStatus.POSTGAME) {
            send(TOWPlugin.getInstance().serverId + ":IN_GAME:" + "ИГРА ОКОНЧЕНА" + ":" + TOWPlugin.game.getActivePlayers() + ":" + TOWPlugin.game.getMaxPlayers() + ":Победил " + TOWPlugin.game.winner.getName());
        } else if (TOWPlugin.game.getStatus() == GameStatus.INGAME || TOWPlugin.game.getStatus() == GameStatus.POSTGAME)
            send(TOWPlugin.getInstance().serverId + ":IN_GAME:" + "ИГРА" + ":" + TOWPlugin.game.getActivePlayers() + ":" + TOWPlugin.game.getMaxPlayers() + ":Бой");
        else if (TOWPlugin.game.getStatus() == GameStatus.RELOAD)
            send(TOWPlugin.getInstance().serverId + ":DISABLED:" + "ОФФЛАЙН" + ":0:0:");

    }

    public static void send(String msg) {
        if (instance == null)
            instance = new senderUpdatesToCenter();
        try {
            instance.c.basicPublish("", "serversUpdates",
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    msg.getBytes());
        } catch (IOException e) {
            L.$(Level.INFO, "ERROR: cant sent message to all servers");
            e.printStackTrace();
        }
    }
}
