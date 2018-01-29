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
package com.evernote.android.state.test;

import com.evernote.android.state.State;
import com.evernote.android.state.bundlers.BundlerListInteger;
import com.evernote.android.state.bundlers.BundlerListParcelable;
import com.evernote.android.state.bundlers.BundlerListString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author rwondratschek
 */
public class TestJavaList {

    @State(BundlerListString.class)
    private List<String> stringList = Collections.unmodifiableList(new ArrayList<String>(){{
        add("Hello");
        add("World");
    }});

    @State(BundlerListInteger.class)
    private List<Integer> emptyList = Collections.emptyList();

    @State(BundlerListParcelable.class)
    private List<TestTypes.ParcelableImpl> parcelableList = Collections.singletonList(new TestTypes.ParcelableImpl(1));

    @State(BundlerListParcelable.class)
    List<TestTypes.ParcelableImpl> parcelableListPublic = Collections.singletonList(new TestTypes.ParcelableImpl(1));

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<Integer> getEmptyList() {
        return emptyList;
    }

    public void setEmptyList(List<Integer> emptyList) {
        this.emptyList = emptyList;
    }

    public List<TestTypes.ParcelableImpl> getParcelableList() {
        return parcelableList;
    }

    public void setParcelableList(List<TestTypes.ParcelableImpl> parcelableList) {
        this.parcelableList = parcelableList;
    }
}
