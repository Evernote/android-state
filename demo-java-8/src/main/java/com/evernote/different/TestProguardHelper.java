package com.evernote.different;

import androidx.annotation.Keep;

/**
 * @author rwondratschek
 */
public final class TestProguardHelper {

    private TestProguardHelper() {
        // no op
    }

    @Keep
    public static void setValue(TestProguard instance, int value) {
        instance.setValue(value);
    }

    @Keep
    public static void verifyValue(TestProguard instance, int value) {
        instance.verifyValue(value);
    }
}
