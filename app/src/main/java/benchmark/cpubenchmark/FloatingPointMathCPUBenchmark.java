package benchmark.cpubenchmark;

import benchmark.IBenchmark;
import database.Score;
import log.myTimeUnit;
import stopwatch.Timer;
import vendetta.androidbenchmark.Test;

/**
 * Created by Vendetta on 16-Mar-17.
 */

public class FloatingPointMathCPUBenchmark implements IBenchmark {
    private Long size = Long.MAX_VALUE;
    private volatile boolean shouldTestRun;
    private long result;

    Test.Callback mCallback;

    @Override
    public void setCallback(Test.Callback callback) {
        mCallback = callback;
    }

    @Override
    public void initialize() {
        this.size = 20000000L;
        this.result = 0;
    }

    @Override
    public void initialize(Long size) {
        this.size = size;
        this.result = 0;
    }

    @Override
    public void warmup() {
        Long prevSize = this.size;
        this.size = 1000L;
        for (int i=1; i<=3; i++){
            compute();
            this.size *= 10;
        }
        this.size = prevSize;
    }

    @Override
    public void run(Object... param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
        final Timer timer = new Timer();
        this.warmup();
        timer.start();
        this.compute();
        this.result = timer.stop();
        if (null != mCallback) {
            mCallback.onUpdate("六千万浮点计算："+result+"ms");
        }
    }

    /**
     * Approximates square roots using the Babylonian Method.
     */
    @Override
    public void compute() {
        this.shouldTestRun = true;
        double sqrtPi = 1.0; // sqrt of PI
        double sqrtE = 1.0;  // sqrt of Euler's number
        double temp;
        for (double i = 0.0; i < this.size && this.shouldTestRun; i++){
            temp = sqrtPi + Math.PI / sqrtPi;
            sqrtPi = temp * 0.5;
            temp = sqrtE + Math.E / sqrtE;
            sqrtE = temp * 0.5;
        }
        this.shouldTestRun = false;
    }

    @Override
    public void stop() {
        this.shouldTestRun = false;
    }

    @Override
    public void clean() {}

    @Override
    public String getInfo(){
        return "FloatingPointBenchmark: Performs various arithmetic operations on Doubles";
    }

    @Override
    public Score getScore() {
        return new Score(
                "FloatingPointBenchmark",
                Long.valueOf(myTimeUnit.convertTime(this.result, myTimeUnit.MilliSecond)).toString(),
                "60 million floating point arithmetic ops in "+myTimeUnit.convertTime(this.result, myTimeUnit.MilliSecond)+" ms,"
        );
    }

}