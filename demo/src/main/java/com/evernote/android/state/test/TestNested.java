package com.evernote.android.state.test;

import com.evernote.android.state.State;

public class TestNested {
    @State
    public int test;

    public static class Inner1 {
        @State
        public int test;

        public static class InnerInner1 {
            @State
            public int test;
        }
        public static class InnerInner2 {
            @State
            public int test;
        }
    }
    public static class Inner2 {
        @State
        public int test;

        public static class InnerInner1 {
            @State
            public int test;
        }
        public static class InnerInner2 {
            @State
            public int test;
        }
    }
}
