package vendetta.androidbenchmark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    InternalTest mLocalTest;
    InternalTest mRemoteTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InternalTest mLocalTest = createLocalTest();
        InternalTest mRemoteTest = createRemoteTest();
    }

    InternalTest createLocalTest() {
        return new InternalTest("local test", R.id.monitor_local, R.id.progress_local, R.id.btn_local_run) {

            Test test;

            @Override
            public void onClick(View v) {
                if (null != test) {
                    monitor.append("test is running!");
                    return;
                }
                super.onClick(v);
                test = new Test();
                test.run();
            }
        };
    }

    InternalTest createRemoteTest() {
        return new InternalTest("remote test", R.id.monitor_remote, R.id.progress_remote, R.id.btn_remote_run) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

            }
        };
    }


    private abstract class InternalTest implements View.OnClickListener, TextWatcher {
        final String testLabel;
        final TextView monitor;
        final ProgressBar progress;
        final Button button;

        InternalTest(String testLabel, int monitor, int progresss, int button) {
            this.testLabel = testLabel;
            this.monitor = findViewById(monitor);
            this.progress = findViewById(progresss);
            this.button = findViewById(button);

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
            Selection.setSelection(s, s.length());
        }

        @Override
        public void onClick(View v) {
            this.monitor.append(testLabel + " start!\n");
        }
    }

}
