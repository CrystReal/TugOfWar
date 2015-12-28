package com.updg.tugofwar.Utils.dataServer;

import com.updg.tugofwar.Models.TOWPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Alex
 * Date: 13.11.13  0:14
 */
public class DSUtils {
    public static BufferedReader in;
    public static PrintWriter out;

    public static void connect(String serverAddress, int port) throws IOException {
        Socket socket = new Socket(serverAddress, port);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public static String[] getExpAndMoney(TOWPlayer p) {
        out.println("getPlayerExpAndMoney\t" + p.getName());
        String response;
        try {
            response = in.readLine();
        } catch (IOException ex) {
            return null;
        }
        return response.split(":");
    }

    public static String[] withdrawPlayerExpAndMoney(TOWPlayer p, double exp, double money) {
        out.println("withdrawPlayerExpAndMoney\t" + p.getName() + ":" + exp + ":" + money);
        String response;
        try {
            response = in.readLine();
        } catch (IOException ex) {
            return null;
        }
        return response.split(":");
    }

    public static String[] addPlayerExpAndMoney(TOWPlayer p, double exp, double money) {
        out.println("addPlayerExpAndMoney\t" + p.getName() + ":" + exp + ":" + money);
        String response;
        try {
            response = in.readLine();
        } catch (IOException ex) {
            return null;
        }
        return response.split(":");
    }

    public static void sendStats(String gameName, String stat) {
        out.println("gameStat\t" + gameName + "\t" + stat);
    }
}
