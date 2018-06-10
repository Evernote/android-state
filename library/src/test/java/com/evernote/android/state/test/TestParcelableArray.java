package com.evernote.android.state.test;

import com.evernote.android.state.State;
import com.evernote.android.state.StateReflection;

/**
 * @author rwondratschek
 */
public class TestParcelableArray {
    @State
    public TestTypes.ParcelableImpl[] mParcelableArrayImpl1 = new TestTypes.ParcelableImpl[]{new TestTypes.ParcelableImpl(0)};

    @State
    private TestTypes.ParcelableImpl[] mParcelableArrayImpl2 = new TestTypes.ParcelableImpl[]{new TestTypes.ParcelableImpl(0)};

    @StateReflection
    private TestTypes.ParcelableImpl[] mParcelableArrayImpl3 = new TestTypes.ParcelableImpl[]{new TestTypes.ParcelableImpl(0)};

    public TestTypes.ParcelableImpl[] getParcelableArrayImpl2() {
        return mParcelableArrayImpl2;
    }

    public void setParcelableArrayImpl2(TestTypes.ParcelableImpl[] parcelableArrayImpl2) {
        mParcelableArrayImpl2 = parcelableArrayImpl2;
    }

    public void setToValue(int value) {
        mParcelableArrayImpl1 = new TestTypes.ParcelableImpl[]{new TestTypes.ParcelableImpl(value)};
        mParcelableArrayImpl2 = new TestTypes.ParcelableImpl[]{new TestTypes.ParcelableImpl(value)};
        mParcelableArrayImpl3 = new TestTypes.ParcelableImpl[]{new TestTypes.ParcelableImpl(value)};
    }

    public boolean isValue(int value) {
        return mParcelableArrayImpl1[0].isValue(value) && mParcelableArrayImpl2[0].isValue(value) && mParcelableArrayImpl3[0].isValue(value);
    }
}
