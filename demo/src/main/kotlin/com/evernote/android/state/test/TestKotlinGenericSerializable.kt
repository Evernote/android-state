package com.evernote.android.state.test

import com.evernote.android.state.State
import com.evernote.android.test.data.GenericSerializable

/**
 * @author rwondratschek
 */
class TestKotlinGenericSerializable {

    @State
    var genericSerializable: GenericSerializable<String>? = null
}