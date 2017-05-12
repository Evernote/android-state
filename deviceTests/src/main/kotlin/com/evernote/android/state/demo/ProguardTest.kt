package com.evernote.android.state.demo

import android.os.Bundle
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.evernote.android.state.StateSaver
import org.assertj.core.api.Assertions.assertThat
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * @author rwondratschek
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
class ProguardTest {

    @Rule @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testProguardRules() {
        val activity = activityRule.activity
        val test2Field = MainActivity::class.java.getDeclaredField("test2")
        test2Field.isAccessible = true

        assertThat(activity.test1).isEqualTo(0)
        assertThat(test2Field.getInt(activity)).isEqualTo(0)
        assertThat(activity.test3).isEqualTo(0)

        activity.test1 = 1
        test2Field.setInt(activity, 2)
        activity.test3 = 3

        val bundle = Bundle()
        StateSaver.saveInstanceState(activity, bundle)

        activity.test1 = 0
        test2Field.setInt(activity, 0)
        activity.test3 = 0

        assertThat(activity.test1).isEqualTo(0)
        assertThat(test2Field.getInt(activity)).isEqualTo(0)
        assertThat(activity.test3).isEqualTo(0)

        StateSaver.restoreInstanceState(activity, bundle)

        assertThat(activity.test1).isEqualTo(1)
        assertThat(test2Field.getInt(activity)).isEqualTo(2)
        assertThat(activity.test3).isEqualTo(3)
    }
}