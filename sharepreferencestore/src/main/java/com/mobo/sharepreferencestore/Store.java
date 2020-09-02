package com.mobo.sharepreferencestore;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

public class Store {
    private Store() {}

    private HashMap<String, Object> mMap;
    private StoreFile mStoreFile;
    private Handler mHandler;
    private CommitThread mThread;

    private void prepare(Context context) {
        mStoreFile = new StoreFile(getShareName(context));
        mMap = mStoreFile.loadFile();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof CommitThread) {
                    ((CommitThread) msg.obj).run();
                }
            }
        };
    }

    private Object getValue(String key, Object def) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        Object result = mMap.get(key);
        if (result == null) {
            return def;
        }
        return result;
    }

    private void delayCommit(String key, Object value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mMap.put(key, value);
        if (mThread == null) {
            mThread = new CommitThread();
        } else if (mHandler.hasMessages(mThread.mMsg.what, mThread)) {
            return;
        }
        mThread.refreshMsg();
        mHandler.sendMessageDelayed(mThread.mMsg, mThread.mDelay);
    }

    private String getShareName(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + "shared_prefs_main";
    }

    private void commit() {
        mStoreFile.write(mMap);
    }

    public static void init(Context context) {
        if (context == null) {
            throw new NullPointerException("get share preference null execption!");
        }
        Inner.ONE.manager.prepare(context);
    }

    public static String getString(String key, String def) {
        return (String) Inner.ONE.manager.getValue(key, def);
    }

    public static boolean getBoolean(String key, boolean def) {
        return (Boolean) Inner.ONE.manager.getValue(key, def);
    }

    public static int getInt(String key, int def) {
        Object result = Inner.ONE.manager.getValue(key, def);
        return ((Number) result).intValue();
    }

    public static long getLong(String key, long def) {
        Object result = Inner.ONE.manager.getValue(key, def);
        return ((Number) result).longValue();
    }

    public static void put(String key, Object value) {
        Inner.ONE.manager.delayCommit(key, value);
    }

    private static class CommitThread implements Runnable {
        private Message mMsg;
        private long mDelay = 100;

        public void refreshMsg() {
            mMsg = Message.obtain(Inner.ONE.manager.mHandler, 101011);
            mMsg.obj = this;
        }

        @Override
        public void run() {
            new Thread(){
                @Override
                public void run() {
                    Inner.ONE.manager.commit();
                }
            }.start();
        }
    }

    enum Inner {
        ONE;
        Store manager = new Store();
    }
}
