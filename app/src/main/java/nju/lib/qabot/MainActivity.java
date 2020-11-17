package nju.lib.qabot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import nju.lib.qabot.mod.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // global state data
    private boolean status = false;
    private String asrResult = "<empty>";
    private String qaResult = "<empty>";

    // recording device
    private final RecordWorker recordWorker = RecordWorker.getInstance();

    // modules
    private ASREngine asrEngine;
    private QAEngine qaEngine;
    private TTSEngine ttsEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initPermission();

        // init Engine
        asrEngine = new ASREngine(this);
        qaEngine = new QAEngine(this);
        ttsEngine = new TTSEngine(this);
    }

    // UI & events register
    private void initView() {
        // setup power button
        final Button bt_power = this.findViewById(R.id.bt_power);
        bt_power.setOnClickListener(v -> {
            if (v.getId() == R.id.bt_power) {
                synchronized (MainActivity.class) {
                    if (!status) {
                        status = true;
                        bt_power.setText(R.string.bt_power_off);
                        statusShow("record started");
                        recordWorker.start();
                    } else {
                        status = false;
                        statusShow("record stopped");
                        bt_power.setText(R.string.bt_power_on);
                        recordWorker.stop();
                        asrResult = asrEngine.recognize(recordWorker.getData());
                        ((TextView) findViewById(R.id.tx_input)).setText(asrResult);
                    }
                }
            }
        });

        // setup query button
        final Button bt_query = this.findViewById(R.id.bt_query);
        bt_query.setOnClickListener(v -> {
            if (v.getId() == R.id.bt_query) {
                qaResult = qaEngine.query(asrResult);
                ((TextView) findViewById(R.id.tx_output)).setText(qaResult);
            }
        });

        // setup speak button
        final Button bt_speak = this.findViewById(R.id.bt_speak);
        bt_speak.setOnClickListener(v -> {
            if (v.getId() == R.id.bt_speak) ttsEngine.synthAndSpeak(qaResult);
        });

        // setup status scroll
        TextView lb_status = findViewById(R.id.lb_status);
        lb_status.setMaxLines(16);
        lb_status.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    // dynamic permission request
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
        };

        ArrayList<String> toApplyList = new ArrayList<>();
        for (String perm : permissions)
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm))
                toApplyList.add(perm);
        if (!toApplyList.isEmpty()) {
            String[] tmpList = new String[toApplyList.size()];
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);   // 123 defined by app, maybe used in call-back `onRequestPermissionsResult`
        }
    }

    // logging utils
    private void statusShow(String message) {
        Log.i(TAG, message);
        ((TextView) this.findViewById(R.id.tx_status)).append(message + '\n');
    }

}