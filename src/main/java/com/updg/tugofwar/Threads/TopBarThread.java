package com.updg.tugofwar.Threads;

import com.updg.tugofwar.TOWPlugin;
import com.updg.tugofwar.Models.enums.GameStatus;
import com.updg.tugofwar.Utils.BarAPI;
import com.updg.tugofwar.Utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Alex
 * Date: 06.12.13  16:14
 */
public class TopBarThread extends Thread implements Runnable {
    public void run() {
        try {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (TOWPlugin.game.getStatus() == GameStatus.WAITING) {
                    if (TOWPlugin.game.tillGame != 120)
                        BarAPI.getInstance().setStatus(p, ChatColor.GREEN + "До игры" + StringUtil.plural(TOWPlugin.game.tillGame, " осталась " + TOWPlugin.game.tillGame + " секунда", " осталось " + TOWPlugin.game.tillGame + " секунды", " осталось " + TOWPlugin.game.tillGame + " секунд") + ".", TOWPlugin.game.tillGame / (TOWPlugin.game.tillGameDefault / 100), false);
                    else if (TOWPlugin.game.getActivePlayers() < TOWPlugin.game.getMinPlayers())
                        BarAPI.getInstance().setStatus(p, ChatColor.GREEN + "Ожидаем игроков.", TOWPlugin.game.getActivePlayers() * (TOWPlugin.game.getMinPlayers() / 100), false);
                    else
                        BarAPI.getInstance().setStatus(p, ChatColor.GREEN + "Ожидаем игроков.", 100, false);
                }
                if (TOWPlugin.game.getStatus() == GameStatus.PRE_GAME) {
                    BarAPI.getInstance().setStatus(p, ChatColor.RED + "Разбегайся! До резни " + StringUtil.plural(TOWPlugin.game.tillGame, " осталась " + TOWPlugin.game.tillGame + " секунда", " осталось " + TOWPlugin.game.tillGame + " секунды", " осталось " + TOWPlugin.game.tillGame + " секунд") + ".", TOWPlugin.game.tillGame / (10 / 100), false);
                }
                if (TOWPlugin.game.getStatus() == GameStatus.INGAME) {
                    BarAPI.getInstance().setStatus(p, ChatColor.GREEN + "Бой", TOWPlugin.game.getActivePlayers() * (TOWPlugin.game.getMinPlayers() / 100), false);
                }
                if (TOWPlugin.game.getStatus() == GameStatus.POSTGAME) {
                    BarAPI.getInstance().setStatus(p, ChatColor.AQUA + "Победил " + TOWPlugin.game.winner.getName(), 1, false);
                }
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
