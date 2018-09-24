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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.state.Bundler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwondratschek
 */
public class BundlerListCharSequence implements Bundler<List<CharSequence>> {
    @Override
    public void put(@NonNull String key, @NonNull List<CharSequence> value, @NonNull Bundle bundle) {
        ArrayList<CharSequence> arrayList = value instanceof ArrayList ? (ArrayList<CharSequence>) value : new ArrayList<>(value);
        bundle.putCharSequenceArrayList(key, arrayList);
    }

    @Nullable
    @Override
    public List<CharSequence> get(@NonNull String key, @NonNull Bundle bundle) {
        return bundle.getCharSequenceArrayList(key);
    }
}
