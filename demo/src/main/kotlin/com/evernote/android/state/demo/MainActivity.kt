/* *****************************************************************************
 * Copyright (c) 2017 Evernote Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Wondratschek - initial version
 *******************************************************************************/
package com.evernote.android.state.demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log

import com.evernote.android.state.State
import com.evernote.android.state.StateReflection

/**
 * @author rwondratschek
 */
class MainActivity : FragmentActivity() {

    @State
    var test1: Int = 0

    @StateReflection
    private var test2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test1++
        test2++

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(TestFragment(), "Tag").commit()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "onResume mTest1=$test1 test2=$test2")
    }
}

class TestFragment : Fragment() {

    @State
    var test: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test++
    }

    override fun onResume() {
        super.onResume()
        Log.i("TestFragment", "onResume test=" + test)
    }
}
