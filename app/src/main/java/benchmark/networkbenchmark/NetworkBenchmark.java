package benchmark.networkbenchmark;

import android.os.Environment;
import android.widget.Button;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import benchmark.Benchmarks;
import benchmark.IBenchmark;
import database.Score;
import log.ConsoleLogger;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Source;
import stopwatch.Timer;
import vendetta.androidbenchmark.Test;

/**
 * Created by alex on 5/14/2017.
 */

/**
 * Measures download speed by downloading a large file from http://www.engineerhammad.com.
 */
public class NetworkBenchmark implements IBenchmark {
    private static final String FILE_ADDRESS = "http://downapp.baidu.com/baidusearch/AndroidPhone/11.6.1.10/1/757p/20190401163341/baidusearch_AndroidPhone_11-6-1-10_757p.apk";
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 512;
    private static final int BUFFER_SIZE = 1024 * 1024 * 16; // Buffer size in bytes.

    private double result = 0; // MB/SECOND
    private String extra;
    private ConsoleLogger logger = new ConsoleLogger();
    private volatile boolean shouldTestRun;

    @Override
    public void initialize() {
        this.result = 0;
    }

    /**
     * @param size the total download in bytes..
     */
    @Override
    public void initialize(Long size) {
    }

    Test.Callback mCallback;

    @Override
    public void setCallback(Test.Callback callback) {
        mCallback = callback;
    }

    @Override
    public void warmup() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run(Object... param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
        final Timer timer = new Timer();
        timer.start();
        this.compute();
        if (null != mCallback) {
            mCallback.onUpdate("下载："+timer.stop()+"ms \n >> Downloaded "+size/(1024*1024)+" MB with a speed of "+String.format(java.util.Locale.US,"%.2f",result)+" MB/s");
        }
    }

    @Override
    public void compute() {
        this.shouldTestRun = true;
        logger.write("Benchmark started");
        logger.write("" + BUFFER_SIZE);
        try {


            Request request = new Request.Builder().url(FILE_ADDRESS).cacheControl(CacheControl.FORCE_NETWORK).build();
            Response response = new OkHttpClient.Builder().build().newCall(request).execute();

            if (!response.isSuccessful()) {
                return;
            }

            ResponseBody body = response.body();
            if (null == body) {
                return;
            }

            long size = body.contentLength();

            Source source = body.source();

            File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString());

            Okio.sink(file);





            byte[] buffer = new byte[bufferSize];
            Timer timer = new Timer();
            boolean eof = false;
            int totalDownload = 0;
            while (totalDownload < size && this.shouldTestRun) {
                timer.start();
                try {
                    //logger.write("Downloading....");
                    stream.readFully(buffer);
                } catch (EOFException e) {
                    eof = true;
                }
                long measure = timer.stop();
                if (eof) {
                    break;
                }
                totalDownload += bufferSize;
                //logger.write("Download " + bufferSize + " " + (bufferSize / (1024 * 1024)));
                measurements.add((double)bufferSize / (1024 * 1024) / (measure / 1000000000.0));
            }
            double sum = 0;
            for (double each : measurements) {
                //logger.write("" + String.format(java.util.Locale.US,"%.6f", each));
                sum += each;
            }
            this.result = sum * 4 / measurements.size();
        } catch (IOException e) {
            logger.write(e.toString());
        } finally {
            this.shouldTestRun = false;
        }
    }

    @Override
    public void stop() {
        this.shouldTestRun = false;
    }

    @Override
    public void clean() {

    }

    @Override
    public Score getScore() {
        return new Score(
                Benchmarks.NetworkBenchmark.toString(),
                Long.toString((long)(this.result * 1000)),
                "Downloaded "+size/(1024*1024)+" MB with a speed of "+String.format(java.util.Locale.US,"%.2f",result)+" MB/s");
    }

    @Override
    public String getInfo(){
        return "Network Speed Benchmark:\nMeasures download speed by downloading a 64 MB file from http://www.engineerhammad.com.";
    }
}