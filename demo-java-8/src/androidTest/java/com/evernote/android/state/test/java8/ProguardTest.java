package com.evernote.android.state.test.java8;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import com.evernote.android.state.StateSaver;
import com.evernote.different.TestProguard;
import com.evernote.different.TestProguardBundler;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author rwondratschek
 */
@RunWith(AndroidJUnit4.class)
public class ProguardTest {

    @Test
    public void testProguardRules() {
        TestProguard data = new TestProguard();
        data.verifyValue(0);
        data.setValue(1);

        Bundle bundle = new Bundle();
        StateSaver.saveInstanceState(data, bundle);

        data.setValue(0);
        data.verifyValue(0);

        StateSaver.restoreInstanceState(data, bundle);
        data.verifyValue(1);
    }

    @Test
    public void verifyCodeObfuscated() throws Exception {
        TestProguard.class.getDeclaredField("test1"); // test1
        TestProguard.class.getDeclaredField("test2"); // test2
        TestProguard.class.getDeclaredField("test3");
        TestProguard.class.getDeclaredMethod("a"); // getTest2()

        TestProguardBundler.class.getDeclaredField("mData2"); // mData2
        TestProguardBundler.class.getDeclaredField("mDataReflOtherName");
        TestProguardBundler.class.getDeclaredMethod("a"); // getData2()
    }

    @Test
    public void testBundler() {
        TestProguardBundler data = new TestProguardBundler();
        data.verifyValue(0);

        data.setValue(1);

        Bundle bundle = new Bundle();
        StateSaver.saveInstanceState(data, bundle);

        data.setValue(0);
        data.verifyValue(0);

        StateSaver.restoreInstanceState(data, bundle);
        data.verifyValue(1);
    }
}
