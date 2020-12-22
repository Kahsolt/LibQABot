package nju.lib.qabot.mod;

import android.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import nju.lib.qabot.MainActivity;
import nju.lib.qabot.R;
import nju.lib.qabot.util.Requests;
import nju.lib.qabot.util.Response;

public class TTSEngine extends Engine {

    private String TTS_API;
    private File tmpFile;

    public TTSEngine(MainActivity app) { super(app); }

    @Override
    public void init() {
        TTS_API = String.format("http://%s:%s/synth", app.getResources().getString(R.string.tts_server_ip), app.getResources().getString(R.string.tts_server_port));
        statusShow(String.format("synthUrl: %s", TTS_API));
        tmpFile = new File(app.getCacheDir().getAbsolutePath(), "synth.mp3");
        statusShow(String.format("tmpFile: %s", tmpFile));
    }

    public boolean synth(String text) {
        statusShow(String.format("GET: %s", text));
        HashMap<String, String> params = new HashMap<String, String>() {{ put("text", text); }};
        Response res = Requests.get(TTS_API, params);

        statusShow(String.format("Resp: %s", res));
        boolean ok = res.hasFile();
        if (!ok) {
            statusShow("response not ok, server error?");
        } else {
            res.file.save(tmpFile);
        }
        return ok;
    }

    public void speak() {
        statusShow("speaking");
        try {
            try (FileInputStream fis = new FileInputStream(tmpFile)) {
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(fis.getFD());
                player.prepare();
                player.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void synthAndSpeak(String text) {
        if (synth(text)) speak();
    }

}
