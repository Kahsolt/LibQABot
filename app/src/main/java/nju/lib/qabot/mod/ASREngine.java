package nju.lib.qabot.mod;

import androidx.appcompat.app.AppCompatActivity;

import nju.lib.qabot.util.Requests;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;


public class ASREngine extends Engine {

    static private final String RECOG_URL = String.format("http://%s:%s/recognize", "210.28.134.114", "25102");



    public ASREngine(AppCompatActivity app) { super(app); }

    public String recognize(byte[] data) {
        statusShow("audio recognizing");
        HashMap<String, String> datas = new HashMap<String, String>();
        datas.put("file", data.toString());
        JSONObject res = Requests.post(RECOG_URL, datas);
        boolean ok = res.getBoolean("ok");
        if(!ok) {
            statusShow("couldn't get the recognition result, server error?");
            System.exit(1);
        }
        String result = res.getString("result");
        return result;
    }

}
