package com.evernote.android.state;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Helper interface to save any object inside a {@link Bundle}.
 *
 * @param <T> The object class.
 */
public interface Bundler<T> {

    /**
     * Save the given value inside of the bundle.
     *
     * @param key    The base key for this value. Each field of the value should have a separate key with this prefix.
     * @param value  The object which should be saved in the bundle.
     * @param bundle The bundle where the value should be stored.
     */
    void put(@NonNull String key, @NonNull T value, @NonNull Bundle bundle);

    /**
     * Restore the value from the bundle.
     *
     * @param key    The base key for this value. Each field of the value should have a separate key with this prefix.
     * @param bundle The bundle in which the value is stored.
     * @return The object restored from the bundle.
     */
    @Nullable
    T get(@NonNull String key, @NonNull Bundle bundle);
}
