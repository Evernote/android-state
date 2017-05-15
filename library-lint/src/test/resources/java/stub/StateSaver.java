/* *****************************************************************************
 * Copyright (c) 2017 Frankie Sardo, and Evernote Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frankie Sardo - initial API and implementation
 *    Ralf Wondratschek - documentation and feature enhancement
 *******************************************************************************/
package com.evernote.android.state;

import android.os.Bundle;

/**
 * Entry point to save and restore objects.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class StateSaver {
    public static <T> void saveInstanceState(T target, Bundle state) {
        // STUB
    }
    public static <T> void restoreInstanceState(T target, Bundle state) {
        // STUB
    }

    private StateSaver() {
        throw new UnsupportedOperationException();
    }
}
