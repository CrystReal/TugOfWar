package com.updg.tugofwar.Utils.MQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.updg.tugofwar.TOWPlugin;
import com.updg.tugofwar.Utils.L;
import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * Created by Alex
 * Date: 29.10.13  22:11
 */
public class listenerFromAllServers extends Thread {
    private Channel c = null;
    QueueingConsumer consumer = null;

    public void run() {
        try {
            c = qConnection.c.createChannel();
        } catch (Exception e) {
            L.$(Level.INFO, "FATAL ERROR: Cant create channel for lobby listener");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(TOWPlugin.getInstance());
            return;
        }
        try {
            c.exchangeDeclare("toAllServers", "fanout");
            String queueName = c.queueDeclare().getQueue();
            c.queueBind(queueName, "toAllServers", "");
            consumer = new QueueingConsumer(c);
            c.basicConsume(queueName, true, consumer);
        } catch (Exception e) {
            L.$(Level.INFO, "FATAL ERROR: cant start listen channel for lobby listener");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(TOWPlugin.getInstance());
            return;
        }
        while (true) {
            QueueingConsumer.Delivery delivery = null;
            try {
                delivery = consumer.nextDelivery();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String message = new String(delivery.getBody());
            if (message.contains("check")) {
                senderUpdatesToCenter.send();
            }
        }
    }
}
