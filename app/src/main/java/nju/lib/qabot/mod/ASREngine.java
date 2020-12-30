package nju.lib.qabot.mod;

import nju.lib.qabot.MainActivity;
import nju.lib.qabot.R;
import nju.lib.qabot.util.Requests;
import nju.lib.qabot.util.Response;

public class ASREngine extends Engine {

    private String ASR_API;

    public ASREngine(MainActivity app) { super(app); }

    @Override
    public void init() {
        ASR_API = String.format("http://%s:%s/synth", app.getResources().getString(R.string.asr_server_ip), app.getResources().getString(R.string.asr_server_port));
        statusShow(String.format("ASR_API: %s", ASR_API));
    }

    public String recognize(byte[] data) {
        statusShow("audio recognizing");
        Response res = Requests.post(ASR_API, data);
        statusShow(res.toString());
        if (res.hasJson()) {
            return res.json.getString("result");
        }
        return "no json";
    }

}