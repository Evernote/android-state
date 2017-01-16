package com.evernote.android.state.demo;

import android.app.Activity;
import android.os.Bundle;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

/**
 * @author rwondratschek
 */
public class MainActivity extends Activity {

    @State
    private int mTest1;

    @State(reflection = true)
    private int test2;

    @State
    public int test3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

    public int getTest1() {
        return mTest1;
    }

    public void setTest1(int test) {
        mTest1 = test;
    }
}
