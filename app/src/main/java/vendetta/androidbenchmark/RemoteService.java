package vendetta.androidbenchmark;

import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RemoteService extends Service implements Test.Callback {

    public static final String LOG_TAG = RemoteService.class.getSimpleName();

    Messenger mService;
    ServiceConnection mConnection;
    final Deque<Message> mPendingMsg = new ArrayDeque<>();

    boolean mTesting = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mTesting) {
            onUpdate("remote: running already");
        } else {
            test();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCallback(String result) {
        Message msg = Message.obtain();
        msg.what = RemoteTest.MSG_RESULT;
        msg.getData().putString(RemoteTest.MSG_DATA_RESULT, result);
        sendMsg(msg);
        mTesting = false;
    }

    @Override
    public void onUpdate(String data) {
        Message msg = Message.obtain();
        msg.what = RemoteTest.MSG_UPDATE;
        msg.getData().putString(RemoteTest.MSG_DATA_UPDATE, data);
        sendMsg(msg);
    }

    @Override
    public void onProgress(float progress) {
        Message msg = Message.obtain();
        msg.what = RemoteTest.MSG_PROGRESS;
        msg.getData().putFloat(RemoteTest.MSG_DATA_PROGRESS, progress);
        sendMsg(msg);
    }

    private void sendMsg(Message msg) {

        if (null != mService) {
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }

        mPendingMsg.offer(msg);

        if (null == mConnection) {
            Intent intent = new Intent(App.self, LocalService.class);
            mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mService = new Messenger(service);
                    while (!mPendingMsg.isEmpty()) {
                        try {
                            mService.send(mPendingMsg.poll());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mConnection = null;
                    mService = null;
                }
            };
            bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
        }

    }

    private void test() {
        mTesting = true;
        LocalTest test = new LocalTest();
        test.run(this);
    }
}
