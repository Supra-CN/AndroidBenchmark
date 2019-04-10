package vendetta.androidbenchmark;

import android.content.Intent;
import android.os.Message;

public class RemoteTest implements Test {
    public static final int MSG_RESULT = 1;
    public static final int MSG_PROGRESS = 2;
    public static final int MSG_UPDATE = 3;
    public static final String MSG_DATA_RESULT = "result";
    public static final String MSG_DATA_PROGRESS = "progress";
    public static final String MSG_DATA_UPDATE = "update";

    private static RemoteTest sInstant;

    Callback mCallback;

    @Override
    public void run(Callback callback) {
        mCallback = callback;
        mCallback.onProgress(0);
        Intent intent = new Intent(App.self, RemoteService.class);
        App.self.startService(intent);

    }

    static public RemoteTest get() {
        if (null == sInstant) {
            sInstant = new RemoteTest();
        }
        return sInstant;
    }

    void onServiceCallback(Message msg) {
        switch (msg.what) {
            case MSG_RESULT:
                if (null != mCallback) {
                    mCallback.onProgress(1);
                    mCallback.onCallback(msg.getData().getString(MSG_DATA_RESULT, ""));
                    mCallback = null;
                }
                break;
            case MSG_PROGRESS:
                if (null != mCallback) {
                    mCallback.onProgress(msg.getData().getFloat(MSG_DATA_PROGRESS, 0));
                }
                break;
            case MSG_UPDATE:
                if (null != mCallback) {
                    mCallback.onUpdate(msg.getData().getString(MSG_DATA_UPDATE, ""));
                }
                break;
            default:
                break;
        }
    }
}
