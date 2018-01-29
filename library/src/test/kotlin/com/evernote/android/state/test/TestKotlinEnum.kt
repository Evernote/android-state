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

/**
 * Copyright 2017 Evernote Corporation. All rights reserved.
 *
 * Created by rwondratschek on 2/13/17.
 */
enum class KotlinEnum {
    LEFT, RIGHT
}

class TestKotlinEnum {
    @State
    var kotlinEnum = KotlinEnum.LEFT
}