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
package com.evernote.android.state.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.evernote.android.state.State;
import com.evernote.android.state.StateReflection;

/**
 * @author rwondratschek
 */
public class MainActivity extends FragmentActivity {

    @State
    private int mTest1;

    @StateReflection
    private int test2;

    @State
    public int test3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTest1++;
        test2++;
        test3++;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(new TestFragment(), "Tag").commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume mTest1=" + mTest1 + " test2=" + test2 + " test3=" + test3);
    }

    public int getTest1() {
        return mTest1;
    }

    public void setTest1(int test) {
        mTest1 = test;
    }

    public static final class TestFragment extends Fragment {

        @State
        public int test;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            test++;
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.i("TestFragment", "onResume test=" + test);
        }
    }
}
