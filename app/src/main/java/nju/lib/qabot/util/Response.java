package nju.lib.qabot.util;

import android.annotation.SuppressLint;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Response {

    public static Response EMPTY = new Response();

    public static class WebFile {
        public String filename = "webfile.tmp";
        public byte[] data;

        public WebFile(byte[] data) { this.data = data; }
        public WebFile(byte[] data, String filename) { this(data); this.filename = filename; }

        public boolean save(String dp) { return save(dp, filename); }
        public boolean save(String dp, String fn) { return save(new File(dp, fn)); }
        public boolean save(File file) {
            try (BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(file))) {
                fout.write(data);
                return true;
            } catch (IOException e) { e.printStackTrace(); }
            return false;
        }

        @NotNull
        @SuppressLint("DefaultLocale")
        @Override
        public String toString() {
            return String.format("<WebFile filename=%s, size=%d", filename, data.length);
        }
    }

    public JSONObject json = null;
    public WebFile file = null;

    private Response() { }
    public static Response asJson(byte[] data) throws Exception {
        Response resp = new Response();
        resp.json = JSONObject.parseObject(new String(data, "UTF-8"));
        return resp;
    }
    public static Response asFile(byte[] data, String filename) {
        Response resp = new Response();
        resp.file = new WebFile(data, filename);
        return resp;
    }

    public boolean hasJson() { return json != null; }
    public boolean hasFile() { return file != null; }

    @NotNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        if (hasJson()) return String.format("<Response json=%s>", json);
        else if (hasFile()) return String.format("<Response file=%s>", file);
        else return "<Response >";
    }
}
