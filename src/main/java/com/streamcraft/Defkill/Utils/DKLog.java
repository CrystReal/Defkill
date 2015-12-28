package com.streamcraft.Defkill.Utils;

import com.streamcraft.Defkill.DefkillPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alex
 * Date: 24.10.13  0:41
 */
public class DKLog {
    public static DKLog instance = null;
    public final static Logger log = Logger.getLogger("Minecraft");
    public String prefix = "[Defkill] ";

    public DKLog() {
        instance = this;
    }

    public static DKLog getInstance() {
        if (DKLog.instance == null)
            DKLog.instance = new DKLog();
        return DKLog.instance;
    }

    public void l(String msg) {
        log.log(Level.INFO, this.prefix + msg);
    }
}
