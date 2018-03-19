package com.evernote.android.state.test;

import com.evernote.android.state.State;
import com.evernote.android.state.StateReflection;

/**
 * @author rwondratschek
 */
public class TestJavaEnum {

    public enum JavaEnum {
        LEFT, RIGHT
    }

    @State
    JavaEnum mJavaEnum = JavaEnum.LEFT;

    @StateReflection
    private JavaEnum mJavaEnum1 = JavaEnum.LEFT;

    public JavaEnum getJavaEnum1() {
        return mJavaEnum1;
    }

    public void setJavaEnum1(JavaEnum javaEnum1) {
        mJavaEnum1 = javaEnum1;
    }
}
