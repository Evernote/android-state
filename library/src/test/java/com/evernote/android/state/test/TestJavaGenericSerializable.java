package com.evernote.android.state.test;

import com.evernote.android.state.State;

/**
 * @author rwondratschek
 */
public class TestJavaGenericSerializable {

    @State
    private GenericSerializable<String> mGenericSerializable;

    public GenericSerializable<String> getGenericSerializable() {
        return mGenericSerializable;
    }

    public void setGenericSerializable(GenericSerializable<String> genericSerializable) {
        mGenericSerializable = genericSerializable;
    }
}
