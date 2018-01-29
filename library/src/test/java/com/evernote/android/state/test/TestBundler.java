package com.evernote.android.state.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.state.Bundler;
import com.evernote.android.state.State;
import com.evernote.android.state.StateReflection;

public class TestBundler {

    @State(MyBundler.class)
    private Data mData2;

    @StateReflection(value = MyBundler.class)
    private Data mDataReflOtherName;

    public Data getData2() {
        return mData2;
    }

    public void setData2(Data data2) {
        mData2 = data2;
    }

    public Data getDataRefl() {
        return mDataReflOtherName;
    }

    public void setDataRefl(Data data) {
        mDataReflOtherName = data;
    }

    public static final class Data {
        public int int1;
        public int int2;
    }

    public static final class MyBundler implements Bundler<Data> {
        @Override
        public void put(@NonNull String key, @NonNull Data value, @NonNull Bundle bundle) {
            bundle.putInt(key + "1", value.int1);
            bundle.putInt(key + "2", value.int2);
        }

        @Override
        @Nullable
        public Data get(@NonNull String key, @NonNull Bundle bundle) {
            if (bundle.containsKey(key + "1")) {
                Data data = new Data();
                data.int1 = bundle.getInt(key + "1");
                data.int2 = bundle.getInt(key + "2");
                return data;
            } else {
                return null;
            }
        }
    }
}
