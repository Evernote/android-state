@file:Suppress("unused")

package com.evernote.android.state.test

import com.evernote.android.state.State
import java.io.Serializable
import java.util.*

/**
 * Copyright 2017 Evernote Corporation. All rights reserved.
 *
 * Created by rwondratschek on 3/1/17.
 */
class MyComparator : Comparator<MyComparable>, Serializable {
    override fun compare(o1: MyComparable, o2: MyComparable): Int = 0
}

class MyComparable : Comparable<MyComparable>, Serializable {
    override fun compareTo(other: MyComparable): Int = 0
}

interface MyInterface<T : MyInterface<T>> {
    fun doSomething(other: T)
}

class MyInterfaceImpl : MyInterface<MyInterfaceImpl>, Serializable {
    override fun doSomething(other: MyInterfaceImpl) {}
}

interface MySerializableInterface<T : MySerializableInterface<T>> : Serializable {
    fun doSomething(other: T)
}

class MySerializableImpl : MySerializableInterface<MySerializableImpl> {
    override fun doSomething(other: MySerializableImpl) {}
}

class TestKotlinComparable {

    @State
    var comparator: MyComparator? = null

    @State
    var comparable: MyComparable? = null

    @State
    var myInterface: MyInterfaceImpl? = null

    @State
    var mySerializableInterface: MySerializableImpl? = null
}