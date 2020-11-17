package nju.lib.qabot.mod;

import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import nju.lib.qabot.R;

// this class just proxies method `MainActivity.statusShow()`
public class Engine {

    private AppCompatActivity app;

    public Engine(AppCompatActivity app) { this.app = app; }

    protected void statusShow(String message) {
        Log.i(this.getClass().getSimpleName(), message);
        ((TextView) app.findViewById(R.id.tx_status)).append(message + '\n');
    }

}
