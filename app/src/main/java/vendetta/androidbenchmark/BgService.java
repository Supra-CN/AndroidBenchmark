package vendetta.androidbenchmark;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;

import database.Score;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BgService extends Service {

    public static final String LOG_TAG = BgService.class.getSimpleName();

    public static final int MSG_TEST = 1;

    Messenger mReplyTo;

    private static class MsgHandler extends Handler {

        final WeakReference<BgService> mRefHost;

        private MsgHandler(BgService service) {
            mRefHost = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BgService host = mRefHost.get();
            if (null == host) {
                return;
            }
            if (null != msg.replyTo) {
                host.mReplyTo = msg.replyTo;
            }

            switch (msg.what) {
                case MSG_TEST:
                    host.test(msg.replyTo);
                    break;
                default:
                    break;
            }
        }
    }

    final Messenger mMessenger = new Messenger(new MsgHandler(this));

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "onBind");
        return mMessenger.getBinder();
    }


    private void test(final Messenger messenger) {
        Log.i(LOG_TAG, "test messenger="+messenger);
        LocalTest test = new LocalTest();
        test.run(new Test.Callback() {
            @Override
            public void onUpdate(String data) {

                Message msg = Message.obtain();
                msg.what = RemoteTest.MSG_UPDATE;
                msg.getData().putString(RemoteTest.MSG_DATA_UPDATE, data);
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgress(float progress) {
                Message msg = Message.obtain();
                msg.what = RemoteTest.MSG_PROGRESS;
                msg.getData().putFloat(RemoteTest.MSG_DATA_PROGRESS, progress);
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCallback(String result) {
                Message msg = Message.obtain();
                msg.what = RemoteTest.MSG_RESULT;
                msg.getData().putString(RemoteTest.MSG_DATA_RESULT, result);
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
