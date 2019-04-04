package vendetta.androidbenchmark;

import android.os.AsyncTask;

import benchmark.IBenchmark;
import benchmark.benchmarksuite.BenchmarkSuite;
import database.Score;

public class Test {
    public void run(final Callback callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                IBenchmark benchmark = new BenchmarkSuite();
                benchmark.run();
                callback.onCallback(benchmark.getScore());
            }
        });
    }

    interface Callback {
        void onCallback(Score score);
    }
}
