package com.evernote.android.state.test;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.evernote.android.state.State;

import java.io.Serializable;
import java.util.ArrayList;

public class TestTypes {
    @State
    public boolean mBoolean;
    @State
    public boolean[] mBooleanArray;
    @State
    public Boolean mBooleanObj;
    @State
    public byte mByte;
    @State
    public byte[] mByteArray;
    @State
    public Byte mByteObj;
    @State
    public char mChar;
    @State
    public char[] mCharArray;
    @State
    public Character mCharObj;
    @State
    public double mDouble;
    @State
    public double[] mDoubleArray;
    @State
    public Double mDoubleObj;
    @State
    public float mFloat;
    @State
    public float[] mFloatArray;
    @State
    public Float mFloatObj;
    @State
    public int mInt;
    @State
    public int[] mIntArray;
    @State
    public Integer mIntegerObj;
    @State
    public long mLong;
    @State
    public long[] mLongArray;
    @State
    public Long mLongObj;
    @State
    public short mShort;
    @State
    public short[] mShortArray;
    @State
    public Short mShortObj;
    @State
    public CharSequence mCharSequence;
    @State
    public CharSequence[] mCharSequenceArray;
    @State
    public String mString;
    @State
    public String[] mStringArray;
    @State
    public ArrayList<CharSequence> mCharSequenceArrayList;
    @State
    public ArrayList<Integer> mIntegerArrayList;
    @State
    public ArrayList<String> mStringArrayList;
    @State
    public Bundle mBundle;
    @State
    public Parcelable[] mParcelableArray;
    @State
    public ParcelableImpl mParcelableImpl;
    @State
    public SerializableImpl mSerializableImpl;
    @State
    public ArrayList<ParcelableImpl> mParcelableArrayList;
    @State
    public SparseArray<ParcelableImpl> mParcelableSparseArray;

    public static class ParcelableImpl implements Parcelable {
        private int mInt;

        public ParcelableImpl(int anInt) {
            mInt = anInt;
        }

        protected ParcelableImpl(Parcel in) {
            mInt = in.readInt();
        }

        public boolean isValue(int value) {
            return mInt == value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ParcelableImpl that = (ParcelableImpl) o;

            return mInt == that.mInt;
        }

        @Override
        public int hashCode() {
            return mInt;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mInt);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ParcelableImpl> CREATOR = new Creator<ParcelableImpl>() {
            @Override
            public ParcelableImpl createFromParcel(Parcel in) {
                return new ParcelableImpl(in);
            }

            @Override
            public ParcelableImpl[] newArray(int size) {
                return new ParcelableImpl[size];
            }
        };
    }

    public static class SerializableImpl implements Serializable {
    }
}
