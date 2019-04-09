package vendetta.androidbenchmark;

import android.os.AsyncTask;

import benchmark.IBenchmark;
import benchmark.benchmarksuite.BenchmarkSuite;

public class LocalTest implements Test {
    @Override
    public void run(final Callback callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                callback.onProgress(0);
                IBenchmark benchmark = new BenchmarkSuite();
                benchmark.setCallback(callback);
                benchmark.initialize();
                benchmark.run();
                callback.onProgress(1);
                callback.onCallback(benchmark.getScore().toString());
            }
        });
    }
}
