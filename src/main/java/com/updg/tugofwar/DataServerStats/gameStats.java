package com.updg.tugofwar.DataServerStats;

import java.util.List;

/**
 * Created by Alex
 * Date: 17.11.13  2:16
 */
public class gameStats {
    private int _serverId;
    private int _winner;
    private long _start;
    private long _end;
    private List<playerStats> _players;

    public List<playerStats> getPlayes() {
        return _players;
    }

    public void setPlayers(List<playerStats> _players) {
        this._players = _players;
    }

    public int getServerId() {
        return _serverId;
    }

    public void setServerId(int _serverId) {
        this._serverId = _serverId;
    }

    public int getWinner() {
        return _winner;
    }

    public void setWinner(int _winner) {
        this._winner = _winner;
    }

    public long getStart() {
        return _start;
    }

    public void setStart(long _start) {
        this._start = _start;
    }

    public long getEnd() {
        return _end;
    }

    public void setEnd(long _end) {
        this._end = _end;
    }
}
