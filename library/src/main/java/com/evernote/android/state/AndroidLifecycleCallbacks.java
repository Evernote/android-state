package com.evernote.android.state;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * @author rwondratschek
 */
/*package*/ class AndroidLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks implements ActivityLifecycleCallbacks {

    static final AndroidLifecycleCallbacks INSTANCE = new AndroidLifecycleCallbacks();

    boolean mEnabled; // the extra flag is necessary in case this feature gets disabled, then we still can receive some callbacks for fragments

    private AndroidLifecycleCallbacks() {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (mEnabled) {
            StateSaver.restoreInstanceState(activity, savedInstanceState);
        }

        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(this, true);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if (mEnabled) {
            StateSaver.saveInstanceState(activity, outState);
        }
    }

    @Override
    public void onFragmentPreCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        if (mEnabled) {
            StateSaver.restoreInstanceState(f, savedInstanceState);
        }
    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        if (mEnabled) {
            StateSaver.saveInstanceState(f, outState);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
