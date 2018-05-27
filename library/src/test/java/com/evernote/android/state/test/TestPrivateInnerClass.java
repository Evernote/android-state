package com.evernote.android.state.test;

import android.os.Parcel;
import android.os.Parcelable;

import com.evernote.android.state.StateReflection;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author rwondratschek
 */
public class TestPrivateInnerClass {

    @StateReflection
    private Inner mInner2;

    @StateReflection
    private ParcelableImpl mParcelable2;

    @StateReflection
    private ArrayList<Inner> mInnerList = new ArrayList<>();

    @StateReflection
    private ArrayList<ParcelableImpl> mParcelableList = new ArrayList<>();

    public TestPrivateInnerClass() {
        setToA();
    }

    public void setToA() {
        mInner2 = Inner.A;
        mParcelable2 = new ParcelableImpl(1);
        mInnerList = new ArrayList<>(Collections.singletonList(Inner.A));
        mParcelableList = new ArrayList<>(Collections.singletonList(new ParcelableImpl(1)));
    }

    public void setToB() {
        mInner2 = Inner.B;
        mParcelable2 = new ParcelableImpl(2);
        mInnerList = new ArrayList<>(Collections.singletonList(Inner.B));
        mParcelableList = new ArrayList<>(Collections.singletonList(new ParcelableImpl(2)));
    }

    public boolean isA() {
        return mInner2 == Inner.A && mParcelable2.mInt == 1 && mInnerList.get(0) == Inner.A && mParcelableList.get(0).mInt == 1;
    }

    public boolean isB() {
        return mInner2 == Inner.B && mParcelable2.mInt == 2 && mInnerList.get(0) == Inner.B && mParcelableList.get(0).mInt == 2;
    }

    private enum Inner {
        A, B
    }

    private static class ParcelableImpl implements Parcelable {
        private int mInt;

        public ParcelableImpl(int anInt) {
            mInt = anInt;
        }

        protected ParcelableImpl(Parcel in) {
            mInt = in.readInt();
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

}
