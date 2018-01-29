package com.evernote.android.state.demo

import android.app.Application

import com.evernote.android.state.StateSaver

/**
 * @author rwondratschek
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true)
    }
}
