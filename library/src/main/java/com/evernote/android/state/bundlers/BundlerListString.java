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
public class BundlerListString implements Bundler<List<String>> {
    @Override
    public void put(@NonNull String key, @NonNull List<String> value, @NonNull Bundle bundle) {
        ArrayList<String> arrayList = value instanceof ArrayList ? (ArrayList<String>) value : new ArrayList<>(value);
        bundle.putStringArrayList(key, arrayList);
    }

    @Nullable
    @Override
    public List<String> get(@NonNull String key, @NonNull Bundle bundle) {
        return bundle.getStringArrayList(key);
    }
}
