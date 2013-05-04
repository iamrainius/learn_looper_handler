package jing.app.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private MyHanderThread mHandlerThread;
    private Handler mHandler;
    private Handler mNativeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "Current thread: " + Thread.currentThread().getId());
            Looper looper;
            switch (msg.what) {
            case 0:
                looper = mHandlerThread.getLooper();
                if (looper != null) {
                    mHandler = new Handler(looper) {
                        public void handleMessage(Message msg) {
                            Log.d(TAG, "Current thread: " + Thread.currentThread().getId());
                        };
                    };
                    
                    mLooperReady = true;
                }
                
                Message m = Message.obtain(mHandler);
                m.sendToTarget();
                
                break;
            }
        }
    
    };
    
    volatile boolean mLooperReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mHandlerThread = new MyHanderThread("MyHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
//        MyThread myThread = new MyThread("loop thread");
//        myThread.start();
//        Handler handler = myThread.getHandler();
//        if (handler != null) {
//            handler.post(new Runnable() {
//                
//                @Override
//                public void run() {
//                    Log.d(TAG, "Posted from UI thread.");
//                }
//            });
//            
//            Message message = Message.obtain(handler);
//            handler.sendMessage(message);
//        }
    }
    
    class MyHanderThread extends HandlerThread {

        public MyHanderThread(String name) {
            super(name);
        }

        public MyHanderThread(String name, int priority) {
            super(name, priority);
        }

        @Override
        protected void onLooperPrepared() {
            Log.d(TAG, "MyHandlerThread: looper prepared");
            Message m = Message.obtain(mNativeHandler);
            m.what = 0;
            m.sendToTarget();
        }
    }

    class MyThread extends Thread {
        private Handler mHandler;

        public MyThread(String threadName) {
            super(threadName);
        }

        public Handler getHandler() {
            return mHandler;
        }

        @Override
        public void run() {
            Looper.prepare();    //Bind the looper to this thread
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    default:
                        Log.d(TAG, "MyThread: handle a message");
                    }
                }
            };
            
            Looper.loop();
        }

    }
}
