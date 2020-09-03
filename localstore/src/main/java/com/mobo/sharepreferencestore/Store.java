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

    public Store(Context context, String name) {
        if (context == null) {
            throw new NullPointerException("get share preference null execption!");
        }
        mStoreFile = new StoreFile(getShareName(context, name));
        mMap = mStoreFile.loadFile();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null && msg.what == what) {
                    new Thread() {
                        @Override
                        public void run() {
                            commit();
                        }
                    }.start();
                }
            }
        };
    }

    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mMap.remove(key);
        delayCommit();
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
        if (TextUtils.isEmpty(key) || value == null) {
            return;
        }
        mMap.put(key, value);
        delayCommit();
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
        if (mHandler.hasMessages(what)) {
            return;
        }
        mHandler.sendEmptyMessageDelayed(what, mDelay);
    }

    private String getShareName(Context context, String name) {
        return context.getFilesDir().getAbsolutePath() + File.separator + "shared_prefs_" + name;
    }

    private void commit() {
        mStoreFile.write(mMap);
    }

    private static final long mDelay = 100;
    private static final int what = 101011;
}
