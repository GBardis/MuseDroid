package com.example.musedroid.musedroid;

/**
 * Created by gdev on 19/10/2017.
 */


import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}