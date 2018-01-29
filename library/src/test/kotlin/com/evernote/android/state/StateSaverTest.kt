package com.evernote.android.state

import android.os.Bundle
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters


/**
 * @author rwondratschek
 */
@FixMethodOrder(MethodSorters.JVM)
class StateSaverTest {

    @Suppress("ReplacePutWithAssignment")
    @Test
    fun verifyClassIsResolvedOnlyOnce() {
        val injectors = spy<MutableMap<Class<*>, Injector>>(mutableMapOf())
        val stateSaver = StateSaverImpl(injectors)

        val savedObject = StateSaverTest()
        stateSaver.saveInstanceState(savedObject, Bundle())
        verify(injectors, times(1)).put(eq(StateSaverTest::class.java), anyOrNull())

        stateSaver.saveInstanceState(savedObject, Bundle())
        stateSaver.restoreInstanceState(savedObject, Bundle())
        verify(injectors, times(1)).put(eq(StateSaverTest::class.java), anyOrNull())
    }

    @Suppress("ReplacePutWithAssignment")
    @Test
    fun verifyJavaClassIsSkipped() {
        val injectors = spy<MutableMap<Class<*>, Injector>>(mutableMapOf())
        val stateSaver = StateSaverImpl(injectors)

        val savedObject = Any()
        stateSaver.saveInstanceState(savedObject, Bundle())
        verify(injectors, never()).put(anyOrNull(), anyOrNull())
    }

    @Suppress("ReplacePutWithAssignment")
    @Test
    fun verifyAndroidClassIsSkipped() {
        val injectors = spy<MutableMap<Class<*>, Injector>>(mutableMapOf())
        val stateSaver = StateSaverImpl(injectors)

        val savedObject = Bundle()
        stateSaver.saveInstanceState(savedObject, Bundle())
        verify(injectors, never()).put(anyOrNull(), anyOrNull())
    }
}