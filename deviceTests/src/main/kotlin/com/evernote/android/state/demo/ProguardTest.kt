package com.evernote.android.state.demo

import android.os.Bundle
import android.support.test.runner.AndroidJUnit4
import com.evernote.android.state.StateSaver
import com.evernote.android.test.data.TestProguard
import com.evernote.android.test.data.TestProguardBundler
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * @author rwondratschek
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
class ProguardTest {

    @Test
    fun testProguardRules() {
        val data = TestProguard()

        data.verifyValue(0)

        data.setValue(1)

        val bundle = Bundle()
        StateSaver.saveInstanceState(data, bundle)

        data.setValue(0)
        data.verifyValue(0)

        StateSaver.restoreInstanceState(data, bundle)
        data.verifyValue(1)
    }

    @Test
    fun verifyCodeObfuscated() {
        TestProguard::class.java.getDeclaredField("a") // test1
        TestProguard::class.java.getDeclaredField("b") // test2
        TestProguard::class.java.getDeclaredField("test3")
        TestProguard::class.java.getDeclaredMethod("a") // getTest2()

        TestProguardBundler::class.java.getDeclaredField("a") // mData2
        TestProguardBundler::class.java.getDeclaredField("mDataReflOtherName")
    }

    @Test
    fun testBundler() {
        val data = TestProguardBundler()
        data.verifyValue(0)

        data.setValue(1)

        val bundle = Bundle()
        StateSaver.saveInstanceState(data, bundle)

        data.setValue(0)
        data.verifyValue(0)

        StateSaver.restoreInstanceState(data, bundle)
        data.verifyValue(1)
    }

}