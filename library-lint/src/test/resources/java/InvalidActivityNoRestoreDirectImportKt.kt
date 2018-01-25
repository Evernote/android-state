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
package com.evernote.android.state.lint

import android.app.Activity
import android.os.Bundle
import com.evernote.android.state.StateSaver.saveInstanceState

/**
 * @author rwondratschek
 */
class InvalidActivityNoRestoreDirectImportKt : Activity() {
    protected override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
    }

    protected override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveInstanceState(this, outState)
    }
}
