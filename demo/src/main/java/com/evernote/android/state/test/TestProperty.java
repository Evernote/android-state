package com.evernote.android.state.test;

import com.evernote.android.state.State;

public class TestProperty {
    @State
    private int test;

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }
}
