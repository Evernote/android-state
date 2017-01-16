//package com.evernote.android.state.test;
//
//import android.os.Bundle;
//
//import com.evernote.android.state.Bundler;
//import com.evernote.android.state.State;
//
//public class TestPrivateClassBundler {
//    @State(MyBundler.class)
//    private Data mData2;
//
//    public Data getData2() {
//        return mData2;
//    }
//
//    public void setData2(Data data2) {
//        mData2 = data2;
//    }
//
//    public static final class Data {
//        private int int1;
//        private int int2;
//    }
//
//    private static final class MyBundler implements Bundler<Data> {
//        @Override
//        public void put(String key, Data value, Bundle bundle) {
//            bundle.putInt(key + "1", value.int1);
//            bundle.putInt(key + "2", value.int2);
//        }
//
//        @Override
//        public Data get(String key, Bundle bundle) {
//            Data data = new Data();
//            data.int1 = bundle.getInt(key + "1");
//            data.int2 = bundle.getInt(key + "2");
//            return data;
//        }
//    }
//}
