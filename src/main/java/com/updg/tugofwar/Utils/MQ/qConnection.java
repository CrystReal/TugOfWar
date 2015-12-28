package com.updg.tugofwar.Utils.MQ;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.updg.tugofwar.TOWPlugin;
import com.updg.tugofwar.Utils.L;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Alex
 * Date: 29.10.13  20:40
 */
public class qConnection {
    public static Connection c = null;

    public static Connection connect(String host) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            c = factory.newConnection();
        } catch (IOException e) {
            L.$(Level.WARNING, "FATAL ERROR: Cant connect to the Q server");
            Bukkit.getPluginManager().disablePlugin(TOWPlugin.getInstance());

        }
        return c;
    }
}
