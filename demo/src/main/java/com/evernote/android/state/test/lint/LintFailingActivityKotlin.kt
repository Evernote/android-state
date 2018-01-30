package com.evernote.android.state.test.lint

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle

import com.evernote.android.state.StateSaver

/**
 * @author rwondratschek
 */
class LintFailingActivityKotlin : Activity() {

    @SuppressLint("NonMatchingStateSaverCalls")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }
}
