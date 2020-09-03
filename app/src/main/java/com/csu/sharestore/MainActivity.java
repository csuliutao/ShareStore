package com.csu.sharestore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.mobo.sharepreferencestore.Store;

import csu.liutao.spstore.R;

public class MainActivity extends AppCompatActivity {
    String key = "maaa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Store store = new Store(this, "main");
        Log.e("main_store", "int =" + store.getInt(key +1, 1));
        Log.e("main_store", "long =" + store.getLong(key +2, 1L));
        Log.e("main_store", "string =" + store.getString(key + 5, "null"));
        Log.e("main_store", "boolean =" + store.getBoolean(key + 6, false));

        store.put(key + 1, 20);
        store.put(key + 2, 200L);
        store.put(key + 5, "sdfss");
        store.put(key + 6, true);

        Log.e("main_store", "int =" + store.getInt(key +1, 1));
        Log.e("main_store", "long =" + store.getLong(key +2, 1L));
        Log.e("main_store", "string =" + store.getString(key + 5, "null"));
        Log.e("main_store", "boolean =" + store.getBoolean(key + 6, false));

        store.remove(key + 2);
        store.remove(key + 3);
    }
}