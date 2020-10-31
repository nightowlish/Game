package com.sneaky.gamey;

import android.app.Application;
import android.content.Context;

import com.sneaky.gamey.database.Firebase;

public class App extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        Firebase.initFirebase();
    }
    public static Context getContext() {
        return App.context;
    }

}
