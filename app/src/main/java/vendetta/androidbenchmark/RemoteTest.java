package vendetta.androidbenchmark;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import java.util.ArrayDeque;
import java.util.Deque;

public class RemoteTest implements Test {
    public static final int MSG_RESULT = 1;
    public static final int MSG_PROGRESS = 2;
    public static final String MSG_DATA_RESULT = "result";
    public static final String MSG_DATA_PROGRESS = "progress";

    private static RemoteTest sInstant;

    Messenger mService;
    Messenger mMessenger = new Messenger(new MsgHandler());
    ServiceConnection mConnection;
    final Deque<Message> mPendingMsg = new ArrayDeque<>();
    Callback mCallback;


    @Override
    public void run(Callback callback) {
        mCallback = callback;
        mCallback.onProgress(0);
        Message msg = Message.obtain();
        msg.replyTo = mMessenger;
        msg.what = BgService.MSG_TEST;

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
            Intent intent = new Intent(App.self, BgService.class);
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
            App.self.bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
        }
    }

    static public RemoteTest get() {
        if (null == sInstant) {
            sInstant = new RemoteTest();
        }
        return sInstant;
    }

    private class MsgHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_RESULT:
                    if (null != mCallback) {
                        mCallback.onProgress(1);
                        mCallback.onCallback(msg.getData().getString(MSG_DATA_RESULT, ""));
                        mCallback = null;
                    }
                    App.self.unbindService(mConnection);
                    mConnection = null;
                    mService = null;
                    break;
                default:
                    break;
            }
        }
    }



}
