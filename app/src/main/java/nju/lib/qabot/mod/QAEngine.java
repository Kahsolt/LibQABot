package nju.lib.qabot.mod;

import androidx.appcompat.app.AppCompatActivity;

public class QAEngine extends Engine {

    public QAEngine(AppCompatActivity app) { super(app); }

    public String query(String question) {
        statusShow("querying");
        return String.format("You've asked %s", question);
    }

}
