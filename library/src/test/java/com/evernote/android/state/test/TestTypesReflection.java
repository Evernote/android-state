package com.evernote.android.state.test;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import com.evernote.android.state.StateReflection;

import java.util.ArrayList;

public class TestTypesReflection {
    @StateReflection
    private int mInt;
    @StateReflection
    private int[] mIntArray;
    @StateReflection
    private Integer mIntegerObj;
    @StateReflection
    private Bundle mBundle;
    @StateReflection
    private Parcelable[] mParcelableArray;
    @StateReflection
    private ArrayList<? extends TestTypes.ParcelableImpl> mParcelableArrayList;
    @StateReflection
    private SparseArray<TestTypes.ParcelableImpl> mParcelableSparseArray;

    public int getInt() {
        return mInt;
    }

    public void setInt(int anInt) {
        mInt = anInt;
    }

    public Integer getIntegerObj() {
        return mIntegerObj;
    }

    public void setIntegerObj(Integer integerObj) {
        mIntegerObj = integerObj;
    }

    public ArrayList<? extends TestTypes.ParcelableImpl> getParcelableArrayList() {
        return mParcelableArrayList;
    }

    public void setParcelableArrayList(ArrayList<? extends TestTypes.ParcelableImpl> parcelableArrayList) {
        mParcelableArrayList = parcelableArrayList;
    }
}
