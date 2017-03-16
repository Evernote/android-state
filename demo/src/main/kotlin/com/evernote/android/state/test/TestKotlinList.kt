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

import com.evernote.android.state.State
import com.evernote.android.state.bundlers.BundlerListInteger
import com.evernote.android.state.bundlers.BundlerListParcelable
import com.evernote.android.state.bundlers.BundlerListString

/**
 * Copyright 2017 Evernote Corporation. All rights reserved.
 *
 * Created by rwondratschek on 2/13/17.
 */
class TestKotlinList {

    @State(BundlerListString::class)
    var stringList = listOf("Hello", "World")

    @State(BundlerListInteger::class)
    var emptyList = emptyList<Int>()

    @State(BundlerListParcelable::class)
    var parcelableList = mutableListOf(TestTypes.ParcelableImpl(1))

    @State @Suppress("unused")
    var innerClass: TestKotlinInnerClass.Inner? = null
}