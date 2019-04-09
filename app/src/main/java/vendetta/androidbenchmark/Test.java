package vendetta.androidbenchmark;

public interface Test {
    void run(final Callback callback);

    interface Callback {
        void onCallback(String result);
        void onUpdate(String msg);

        void onProgress(float progress);
    }
}
