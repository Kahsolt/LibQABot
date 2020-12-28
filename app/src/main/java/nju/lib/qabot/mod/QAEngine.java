package nju.lib.qabot.mod;

import java.util.HashMap;

import nju.lib.qabot.MainActivity;
import nju.lib.qabot.R;
import nju.lib.qabot.util.Requests;
import nju.lib.qabot.util.Response;

public class QAEngine extends Engine {
    private String QA_API;

    public QAEngine(MainActivity app) { super(app); }

    @Override
    public void init() {
        QA_API = String.format("http://%s:%s/qa",
                app.getResources().getString(R.string.qa_server_ip),
                app.getResources().getString(R.string.qa_server_port));
        statusShow(String.format("qaUrl:%s", QA_API));
    }

    public String query(String question) {
        statusShow(String.format("GET THE QUESTION:%S",question));
        HashMap<String, String> params = new HashMap<String, String>() {{ put("question", question); }};
        Response res = Requests.post(QA_API, params);
        statusShow(String.format("Resp:%s",res));
        boolean ok = res.hasJson();
        if(ok){
            statusShow(String.format("is ok!"));
//            String result = new StringBuffer()
        }else{
            statusShow(String.format("don't receive good answer!"));
        }
        statusShow(String.format("result:%s",res.json));
        return res.json.getString("response");
//        return new StringBuffer(question).reverse().toString();
    }

}
