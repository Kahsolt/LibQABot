package nju.lib.qabot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class Requests {

    static private final StringBuilder sb = new StringBuilder();    // tool object

    private Requests () { } // no INSTANCE

    static public JSONObject get(String url) {
        return get(url, Collections.emptyMap());
    }

    static public JSONObject get(String url, Map<String, String> params) {
        JSONObject ret = null;
        try {
            // reform url
            if (!params.isEmpty()) {
                sb_reset(url);
                sb.append('?');
                for (Map.Entry<String, String> kv: params.entrySet()) {
                    sb.append(kv.getKey());
                    sb.append('=');
                    sb.append(kv.getValue());
                    sb.append('&');
                }
                sb.deleteCharAt(sb.length());  // remove redundant '&'
                url = sb.toString();
            }

            // write header
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            // read response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                sb_reset();
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line);
                ret = JSONObject.parseObject(sb.toString());
            }

            // close
            conn.disconnect();
        } catch (IOException e) { e.printStackTrace(); }
        return ret;
    }

    static public JSONObject post(String url, Map<String, String> params) {
        JSONObject ret = null;
        try {
            // write header
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            // write body
            try (PrintWriter writer = new PrintWriter(conn.getOutputStream())) {
                writer.write(JSONObject.toJSONString(params));
                writer.flush();
            }

            // read response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                sb_reset();
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line);
                ret = JSONObject.parseObject(sb.toString());
            }

            // close
            conn.disconnect();
        } catch (IOException e) { e.printStackTrace(); }
        return ret;
    }

    static private void sb_reset() { sb.setLength(0); }
    static private void sb_reset(String initValue) { sb_reset(); sb.append(initValue); }

}
