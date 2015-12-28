package com.updg.tugofwar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.updg.tugofwar.DataServerStats.gameStats;
import com.updg.tugofwar.DataServerStats.playerStats;
import com.updg.tugofwar.Models.TOWPlayer;
import com.updg.tugofwar.Models.enums.GameStatus;
import com.updg.tugofwar.Utils.Bungee.PluginMessage;
import com.updg.tugofwar.Utils.FileUtils;
import com.updg.tugofwar.Utils.MQ.listenerFromAllServers;
import com.updg.tugofwar.Utils.MQ.qConnection;
import com.updg.tugofwar.Utils.dataServer.DSUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 17.06.13
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */
public class TOWPlugin extends JavaPlugin {
    public static String prefix = ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "[Взрывной Сплиф] " + ChatColor.RESET;
    public static int floor;
    public static Game game;
    private static TOWPlugin instance;
    public int serverId = 0;

    public static TOWPlugin getInstance() {
        return instance;
    }

    public void onLoad() {
        ArrayList<String> worlds = new ArrayList<String>();
        File folder = new File("./orig");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null)
            return;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isDirectory()) {
                worlds.add(listOfFile.getName());
            }
        }
        File tmp;
        for (String item : worlds) {
            tmp = new File(getServer().getWorldContainer(), item);
            FileUtils.deleteDirectory(tmp);
            try {
                File srcFolder = new File("./orig/" + item);
                File destFolder = new File(getServer().getWorldContainer(), item);
                FileUtils.copyFolder(srcFolder, destFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onEnable() {
        TOWPlugin.instance = this;
        this.serverId = getConfig().getInt("serverId", 0);
        TOWPlugin.floor = getConfig().getInt("floor", 256);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "StreamBungee");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this,
                "StreamBungee", new PluginMessage());
        qConnection.connect(this.getConfig().getString("qServerHost", "localhost"));
        new listenerFromAllServers().start();

        getServer().getPluginManager().registerEvents(new Events(), this);
        game = new Game();
        game.getReady();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Недостаточно прав");
            return true;
        }
        TOWPlayer p;
        if (sender instanceof Player) {
            p = game.getPlayer(sender.getName());
        } else {
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("setspawn")) {
            game.setSpawn(((Player) sender).getLocation());
            getConfig().set("spawn", game.getSpawn().getWorld().getName() + "|" + game.getSpawn().getX() + "|" + game.getSpawn().getY() + "|" + game.getSpawn().getZ());
            this.saveConfig();
        }
        if (cmd.getName().equalsIgnoreCase("setlobby")) {
            getConfig().set("lobby", ((Player) sender).getLocation().getWorld().getName() + "|" + ((Player) sender).getLocation().getX() + "|" + ((Player) sender).getLocation().getY() + "|" + ((Player) sender).getLocation().getZ());
            this.saveConfig();
        }
        if (cmd.getName().equalsIgnoreCase("setfloor")) {
            getConfig().set("floor", ((Player) sender).getLocation().getY() - 1);
            this.saveConfig();
        }
        if (cmd.getName().equalsIgnoreCase("spectate")) {
            if (p.getBukkitModel().hasPermission("dk.spectate")) {
                if (p.isSpectator() || (game.getStatus() == GameStatus.INGAME || game.getStatus() == GameStatus.POSTGAME)) {
                    p.sendMessage("Нельзя менять статус во время игры.");
                    return false;
                }
                if (p.isSpectator() && game.getActivePlayers() >= game.getMaxPlayers()) {
                    p.sendMessage("Ошибка смены статуса. Сервер полный.");
                    return false;
                }
                if (p.isSpectator()) {
                    p.setSpectator(false);
                    game.removeSpectator(p);
                    game.addPlayer(p);
                    p.sendMessage("Теперь ты обычный игрок");
                } else {
                    p.setSpectator(true);
                    game.removePlayer(p);
                    game.addSpectator(p);
                    p.sendMessage("Теперь ты наблюдающий");
                }
            } else {
                p.sendMessage(ChatColor.RED + "Не достаточно прав");
            }
        }
        return false;
    }

    public Location stringToLoc(String string) {
        String[] loc = string.split("\\|");
        World world = Bukkit.getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);

        return new Location(world, x, y, z);
    }

    public void sendStats() {
        gameStats game = new gameStats();
        game.setServerId(this.serverId);
        game.setWinner(TOWPlugin.game.winner.getId());
        game.setStart(TOWPlugin.game.getTimeStart());
        game.setEnd(TOWPlugin.game.getTimeEnd());
        List<playerStats> players = new ArrayList<playerStats>();
        playerStats tmpPlayer;
        for (TOWPlayer p : TOWPlugin.game.getActivePlayersArray()) {
            tmpPlayer = new playerStats();
            tmpPlayer.setPlayerId(p.getId());
            tmpPlayer.setIsWinner(p.getId() == game.getWinner());
            tmpPlayer.setTimeInGame(p.getStats().getInGameTime());
            tmpPlayer.setShots(p.getStats().getShots());
            tmpPlayer.setPosition(p.getStats().getPosition());
            players.add(tmpPlayer);
        }
        for (TOWPlayer p : TOWPlugin.game.getSpectatorsArray()) {
            if (p.wasInGame()) {
                tmpPlayer = new playerStats();
                tmpPlayer.setPlayerId(p.getId());
                tmpPlayer.setIsWinner(p.getId() == game.getWinner());
                tmpPlayer.setTimeInGame(p.getStats().getInGameTime());
                tmpPlayer.setShots(p.getStats().getShots());
                tmpPlayer.setPosition(p.getStats().getPosition());
                players.add(tmpPlayer);
            }
        }
        game.setPlayers(players);
        try {
            String stat = new ObjectMapper().writeValueAsString(game);
            DSUtils.sendStats("tugofwar", stat);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
