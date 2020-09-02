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
        long start = System.currentTimeMillis();
        Store.init(this);
        /*Store.put(key + 1, 10);
        Store.put(key + 2, 100L);
        Store.put(key + 5, "sss");
        Store.put(key + 6, true);*/

        Log.e("main_store", "int =" + Store.getInt(key +1, 1));
        Log.e("main_store", "long =" + Store.getLong(key +2, 1L));
        Log.e("main_store", "string =" + Store.getString(key + 5, "null"));
        Log.e("main_store", "boolean =" + Store.getBoolean(key + 6, false));
    }
}