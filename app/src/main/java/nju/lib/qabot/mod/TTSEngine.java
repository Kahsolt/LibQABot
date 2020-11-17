package nju.lib.qabot.mod;

import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import nju.lib.qabot.R;
import nju.lib.qabot.util.Requests;

public class TTSEngine extends Engine {

    static private final String SYNTH_URL = String.format("http://%s:%s/synth", R.string.tts_server_ip, R.string.tts_server_port);

    private File tmpfile;

    public TTSEngine(AppCompatActivity app) {
        super(app);
        try {
            tmpfile = File.createTempFile("synth", "mp3", app.getCacheDir());
            // tmpfile.deleteOnExit();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void synth(String text) {
        // ask server
        statusShow("online synthersizing");
        HashMap<String, String> params = new HashMap<String, String>() {{
            put("text", text);
        }};
        JSONObject res = Requests.post(SYNTH_URL, params);
        boolean ok = res.getBoolean("ok");
        if (!ok) {
            statusShow("response not ok, server error?");
            return;
        }
        byte[] data = res.getBytes("data");

        // write tmpfile
        statusShow("write tmpfile");
        try {
            try (FileOutputStream fos = new FileOutputStream(tmpfile)) {
                fos.write(data);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void speak() {
        statusShow("speaking");
        try {
            try (FileInputStream fis = new FileInputStream(tmpfile)) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void synthAndSpeak(String text) {
        synth(text);
        speak();
    }

}
