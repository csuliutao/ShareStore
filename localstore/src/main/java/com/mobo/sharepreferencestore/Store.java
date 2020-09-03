package com.mobo.sharepreferencestore;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class Store {
    private HashMap<String, Object> mMap;
    private StoreFile mStoreFile;
    private Handler mHandler;
    private static HandlerThread sThread = null;
    private CountDownLatch mLatch;

    public Store(Context context, String name) {
        if (context == null) {
            throw new NullPointerException("get share preference null execption!");
        }
        if (sThread == null) {
            sThread = new StoreHandler.StoreThread();
            sThread.start();
        }
        mLatch = new CountDownLatch(1);
        mStoreFile = new StoreFile(getShareName(context, name));
        mHandler = new StoreHandler(this, sThread.getLooper());
        mHandler.sendEmptyMessage(StoreHandler.LOAD_MSG);
    }

    void loadMap() {
        mMap = mStoreFile.loadFile();
        mLatch.countDown();
    }

    public void remove(String key) {
        waitLoad();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mMap.remove(key);
        delayCommit();
    }

    public String getString(String key, String def) {
        waitLoad();
        return (String) getValue(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        waitLoad();
        return (Boolean) getValue(key, def);
    }

    public int getInt(String key, int def) {
        waitLoad();
        Object result = getValue(key, def);
        return ((Number) result).intValue();
    }

    public long getLong(String key, long def) {
        waitLoad();
        Object result = getValue(key, def);
        return ((Number) result).longValue();
    }

    public void put(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return;
        }
        waitLoad();
        mMap.put(key, value);
        delayCommit();
    }

    public static void finish() {
        if (sThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                sThread.quitSafely();
            } else {
                sThread.quit();
            }
        }
    }

    private void waitLoad() {
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private void delayCommit() {
        if (mHandler.hasMessages(StoreHandler.WRITE_MSG)) {
            return;
        }
        mHandler.sendEmptyMessageDelayed(StoreHandler.WRITE_MSG, StoreHandler.DELAY);
    }

    private String getShareName(Context context, String name) {
        return context.getFilesDir().getAbsolutePath() + File.separator + "shared_prefs_" + name;
    }

    void commit() {
        mStoreFile.write(mMap);
    }
}
