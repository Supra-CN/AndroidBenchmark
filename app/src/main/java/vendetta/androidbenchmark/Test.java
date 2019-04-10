package vendetta.androidbenchmark;

public interface Test {
    void run(final Callback callback);

    interface Callback {
        void onCallback(String result);
        void onUpdate(String data);

        void onProgress(float progress);
    }
}
