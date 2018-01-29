package com.evernote.android.state.test

import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Looper
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

/**
 * @author rwondratschek
 */
@RunWith(AndroidJUnit4::class)
class GlobalStateSaverTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule<MyActivity>(MyActivity::class.java, true, false)

    @Test
    fun verifyStateSaverGloballyEnabled() {
        val context = InstrumentationRegistry.getTargetContext()
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(context.applicationContext as Application, true)
        activityRule.launchActivity(Intent(context, MyActivity::class.java)) // launch manually delayed

        var count = 0

        repeat(4) {
            count++
            assertThat(activity.state).isEqualTo(count)
            assertThat(activity.fragment.state).isEqualTo(count)
            assertThat(activity.fragment.innerFragment.state).isEqualTo(count)

            changeOrientation()
        }
    }

    @Test
    fun verifyStateSaverGloballyDisabled() {
        val context = InstrumentationRegistry.getTargetContext()
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(context.applicationContext as Application, false)
        activityRule.launchActivity(Intent(context, MyActivity::class.java)) // launch manually delayed

        val count = 1

        repeat(4) {
            assertThat(activity.state).isEqualTo(count)
            assertThat(activity.fragment.state).isEqualTo(count)
            assertThat(activity.fragment.innerFragment.state).isEqualTo(count)

            changeOrientation()
        }
    }

    private fun changeOrientation() {
        activity.requestedOrientation =
                if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Thread.sleep(500)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private val activity: MyActivity
        get() {
            return if (Looper.getMainLooper() == Looper.myLooper()) {
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first { it is MyActivity } as MyActivity
            } else {
                val latch = CountDownLatch(1)
                lateinit var result: MyActivity
                activityRule.runOnUiThread {
                    result = activity
                    latch.countDown()
                }
                latch.await()
                result
            }
        }
}

class MyActivity : FragmentActivity() {

    @State
    var state = 0

    val fragment: MyFragment get() = supportFragmentManager.findFragmentByTag("Tag") as MyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(MyFragment().apply {
                arguments = Bundle().apply { putBoolean("create_inner", true) }
            }, "Tag").commit()
        }
        state++
    }
}

class MyFragment : Fragment() {

    @State
    var state = 0

    val innerFragment: MyFragment get() = fragmentManager!!.findFragmentByTag("Inner") as MyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && arguments?.getBoolean("create_inner", false) == true) {
            fragmentManager!!.beginTransaction().add(MyFragment(), "Inner").commit()
        }
        state++
    }
}

