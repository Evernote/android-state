package com.evernote.android.state.test.lint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.evernote.android.state.StateSaver;

/**
 * @author rwondratschek
 */
public class LintFailingActivityJava extends Activity {

    @SuppressLint("NonMatchingStateSaverCalls")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
    }
}
