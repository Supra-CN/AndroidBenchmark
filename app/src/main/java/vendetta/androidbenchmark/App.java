package vendetta.androidbenchmark;

import android.app.Application;

public class App extends Application {

    public static App self;

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
    }
}
