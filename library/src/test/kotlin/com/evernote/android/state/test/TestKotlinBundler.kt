package com.evernote.android.state.test

import android.os.Bundle
import com.evernote.android.state.Bundler
import com.evernote.android.state.State
import java.io.Serializable

/**
 * @author rwondratschek
 */
class MyClass {
    @State(value = WrapperBundler::class)
    var wrapped = Wrapper(42)

    @State(value = WrapperBundlerGeneric::class)
    var wrappedGeneric = Wrapper(42)
}

class Wrapper<out T>(val content: T?)

class WrapperBundler : Bundler<Wrapper<Serializable>> {
    override fun get(key: String, bundle: Bundle): Wrapper<Serializable>? {
        @Suppress("UNCHECKED_CAST")
        return Wrapper(bundle.getSerializable(key) as Serializable)
    }

    override fun put(key: String, value: Wrapper<Serializable>, bundle: Bundle) {
        bundle.putSerializable(key, value.content)
    }
}

class WrapperBundlerGeneric<T : Serializable> : Bundler<Wrapper<T>> {
    override fun get(key: String, bundle: Bundle): Wrapper<T>? {
        @Suppress("UNCHECKED_CAST")
        return Wrapper(bundle.getSerializable(key) as T)
    }

    override fun put(key: String, value: Wrapper<T>, bundle: Bundle) {
        bundle.putSerializable(key, value.content)
    }
}