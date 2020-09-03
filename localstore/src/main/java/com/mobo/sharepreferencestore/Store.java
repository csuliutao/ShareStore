package com.mobo.sharepreferencestore;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

public class Store {
    private HashMap<String, Object> mMap;
    private StoreFile mStoreFile;
    private Handler mHandler;
    private CommitThread mThread;

    public Store(Context context, String name) {
        mStoreFile = new StoreFile(getShareName(context, name));
        mMap = mStoreFile.loadFile();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof CommitThread) {
                    ((CommitThread) msg.obj).start();
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

    private String getShareName(Context context, String name) {
        return context.getFilesDir().getAbsolutePath() + File.separator + "shared_prefs_" + name;
    }

    private void commit() {
        mStoreFile.write(mMap);
    }

    public String getString(String key, String def) {
        return (String) getValue(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return (Boolean) getValue(key, def);
    }

    public int getInt(String key, int def) {
        Object result = getValue(key, def);
        return ((Number) result).intValue();
    }

    public long getLong(String key, long def) {
        Object result = getValue(key, def);
        return ((Number) result).longValue();
    }

    public void put(String key, Object value) {
        delayCommit(key, value);
    }

    private class CommitThread extends Thread {
        private Message mMsg;
        private long mDelay = 100;

        public void refreshMsg() {
            mMsg = Message.obtain(mHandler, 101011);
            mMsg.obj = this;
        }

        @Override
        public void run() {
            commit();
        }
    }
}
