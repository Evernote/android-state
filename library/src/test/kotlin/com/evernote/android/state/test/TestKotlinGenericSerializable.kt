package com.evernote.android.state.test

import com.evernote.android.state.State

/**
 * @author rwondratschek
 */
class TestKotlinGenericSerializable {

    @State
    var genericSerializable: GenericSerializable<String>? = null
}