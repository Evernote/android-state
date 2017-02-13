/* *****************************************************************************
 * Copyright (c) 2017 Evernote Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Wondratschek
 *******************************************************************************/
package com.evernote.android.state.test

import android.os.Bundle
import com.evernote.android.state.StateSaver
import org.assertj.core.api.Assertions.assertThat
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Copyright 2017 Evernote Corporation. All rights reserved.
 *
 * Created by rwondratschek on 2/13/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@FixMethodOrder(MethodSorters.JVM)
class TestKotlinBundling {

    @Test
    fun testKotlinList() {
        val kotlinList = TestKotlinList()
        assertThat(kotlinList.emptyList).isEmpty()
        assertThat(kotlinList.stringList).isNotEmpty()
        assertThat(kotlinList.parcelableList[0]).isEqualTo(TestTypes.ParcelableImpl(1))

        val bundle = Bundle()
        StateSaver.saveInstanceState(kotlinList, bundle)

        kotlinList.stringList = mutableListOf("single")
        kotlinList.parcelableList = mutableListOf(TestTypes.ParcelableImpl(2))
        assertThat(kotlinList.stringList).containsExactly("single")
        assertThat(kotlinList.parcelableList[0]).isEqualTo(TestTypes.ParcelableImpl(2))

        StateSaver.restoreInstanceState(kotlinList, bundle)
        assertThat(kotlinList.stringList).containsExactly("Hello", "World")
        assertThat(kotlinList.parcelableList[0]).isEqualTo(TestTypes.ParcelableImpl(1))
    }

    @Test
    fun testJavaList() {
        val kotlinList = TestJavaList()
        assertThat(kotlinList.emptyList).isEmpty()
        assertThat(kotlinList.stringList).isNotEmpty()
        assertThat(kotlinList.parcelableList[0]).isEqualTo(TestTypes.ParcelableImpl(1))
        assertThat(kotlinList.parcelableListPublic[0]).isEqualTo(TestTypes.ParcelableImpl(1))

        val bundle = Bundle()
        StateSaver.saveInstanceState(kotlinList, bundle)

        kotlinList.stringList = mutableListOf("single")
        kotlinList.parcelableList = mutableListOf(TestTypes.ParcelableImpl(2))
        kotlinList.parcelableListPublic = mutableListOf(TestTypes.ParcelableImpl(2))
        assertThat(kotlinList.stringList).containsExactly("single")
        assertThat(kotlinList.parcelableList[0]).isEqualTo(TestTypes.ParcelableImpl(2))
        assertThat(kotlinList.parcelableListPublic[0]).isEqualTo(TestTypes.ParcelableImpl(2))

        StateSaver.restoreInstanceState(kotlinList, bundle)
        assertThat(kotlinList.stringList).containsExactly("Hello", "World")
        assertThat(kotlinList.parcelableList[0]).isEqualTo(TestTypes.ParcelableImpl(1))
        assertThat(kotlinList.parcelableListPublic[0]).isEqualTo(TestTypes.ParcelableImpl(1))
    }
}