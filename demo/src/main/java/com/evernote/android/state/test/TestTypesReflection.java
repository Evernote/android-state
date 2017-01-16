package com.evernote.android.state.test;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import com.evernote.android.state.State;

import java.util.ArrayList;

public class TestTypesReflection {
    @State(reflection = true)
    private int mInt;
    @State(reflection = true)
    private int[] mIntArray;
    @State(reflection = true)
    private Integer mIntegerObj;
    @State(reflection = true)
    private Bundle mBundle;
    @State(reflection = true)
    private Parcelable[] mParcelableArray;
    @State(reflection = true)
    private ArrayList<? extends TestTypes.ParcelableImpl> mParcelableArrayList;
    @State(reflection = true)
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
