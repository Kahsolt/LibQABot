package nju.lib.qabot.mod;

import androidx.appcompat.app.AppCompatActivity;

public class ASREngine extends Engine {

    public ASREngine(AppCompatActivity app) { super(app); }

    public String recognize(byte[] data) {
        statusShow("audio recognizing");
        return "I just heard you say nothing";
    }

}
