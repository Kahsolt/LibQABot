package nju.lib.qabot;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

import java.util.LinkedList;

public class RecordWorker {

    // PCM params
    private final static int AUDIO_INPUT_DEVICE = AudioSource.MIC;
    private final static int AUDIO_SAMPLE_RATE = 16000;                     // 44100, 22050, 16000, 11025
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    // record device & status
    private boolean status = false;    // false for idle, true for recording
    private int bufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
    private AudioRecord recorder = new AudioRecord(AUDIO_INPUT_DEVICE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSize);
    private LinkedList<byte[]> frameBuffer = new LinkedList<>();
    private int dataLength = 0;    // total length of data in `frameBuffer`

    // singleton
    static private RecordWorker INSTANCE;
    static { INSTANCE = new RecordWorker(); }
    private RecordWorker() { }
    static public RecordWorker getInstance() { return INSTANCE; }

    public void start() {
        synchronized (RecordWorker.class) {
            frameBuffer.clear();   // reset buffer
            dataLength = 0;
            status = true;         // start recording
            recorder.startRecording();
            new Thread(() -> {
                byte[] data = new byte[bufferSize];     // one frame per time
                while (status) {
                    int sz = recorder.read(data, 0, bufferSize);
                    if (sz > 0) {
                        frameBuffer.add(data.clone());   // skip a frame on errors
                        dataLength += sz;
                    }
                }
            }).start();
        }
    }

    public void stop() {
        synchronized (RecordWorker.class) {
            recorder.stop();
            status = false;
        }
    }

    public void release() {
        recorder.release();
        recorder = null;
    }

    public byte[] getData() {
        byte[] data = new byte[dataLength];
        int pos = 0;
        for (byte[] frame : frameBuffer) {
            System.arraycopy(frame, 0, data, pos, frame.length);
            pos += frame.length;
        }
        return data;
    }

}