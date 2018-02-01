package com.softworks.prom.gaidar.lapitchat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by User on 01.02.2018.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
