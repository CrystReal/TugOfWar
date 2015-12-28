package com.updg.tugofwar;

import com.updg.tugofwar.Models.TOWPlayer;
import com.updg.tugofwar.Models.TOWTeam;
import com.updg.tugofwar.Models.enums.GameStatus;
import com.updg.tugofwar.Threads.TopBarThread;
import com.updg.tugofwar.Utils.Bungee.BungeeUtils;
import com.updg.tugofwar.Utils.EconomicSettings;
import com.updg.tugofwar.Utils.MQ.senderUpdatesToCenter;
import com.updg.tugofwar.Utils.TOWConfig;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Alex
 * Date: 17.06.13  20:26
 */
public class Game {
    private HashMap<String, TOWPlayer> players = new HashMap<String, TOWPlayer>();
    private HashMap<String, TOWPlayer> spectators = new HashMap<String, TOWPlayer>();
    private HashMap<String, TOWTeam> teams = new HashMap<String, TOWTeam>();

    private Location lobby;
    private GameStatus status;
    private int minPlayers = 0;
    private int maxPlayers = 12;

    public int tillGameDefault = 15;
    public int tillGame = 15;

    public TOWPlayer winner;
    private int tid;

    private long timeStart = 0;
    private long timeEnd = 0;

    public Location redLobbySign;
    public Location blueLobbySign;
    private int stage = 1;
    private boolean building = true;
    private Location spectatorsSpawn;

    public Game() {
        FileConfiguration c = TOWPlugin.getInstance().getConfig();
        this.lobby = TOWConfig.stringToLocation(c.getString("lobby"));
        this.spectatorsSpawn = TOWConfig.stringToLocation(c.getString("spectatorsSpawn"));
        this.redLobbySign = TOWConfig.stringToLocation(c.getString("lobbySigns.red"));
        this.blueLobbySign = TOWConfig.stringToLocation(c.getString("lobbySigns.blue"));
        // this.spawn = TOWPlugin.getInstance().stringToLoc(TOWPlugin.getInstance().getConfig().getString("spawn"));

        this.teams.put("red", new TOWTeam(c.getConfigurationSection("teams.red"), 1, "red", "Красная", "Красной"));
        this.teams.put("blue", new TOWTeam(c.getConfigurationSection("teams.blues"), 2, "blue", "Синяя", "Синей"));

        this.minPlayers = c.getInt("minPlayers");
        this.maxPlayers = c.getInt("maxPlayers");
    }

    public boolean isAbleToStart() {
        return this.status == GameStatus.WAITING && this.players.size() >= this.minPlayers;
    }

    public void getReady() {
        this.status = GameStatus.WAITING;
        new TopBarThread().start();
        senderUpdatesToCenter.send();
    }

    public void preGame() {
        tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(TOWPlugin.getInstance(), new Runnable() {
            public void run() {
                if (Bukkit.getOnlinePlayers().length < getMinPlayers()) {
                    Bukkit.broadcastMessage(TOWPlugin.prefix + "Старт игры отменен так как игрок(и) покинули сервер.");
                    senderUpdatesToCenter.send();
                    Bukkit.getScheduler().cancelTask(tid);
                    tid = 0;
                    tillGame = tillGameDefault;
                } else if (tillGame > 0) {
                    tillGame--;
                } else {
                    TOWPlugin.game.startGame();
                    Bukkit.getScheduler().cancelTask(tid);
                }
            }
        }, 0, 20);
    }

    public void startGame() {
        this.status = GameStatus.PRE_GAME;
        int i = 1;
        for (TOWPlayer p : this.players.values()) {
            p.getBukkitModel().setGameMode(GameMode.SURVIVAL);
            p.getBukkitModel().setFlying(false);
            p.getBukkitModel().setAllowFlight(false);
            p.getBukkitModel().teleport(p.getTeam().getSpawn(i));
            p.provideStaff();
            p.setWasInGame(true);
            i++;
        }
        Bukkit.broadcastMessage(TOWPlugin.prefix + "Разбегайся! До начала резни всего 10 секунд!");
        this.tillGame = 10;
        tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(TOWPlugin.getInstance(), new Runnable() {
            public void run() {
                tillGame--;
                if (tillGame == 0) {
                    for (TOWPlayer p : players.values()) {
                        p.provideStaff();
                        Bukkit.broadcastMessage(TOWPlugin.prefix + "БОЙ!");
                    }
                    status = GameStatus.INGAME;
                    Bukkit.getScheduler().cancelTask(tid);
                }
            }
        }, 0, 20);
        senderUpdatesToCenter.send();
    }

    public void endGame() {
        if (this.status == GameStatus.INGAME && this.players.size() < 2) {
            this.timeEnd = System.currentTimeMillis() / 1000L;
            for (TOWPlayer p : this.players.values()) {
                p.sendMessage(TOWPlugin.prefix + "Ты выиграл бой!");
                p.getStats().setInGameTime(System.currentTimeMillis() / 1000L - this.timeStart);
                winner = p;
                winner.addExp(EconomicSettings.win);
            }
            for (Player p1 : Bukkit.getOnlinePlayers()) {
                if (winner != null && !p1.getName().equals(winner.getName())) {
                    p1.sendMessage(TOWPlugin.prefix + "Игрок " + winner.getName() + " выиграл!");
                }
            }
        } else {
            for (TOWPlayer p : this.players.values()) {
                p.sendMessage(TOWPlugin.prefix + "Игра остановлена системой.");
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(TOWPlugin.prefix + "Сервер перезагрузится через 15 секунд.");
            p.getInventory().clear();
        }
        new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(TOWPlugin.prefix + "Сервер перезагрузится через 10 секунд.");
                            }
                            Thread.sleep(5000);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(TOWPlugin.prefix + "Сервер перезагрузится через 5 секунд.");
                            }
                            Thread.sleep(5000);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendMessage(TOWPlugin.prefix + "Сервер перезагружается.");
                            }
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        for (Player p2 : Bukkit.getOnlinePlayers()) {
                            BungeeUtils.teleportPlayer(p2, "lobby");
                        }
                        TOWPlugin.getInstance().sendStats();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        senderUpdatesToCenter.send();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
                    }
                }).start();
    }

    @Deprecated
    public void killPlayer(Player p) {
        if (this.status == GameStatus.INGAME && this.players.containsKey(p.getName())) {
            TOWPlayer pl = this.players.get(p.getName());
            for (Player p1 : Bukkit.getOnlinePlayers()) {
                if (!p1.getName().equals(p.getName())) {
                    p.sendMessage(TOWPlugin.prefix + p.getName() + " выбыл.");
                }
            }
            pl.sendMessage(TOWPlugin.prefix + "Ты погиб в бою.");
            pl.getStats().setPosition(this.players.size());
            pl.getStats().setInGameTime(System.currentTimeMillis() / 1000L - this.timeStart);
            pl.getBukkitModel().closeInventory();
            pl.getBukkitModel().getInventory().clear();
            this.players.remove(pl.getName());
            this.spectators.put(pl.getName(), pl);
            pl.setSpectator(true);
            pl.getBukkitModel().teleport(getLobby());

            if (this.players.size() < 2) {
                TOWPlugin.game.endGame();
            }
        } else {
            p.teleport(getLobby());
        }
        senderUpdatesToCenter.send();
    }

    public Location getLobby() {
        return this.lobby;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getActivePlayers() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public TOWPlayer getPlayer(String name) {
        if (this.players.containsKey(name))
            return this.players.get(name);
        if (this.spectators.containsKey(name))
            return this.spectators.get(name);
        return null;
    }

    public void addSpectator(TOWPlayer p) {
        this.spectators.put(p.getName(), p);
    }

    public boolean isSpectator(TOWPlayer p) {
        return this.spectators.containsKey(p.getName());
    }

    public void addPlayer(TOWPlayer p) {
        this.players.put(p.getName(), p);
    }

    public HashMap<String, TOWPlayer> getPlayers() {
        return players;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public Collection<TOWPlayer> getActivePlayersArray() {
        return this.players.values();
    }

    public Collection<TOWPlayer> getSpectatorsArray() {
        return this.spectators.values();
    }

    public void removeSpectator(TOWPlayer p) {
        if (p.isSpectator())
            this.spectators.remove(p.getName());
    }

    public void removePlayer(TOWPlayer p) {
        if (this.players.containsKey(p.getName()))
            this.players.remove(p.getName());
    }

    public int getStage() {
        return stage;
    }

    public boolean isBuilding() {
        return building;
    }

    public void rebuidField() {
        //TODO
    }

    public Location getSpectatorsSpawn() {
        return spectatorsSpawn;
    }
}
