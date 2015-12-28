package com.updg.tugofwar.Utils;

import com.updg.tugofwar.TOWPlugin;

import java.util.logging.Level;

/**
 * Created by Alex
 * Date: 15.12.13  13:17
 */
public class L {
    public static void $(String str) {
        TOWPlugin.getInstance().getLogger().log(Level.INFO, str);
    }

    public static void $(Level l, String str) {
        TOWPlugin.getInstance().getLogger().log(l, str);
    }
}
