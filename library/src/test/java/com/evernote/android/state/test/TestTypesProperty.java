package com.evernote.android.state.test;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.evernote.android.state.State;

import java.util.ArrayList;

public class TestTypesProperty {
    @State
    private boolean mBoolean;
    @State
    private boolean[] mBooleanArray;
    @State
    private Boolean mBooleanObj;
    @State
    private byte mByte;
    @State
    private byte[] mByteArray;
    @State
    private Byte mByteObj;
    @State
    private char mChar;
    @State
    private char[] mCharArray;
    @State
    private Character mCharObj;
    @State
    private double mDouble;
    @State
    private double[] mDoubleArray;
    @State
    private Double mDoubleObj;
    @State
    private float mFloat;
    @State
    private float[] mFloatArray;
    @State
    private Float mFloatObj;
    @State
    private int mInt;
    @State
    private int[] mIntArray;
    @State
    private Integer mIntegerObj;
    @State
    private long mLong;
    @State
    private long[] mLongArray;
    @State
    private Long mLongObj;
    @State
    private short mShort;
    @State
    private short[] mShortArray;
    @State
    private Short mShortObj;
    @State
    private CharSequence mCharSequence;
    @State
    private CharSequence[] mCharSequenceArray;
    @State
    private String mString;
    @State
    private String[] mStringArray;
    @State
    private ArrayList<CharSequence> mCharSequenceArrayList;
    @State
    private ArrayList<Integer> mIntegerArrayList;
    @State
    private ArrayList<String> mStringArrayList;
    @State
    private Bundle mBundle;
    @State
    private Parcelable[] mParcelableArray;
    @State
    private TestTypes.ParcelableImpl mParcelableImpl;
    @State
    private ParcelableImplExtension mParcelableImplExtension;
    @State
    private TestTypes.SerializableImpl mSerializableImpl;
    @State
    private ArrayList<TestTypes.ParcelableImpl> mParcelableArrayList;
    @State
    private SparseArray<ParcelableImplExtension> mParcelableSparseArray;

    public boolean isBoolean() {
        return mBoolean;
    }

    public void setBoolean(boolean aBoolean) {
        mBoolean = aBoolean;
    }

    public boolean[] getBooleanArray() {
        return mBooleanArray;
    }

    public void setBooleanArray(boolean[] booleanArray) {
        mBooleanArray = booleanArray;
    }

    public Boolean getBooleanObj() {
        return mBooleanObj;
    }

    public void setBooleanObj(Boolean booleanObj) {
        mBooleanObj = booleanObj;
    }

    public byte getByte() {
        return mByte;
    }

    public void setByte(byte aByte) {
        mByte = aByte;
    }

    public byte[] getByteArray() {
        return mByteArray;
    }

    public void setByteArray(byte[] byteArray) {
        mByteArray = byteArray;
    }

    public Byte getByteObj() {
        return mByteObj;
    }

    public void setByteObj(Byte byteObj) {
        mByteObj = byteObj;
    }

    public char getChar() {
        return mChar;
    }

    public void setChar(char aChar) {
        mChar = aChar;
    }

    public char[] getCharArray() {
        return mCharArray;
    }

    public void setCharArray(char[] charArray) {
        mCharArray = charArray;
    }

    public Character getCharObj() {
        return mCharObj;
    }

    public void setCharObj(Character charObj) {
        mCharObj = charObj;
    }

    public double getDouble() {
        return mDouble;
    }

    public void setDouble(double aDouble) {
        mDouble = aDouble;
    }

    public double[] getDoubleArray() {
        return mDoubleArray;
    }

    public void setDoubleArray(double[] doubleArray) {
        mDoubleArray = doubleArray;
    }

    public Double getDoubleObj() {
        return mDoubleObj;
    }

    public void setDoubleObj(Double doubleObj) {
        mDoubleObj = doubleObj;
    }

    public float getFloat() {
        return mFloat;
    }

    public void setFloat(float aFloat) {
        mFloat = aFloat;
    }

    public float[] getFloatArray() {
        return mFloatArray;
    }

    public void setFloatArray(float[] floatArray) {
        mFloatArray = floatArray;
    }

    public Float getFloatObj() {
        return mFloatObj;
    }

    public void setFloatObj(Float floatObj) {
        mFloatObj = floatObj;
    }

    public int getInt() {
        return mInt;
    }

    public void setInt(int anInt) {
        mInt = anInt;
    }

    public int[] getIntArray() {
        return mIntArray;
    }

    public void setIntArray(int[] intArray) {
        mIntArray = intArray;
    }

    public Integer getIntegerObj() {
        return mIntegerObj;
    }

    public void setIntegerObj(Integer integerObj) {
        mIntegerObj = integerObj;
    }

    public long getLong() {
        return mLong;
    }

    public void setLong(long aLong) {
        mLong = aLong;
    }

    public long[] getLongArray() {
        return mLongArray;
    }

    public void setLongArray(long[] longArray) {
        mLongArray = longArray;
    }

    public Long getLongObj() {
        return mLongObj;
    }

    public void setLongObj(Long longObj) {
        mLongObj = longObj;
    }

    public short getShort() {
        return mShort;
    }

    public void setShort(short aShort) {
        mShort = aShort;
    }

    public short[] getShortArray() {
        return mShortArray;
    }

    public void setShortArray(short[] shortArray) {
        mShortArray = shortArray;
    }

    public Short getShortObj() {
        return mShortObj;
    }

    public void setShortObj(Short shortObj) {
        mShortObj = shortObj;
    }

    public CharSequence getCharSequence() {
        return mCharSequence;
    }

    public void setCharSequence(CharSequence charSequence) {
        mCharSequence = charSequence;
    }

    public CharSequence[] getCharSequenceArray() {
        return mCharSequenceArray;
    }

    public void setCharSequenceArray(CharSequence[] charSequenceArray) {
        mCharSequenceArray = charSequenceArray;
    }

    public String getString() {
        return mString;
    }

    public void setString(String string) {
        mString = string;
    }

    public String[] getStringArray() {
        return mStringArray;
    }

    public void setStringArray(String[] stringArray) {
        mStringArray = stringArray;
    }

    public ArrayList<CharSequence> getCharSequenceArrayList() {
        return mCharSequenceArrayList;
    }

    public void setCharSequenceArrayList(ArrayList<CharSequence> charSequenceArrayList) {
        mCharSequenceArrayList = charSequenceArrayList;
    }

    public ArrayList<Integer> getIntegerArrayList() {
        return mIntegerArrayList;
    }

    public void setIntegerArrayList(ArrayList<Integer> integerArrayList) {
        mIntegerArrayList = integerArrayList;
    }

    public ArrayList<String> getStringArrayList() {
        return mStringArrayList;
    }

    public void setStringArrayList(ArrayList<String> stringArrayList) {
        mStringArrayList = stringArrayList;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle bundle) {
        mBundle = bundle;
    }

    public Parcelable[] getParcelableArray() {
        return mParcelableArray;
    }

    public void setParcelableArray(Parcelable[] parcelableArray) {
        mParcelableArray = parcelableArray;
    }

    public TestTypes.ParcelableImpl getParcelableImpl() {
        return mParcelableImpl;
    }

    public void setParcelableImpl(TestTypes.ParcelableImpl parcelableImpl) {
        mParcelableImpl = parcelableImpl;
    }

    public TestTypes.SerializableImpl getSerializableImpl() {
        return mSerializableImpl;
    }

    public void setSerializableImpl(TestTypes.SerializableImpl serializableImpl) {
        mSerializableImpl = serializableImpl;
    }

    public ArrayList<TestTypes.ParcelableImpl> getParcelableArrayList() {
        return mParcelableArrayList;
    }

    public void setParcelableArrayList(ArrayList<TestTypes.ParcelableImpl> parcelableArrayList) {
        mParcelableArrayList = parcelableArrayList;
    }

    public SparseArray<ParcelableImplExtension> getParcelableSparseArray() {
        return mParcelableSparseArray;
    }

    public void setParcelableSparseArray(SparseArray<ParcelableImplExtension> parcelableSparseArray) {
        mParcelableSparseArray = parcelableSparseArray;
    }

    public ParcelableImplExtension getParcelableImplExtension() {
        return mParcelableImplExtension;
    }

    public void setParcelableImplExtension(ParcelableImplExtension parcelableImplExtension) {
        mParcelableImplExtension = parcelableImplExtension;
    }

    public static class ParcelableImplExtension extends TestTypes.ParcelableImpl {
        private int mInt2;

        public ParcelableImplExtension(int anInt, int anInt2) {
            super(anInt);
            mInt2 = anInt2;
        }

        protected ParcelableImplExtension(Parcel in) {
            super(in);
            mInt2 = in.readInt();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ParcelableImplExtension that = (ParcelableImplExtension) o;

            return mInt2 == that.mInt2;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + mInt2;
            return result;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mInt2);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TestTypes.ParcelableImpl> CREATOR = new Creator<TestTypes.ParcelableImpl>() {
            @Override
            public TestTypes.ParcelableImpl createFromParcel(Parcel in) {
                return new TestTypes.ParcelableImpl(in);
            }

            @Override
            public TestTypes.ParcelableImpl[] newArray(int size) {
                return new TestTypes.ParcelableImpl[size];
            }
        };
    }

}
