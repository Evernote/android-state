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
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.state.Bundler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwondratschek
 */
public class BundlerListParcelable implements Bundler<List<? extends Parcelable>> {
    @SuppressWarnings("unchecked")
    @Override
    public void put(@NonNull String key, @NonNull List<? extends Parcelable> value, @NonNull Bundle bundle) {
        ArrayList<? extends Parcelable> arrayList = value instanceof ArrayList ? (ArrayList<? extends Parcelable>) value : new ArrayList<>(value);
        bundle.putParcelableArrayList(key, arrayList);
    }

    @Nullable
    @Override
    public List<? extends Parcelable> get(@NonNull String key, @NonNull Bundle bundle) {
        return bundle.getParcelableArrayList(key);
    }
}
