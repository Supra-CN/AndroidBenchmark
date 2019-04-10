package vendetta.androidbenchmark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.brotli.dec.BrotliInputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    InternalTest mLocalTest;
    InternalTest mRemoteTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocalTest = new InternalTest("local test", R.id.monitor_local, R.id.progress_local, R.id.btn_local_run, new LocalTest());
        mRemoteTest = new InternalTest("remote test", R.id.monitor_remote, R.id.progress_remote, R.id.btn_remote_run, RemoteTest.get());
        findViewById(R.id.btn_both_run).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        Log.i("supra", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_both_run:
                mLocalTest.onClick(v);
                mRemoteTest.onClick(v);
                break;
            case R.id.btn_clear:
                mLocalTest.monitor.setText("");
                mRemoteTest.monitor.setText("");
                break;
            default:
                break;
        }
    }

    private class InternalTest implements View.OnClickListener, TextWatcher {
        final String testLabel;
        final TextView monitor;
        final ProgressBar progress;
        final Button button;
        final Test test;
        boolean isRunning = false;

        static final int progressResolution = 1000;

        InternalTest(String testLabel, int monitor, int progresss, int button, Test test) {
            this.testLabel = testLabel;
            this.monitor = findViewById(monitor);
            this.progress = findViewById(progresss);
            this.button = findViewById(button);
            this.test = test;

            this.progress.setMin(0);
            this.progress.setMax(progresss);
            this.monitor.setMovementMethod(ScrollingMovementMethod.getInstance());
            this.monitor.addTextChangedListener(this);
            this.button.setOnClickListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Selection.setSelection(s, s.length()-1);
        }

        @Override
        public void onClick(View v) {
            if (isRunning) {
                monitor.append(testLabel + " is already running!\n");
                return;
            }
            monitor.append(testLabel + " start!\n");
            isRunning = true;
            test.run(new Test.Callback() {

                @Override
                public void onUpdate(String msg) {
                    isRunning = false;
                    monitor.append(msg + "\n");
                }

                @Override
                public void onProgress(float progress) {
                    InternalTest.this.progress.setProgress(Float.valueOf(progress * progressResolution).intValue(), true);
                }

                @Override
                public void onCallback(String result) {
                    isRunning = false;
                    monitor.append("\n"+result + "\n");
                }
            });
        }
    }

}
