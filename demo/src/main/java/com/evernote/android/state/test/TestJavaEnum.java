package com.evernote.android.state.test;

import com.evernote.android.state.State;

/**
 * @author rwondratschek
 */
public class TestJavaEnum {

    public enum JavaEnum {
        LEFT, RIGHT
    }

    @State
    JavaEnum mJavaEnum = JavaEnum.LEFT;
}
