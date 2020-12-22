package nju.lib.qabot.mod;

import android.util.Log;

import nju.lib.qabot.MainActivity;

public abstract class Engine {

    protected final MainActivity app;

    public Engine(MainActivity app) { this.app = app; }

    public void init() { /* to @Override */ }

    protected void statusShow(String message) {
        Log.i(this.getClass().getSimpleName(), message);
        app.tx_status.append(message + '\n');
    }

}
