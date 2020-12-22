package nju.lib.qabot.mod;

import nju.lib.qabot.MainActivity;

public class QAEngine extends Engine {

    public QAEngine(MainActivity app) { super(app); }

    @Override
    public void init() { }

    public String query(String question) {
        statusShow("querying");
        return new StringBuffer(question).reverse().toString();
    }

}
