package com.evernote.android.state.test;

import com.evernote.android.state.State;

public class TestInheritanceGeneric<P> {

    @State
    public int mValue1;

    public P mGenericValue;

    public static class InheritanceLevelGeneric1 <P> extends TestInheritanceGeneric<P> {

    }

    public static class InheritanceLevelGeneric2 extends InheritanceLevelGeneric1<String> {

        @State
        public int mValue2;
    }
}
