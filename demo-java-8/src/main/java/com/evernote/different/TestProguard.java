package com.evernote.different;
// different package to avoid clash with Proguard rules

import com.evernote.android.state.State;
import com.evernote.android.state.StateReflection;

/**
 * @author rwondratschek
 */
public class TestProguard {

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

    public void setValue(int value) {
        test1 = value;
        setTest2(value);
        test3 = value;
    }

    public void verifyValue(int value) {
        if (test1 != value) {
            throw new IllegalStateException("test1 different, expected " + value + ", is " + test1);
        }
        if (getTest2() != value) {
            throw new IllegalStateException("getTest2() different, expected " + value + ", is " + getTest2());
        }
        if (test3 != value) {
            throw new IllegalStateException("test3 different, expected " + value + ", is " + test3);
        }
    }
}
