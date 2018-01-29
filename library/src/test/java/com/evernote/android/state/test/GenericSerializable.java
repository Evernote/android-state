package com.evernote.android.state.test;

import java.io.Serializable;

/**
 * @author rwondratschek
 */
public class GenericSerializable<T> implements Serializable {

    private T mValue;

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }
}
