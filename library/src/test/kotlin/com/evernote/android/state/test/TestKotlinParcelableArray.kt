package com.evernote.android.state.test

import com.evernote.android.state.State
import com.evernote.android.state.StateReflection

/**
 * @author rwondratschek
 */
class TestKotlinParcelableArray {
    @State
    var parcelableArrayImpl1: Array<TestTypes.ParcelableImpl> = arrayOf(TestTypes.ParcelableImpl(0))

    @StateReflection
    private var mParcelableArrayImpl2: Array<TestTypes.ParcelableImpl> = arrayOf(TestTypes.ParcelableImpl(0))

    fun setToValue(value: Int) {
        parcelableArrayImpl1 = arrayOf(TestTypes.ParcelableImpl(value))
        mParcelableArrayImpl2 = arrayOf(TestTypes.ParcelableImpl(value))
    }

    fun isValue(value: Int): Boolean {
        return parcelableArrayImpl1[0].isValue(value) && mParcelableArrayImpl2[0].isValue(value)
    }
}