package com.mobo.sharepreferencestore;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

class StoreHandler extends Handler {
    static final int LOAD_MSG = 1011;
    static final int WRITE_MSG = 1100;
    static final int DELAY = 100;
    private Store mStore;

    public StoreHandler(Store mStore, Looper looper) {
        super(looper);
        this.mStore = mStore;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg == null) {
            return;
        }
        if (msg.what == LOAD_MSG) {
            mStore.loadMap();
        } else if (msg.what == WRITE_MSG) {
            mStore.commit();
        }
    }

    public static class StoreThread extends HandlerThread {

        public StoreThread() {
            super("StoreHandler", Process.THREAD_PRIORITY_BACKGROUND);
        }
    }
}
