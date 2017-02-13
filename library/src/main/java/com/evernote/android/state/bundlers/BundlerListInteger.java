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
package com.evernote.android.state.bundlers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.state.Bundler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwondratschek
 */
public class BundlerListInteger implements Bundler<List<Integer>> {
    @Override
    public void put(@NonNull String key, @NonNull List<Integer> value, @NonNull Bundle bundle) {
        ArrayList<Integer> arrayList = value instanceof ArrayList ? (ArrayList<Integer>) value : new ArrayList<>(value);
        bundle.putIntegerArrayList(key, arrayList);
    }

    @Nullable
    @Override
    public List<Integer> get(@NonNull String key, @NonNull Bundle bundle) {
        return bundle.getIntegerArrayList(key);
    }
}
