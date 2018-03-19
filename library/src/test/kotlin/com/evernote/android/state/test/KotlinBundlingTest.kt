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
@file:Suppress("UsePropertyAccessSyntax")

package com.evernote.android.state.test

import android.os.Bundle
import com.evernote.android.state.StateSaver
import org.assertj.core.api.Assertions.assertThat
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

/**
 * Copyright 2017 Evernote Corporation. All rights reserved.
 *
 * Created by rwondratschek on 2/13/17.
 */
@FixMethodOrder(MethodSorters.JVM)
class KotlinBundlingTest {

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
        val kotlinList = com.evernote.android.state.test.TestJavaList()
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

    @Test
    fun testKotlinEnum() {
        val kotlinEnum = TestKotlinEnum()
        assertThat(kotlinEnum.kotlinEnum).isEqualTo(KotlinEnum.LEFT)
        assertThat(kotlinEnum.getKotlinEnum1()).isEqualTo(KotlinEnum.LEFT)

        val bundle = Bundle()
        StateSaver.saveInstanceState(kotlinEnum, bundle)

        kotlinEnum.kotlinEnum = KotlinEnum.RIGHT
        kotlinEnum.setKotlinEnum1(KotlinEnum.RIGHT)
        StateSaver.restoreInstanceState(kotlinEnum, bundle)
        assertThat(kotlinEnum.kotlinEnum).isEqualTo(KotlinEnum.LEFT)
        assertThat(kotlinEnum.getKotlinEnum1()).isEqualTo(KotlinEnum.LEFT)

        kotlinEnum.kotlinEnum = KotlinEnum.RIGHT
        kotlinEnum.setKotlinEnum1(KotlinEnum.RIGHT)
        StateSaver.saveInstanceState(kotlinEnum, bundle)

        kotlinEnum.kotlinEnum = KotlinEnum.LEFT
        kotlinEnum.setKotlinEnum1(KotlinEnum.LEFT)
        StateSaver.restoreInstanceState(kotlinEnum, bundle)
        assertThat(kotlinEnum.kotlinEnum).isEqualTo(KotlinEnum.RIGHT)
        assertThat(kotlinEnum.getKotlinEnum1()).isEqualTo(KotlinEnum.RIGHT)
    }

    @Test
    fun testKotlinBoolean() {
        val kotlinBoolean = TestKotlinBoolean()
        assertThat(kotlinBoolean.test1).isFalse()
        assertThat(kotlinBoolean.isTest2).isFalse()
        assertThat(kotlinBoolean.mTest3).isFalse()

        val bundle = Bundle()
        StateSaver.saveInstanceState(kotlinBoolean, bundle)

        kotlinBoolean.test1 = true
        kotlinBoolean.isTest2 = true
        kotlinBoolean.mTest3 = true

        StateSaver.restoreInstanceState(kotlinBoolean, bundle)
        assertThat(kotlinBoolean.test1).isFalse()
        assertThat(kotlinBoolean.isTest2).isFalse()
        assertThat(kotlinBoolean.mTest3).isFalse()

        kotlinBoolean.test1 = true
        kotlinBoolean.isTest2 = true
        kotlinBoolean.mTest3 = true
        StateSaver.saveInstanceState(kotlinBoolean, bundle)

        kotlinBoolean.test1 = false
        kotlinBoolean.isTest2 = false
        kotlinBoolean.mTest3 = false

        StateSaver.restoreInstanceState(kotlinBoolean, bundle)
        assertThat(kotlinBoolean.test1).isTrue()
        assertThat(kotlinBoolean.isTest2).isTrue()
        assertThat(kotlinBoolean.mTest3).isTrue()
    }

    @Test
    fun testGenericSerializable() {
        val javaGenericSerializable = TestJavaGenericSerializable()
        val kotlinGenericSerializable = TestKotlinGenericSerializable()
        assertThat(javaGenericSerializable.genericSerializable).isNull()
        assertThat(kotlinGenericSerializable.genericSerializable).isNull()

        val bundle = Bundle()
        StateSaver.saveInstanceState(javaGenericSerializable, bundle)
        StateSaver.saveInstanceState(kotlinGenericSerializable, bundle)

        javaGenericSerializable.genericSerializable = GenericSerializable()
        kotlinGenericSerializable.genericSerializable = GenericSerializable()

        StateSaver.restoreInstanceState(javaGenericSerializable, bundle)
        StateSaver.restoreInstanceState(kotlinGenericSerializable, bundle)
        assertThat(javaGenericSerializable.genericSerializable).isNull()
        assertThat(kotlinGenericSerializable.genericSerializable).isNull()

        javaGenericSerializable.genericSerializable = GenericSerializable()
        kotlinGenericSerializable.genericSerializable = GenericSerializable()
        StateSaver.saveInstanceState(javaGenericSerializable, bundle)
        StateSaver.saveInstanceState(kotlinGenericSerializable, bundle)

        javaGenericSerializable.genericSerializable = null
        kotlinGenericSerializable.genericSerializable = null

        StateSaver.restoreInstanceState(javaGenericSerializable, bundle)
        StateSaver.restoreInstanceState(kotlinGenericSerializable, bundle)
        assertThat(javaGenericSerializable.genericSerializable as Any).isNotNull()
        assertThat(kotlinGenericSerializable.genericSerializable as Any).isNotNull()
    }

    @Test
    fun testWithNoneConcreteType() {
        val item = MyClass()

        assertThat(item.wrapped.content).isEqualTo(42)
        assertThat(item.wrappedGeneric.content).isEqualTo(42)

        val bundle = Bundle()
        StateSaver.saveInstanceState(item, bundle)

        item.wrapped = Wrapper(10)
        item.wrappedGeneric = Wrapper(10)

        StateSaver.restoreInstanceState(item, bundle)
        assertThat(item.wrapped.content).isEqualTo(42)
        assertThat(item.wrappedGeneric.content).isEqualTo(42)

        item.wrapped = Wrapper(10)
        item.wrappedGeneric = Wrapper(10)
        StateSaver.saveInstanceState(item, bundle)

        item.wrapped = Wrapper(42)
        item.wrappedGeneric = Wrapper(42)

        StateSaver.restoreInstanceState(item, bundle)
        assertThat(item.wrapped.content).isEqualTo(10)
        assertThat(item.wrappedGeneric.content).isEqualTo(10)
    }
}