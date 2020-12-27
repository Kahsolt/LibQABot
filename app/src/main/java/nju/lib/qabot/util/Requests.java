package nju.lib.qabot.util;

import android.annotation.SuppressLint;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

import nju.lib.qabot.MainActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class Requests {

    static private final MediaType MIME_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    static private final MediaType MIME_TYPE_BINARY = MediaType.parse("application/octet-stream");
    static private final MediaType MIME_TYPE_AUDIO_WAVE = MediaType.parse("audio/wav");
    static private final MediaType MIME_TYPE_AUDIO_MP3 = MediaType.parse("audio/mp3");
    static public final String ENCODE_CHARSET = "utf-8";

    static private final OkHttpClient http = new OkHttpClient();    // singleton tool object

    @SuppressLint("StaticFieldLeak")
    static public MainActivity app;

    private Requests () { }   // no INSTANCE

    // GET
    static public Response get(String url) { return get(url, Collections.emptyMap()); }

    // GET with params
    static public Response get(String url, Map<String, String> params) {
        // build url query parts
        if (!params.isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            sb.append('?');
            for (Map.Entry<String, String> kv: params.entrySet()) {
                try {
                    sb.append(URLEncoder.encode(kv.getKey(), ENCODE_CHARSET));
                    sb.append('=');
                    sb.append(URLEncoder.encode(kv.getValue(), ENCODE_CHARSET));
                    sb.append('&');
                } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
            }
            sb.deleteCharAt(sb.length() - 1);  // remove redundant '&'
            url = sb.toString();
        }

        // remote call
        Request req = new Request.Builder().url(url).get().build();
        return getResponse(req);
    }

    // POST with params (turned to json string)
    static public Response post(String url, Map<String, String> params) {
        // build json string
        String data = JSONObject.toJSONString(params);
        RequestBody body = RequestBody.create(data, MIME_TYPE_JSON);

        // remote call
        Request req = new Request.Builder().url(url).post(body).build();
        return getResponse(req);
    }

    // POST with file / raw binary
    static public Response post(String url, File file) {
        RequestBody body = RequestBody.create(file, MIME_TYPE_BINARY);
        Request req = new Request.Builder().url(url).post(body).build();
        return getResponse(req);
    }
    static public Response post(String url, byte[] content) {
        RequestBody body = RequestBody.create(content, MIME_TYPE_BINARY);
        Request req = new Request.Builder().url(url).post(body).build();
        return getResponse(req);
    }

    @SuppressLint("DefaultLocale")
    static private Response getResponse(Request request) {
        try {
            app.statusShow(String.format("%s %s", request.method(), request.url().toString()));
            okhttp3.Response res = http.newCall(request).execute();
            app.statusShow(String.format("HTTP %d %s", res.code(), res.message()));

            ResponseBody body = res.body();   // it's a stream, thus only read once
            assert body != null;
            byte[] data = body.bytes();
            MediaType type = body.contentType();
            app.statusShow(String.format("MIME_TYPE: %s", type));

            try {
                return Response.asJson(data);
            } catch (Exception ignore){
                String filename = res.header("X-FILENAME", "tmp");
                return Response.asFile(data, filename);
            }
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
            app.statusShow(e.getMessage());
        }
        return Response.EMPTY;
    }
}
