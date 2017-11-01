package com.evernote.android.state.demo;

import android.app.Application;

import com.evernote.android.state.StateSaver;

/**
 * @author rwondratschek
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true);
    }
}
