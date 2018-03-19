package com.evernote.android.state.test;

import com.evernote.android.state.State;

/**
 * @author rwondratschek
 */
public class TestInnerClassExtension extends TestInnerClass {
    public class Inner extends TestInnerClass.Inner {
        @State int fields;
    }
}
