package benchmark.cpubenchmark;

import android.util.Log;

import benchmark.Benchmarks;
import benchmark.IBenchmark;
import database.Score;
import vendetta.androidbenchmark.Test;

/**
 * Created by Vendetta on 24-May-17.
 */

public class CPUBenchmark implements IBenchmark {
    private FloatingPointMathCPUBenchmark floatBench = new FloatingPointMathCPUBenchmark();
    private IntegerMathCPUBenchmark intBench = new IntegerMathCPUBenchmark();
    private PiDigitsCPUBenchmark piBench = new PiDigitsCPUBenchmark();


    Test.Callback mCallback;

    @Override
    public void setCallback(Test.Callback callback) {
        mCallback = callback;
    }

    @Override
    public void initialize() {
        floatBench.setCallback(mCallback);
        floatBench.initialize();
        intBench.setCallback(mCallback);
        intBench.initialize();
        piBench.setCallback(mCallback);
        piBench.initialize();

    }

    @Override
    public void initialize(Long size) {

    }

    @Override
    public void warmup() {

    }

    @Override
    public void run(Object... param) {

    }

    @Override
    public void run() {
        Log.d("CPUBench","Starting IntegerBenchmark");
        intBench.run();
        Log.d("CPUBench","Starting FloatBenchmark");
        floatBench.run();
        Log.d("CPUBench","Starting PIBenchmark");
        piBench.run();
        Log.d("CPUBench","All benchmarks finished");
    }

    @Override
    public void stop() {
        intBench.stop();
        floatBench.stop();
        piBench.stop();
    }

    @Override
    public void clean() {

    }

    @Override
    public String getInfo() {
        return intBench.getInfo()+"\n"+floatBench.getInfo()+"\n"+piBench.getInfo();
    }

    @Override
    public Score getScore() {
        Score intScore = intBench.getScore();
        Score floatScore = floatBench.getScore();
        Score piScore = piBench.getScore();
        long result = (long)(100000000.0/Math.pow((double)Long.parseLong(intScore.getResult())*Long.parseLong(floatScore.getResult())*Long.parseLong(piScore.getResult()),1.0/3));
        return new Score(Benchmarks.CPUBenchmark.toString(),
               Long.toString(result),
                intScore.getExtra()+" "+floatScore.getExtra()+" "+piScore.getExtra());
    }

    @Override
    public void compute() {

    }

}
