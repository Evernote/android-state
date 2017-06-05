package com.evernote.android.test.data;

import android.support.annotation.Keep;

import com.evernote.android.state.State;
import com.evernote.android.state.StateReflection;

/**
 * @author rwondratschek
 */
public class TestProguard {
    // different package to avoid clash with Proguard rules

    @State
    public int test1;

    @State
    private int test2;

    @StateReflection
    private int test3;

    public int getTest2() {
        return test2;
    }

    public void setTest2(int test2) {
        this.test2 = test2;
    }

    @Keep
    public void setValue(int value) {
        test1 = value;
        setTest2(value);
        test3 = value;
    }

    @Keep
    public void verifyValue(int value) {
        if (test1 != value) {
            throw new IllegalStateException();
        }
        if (getTest2() != value) {
            throw new IllegalStateException();
        }
        if (test3 != value) {
            throw new IllegalStateException();
        }
    }
}
