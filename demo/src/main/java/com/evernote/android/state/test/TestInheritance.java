package com.evernote.android.state.test;

import com.evernote.android.state.State;

public class TestInheritance {

    @State
    public int mValue1;

    public static class InheritanceLevel1 extends TestInheritance {

    }

    public static class InheritanceLevel2 extends InheritanceLevel1 {

        @State
        public int mValue2;
    }
}
