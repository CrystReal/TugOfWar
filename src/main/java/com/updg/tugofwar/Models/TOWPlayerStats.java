package com.updg.tugofwar.Models;

/**
 * Created by Alex
 * Date: 15.12.13  13:40
 */
public class TOWPlayerStats {
    private int shots = 0;
    private boolean winner = false;
    private int position = 1;
    private long inGameTime = 0;
    private int kills = 0;

    public int getShots() {
        return shots;
    }

    public void addShot() {
        this.shots++;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getInGameTime() {
        return inGameTime;
    }

    public void setInGameTime(long inGameTime) {
        this.inGameTime = inGameTime;
    }

    public void addKill() {
        this.kills++;
    }

    public int getKills() {
        return kills;
    }
}
