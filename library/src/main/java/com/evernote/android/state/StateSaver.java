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

import android.app.Application;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import java.util.LinkedHashMap;

/**
 * Entry point to save and restore objects.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class StateSaver {

    public static final String SUFFIX = "$$StateSaver";
    public static final String ANDROID_PREFIX = "android.";
    public static final String JAVA_PREFIX = "java.";

    private static final StateSaverImpl IMPL = new StateSaverImpl(new LinkedHashMap<Class<?>, Injector>());

    /**
     * Save the given {@code target} in the passed in {@link Bundle}.
     *
     * @param target The object containing fields annotated with {@link State}.
     * @param state  The object is saved into this bundle.
     */
    public static <T> void saveInstanceState(@NonNull T target, @NonNull Bundle state) {
        IMPL.saveInstanceState(target, state);
    }

    /**
     * Restore the given {@code target} from the passed in {@link Bundle}.
     *
     * @param target The object containing fields annotated with {@link State}.
     * @param state  The object is being restored from this bundle. Nothing is restored if the argument is {@code null}.
     */
    public static <T> void restoreInstanceState(@NonNull T target, @Nullable Bundle state) {
        IMPL.restoreInstanceState(target, state);
    }

    /**
     * Save the state of the given view and the other state inside of the returned {@link Parcelable}.
     *
     * @param target The view containing fields annotated with {@link State}.
     * @param state  The super state of the parent class of the view. Usually it isn't {@code null}.
     * @return A parcelable containing the view's state and its super state.
     */
    @NonNull
    public static <T extends View> Parcelable saveInstanceState(@NonNull T target, @Nullable Parcelable state) {
        return IMPL.saveInstanceState(target, state);
    }

    /**
     * Restore the sate of the given view and return the super state of the parent class.
     *
     * @param target The view containing fields annotated with {@link State}.
     * @param state  The parcelable containing the view's state and its super sate.
     * @return The super state of teh parent class of the view. Usually it isn't {@code null}.
     */
    @Nullable
    public static <T extends View> Parcelable restoreInstanceState(@NonNull T target, @Nullable Parcelable state) {
        return IMPL.restoreInstanceState(target, state);
    }

    /**
     * Turns automatic instance saving on or off for all activities and fragments from the support library. This avoids
     * manual calls of {@link #saveInstanceState(Object, Bundle)} and {@link #restoreInstanceState(Object, Bundle)}, instead
     * the library is doing this automatically for you. It's still necessary to annotate fields with {@link State}, though.
     * <br>
     * <br>
     * The best place to turn on this feature is in your {@link Application#onCreate()} method.
     *
     * @param application Your application instance.
     * @param enabled Whether this feature should be enabled.
     */
    public static void setEnabledForAllActivitiesAndSupportFragments(@NonNull Application application, boolean enabled) {
        IMPL.setEnabledForAllActivitiesAndSupportFragments(application, enabled);
    }

    private StateSaver() {
        throw new UnsupportedOperationException();
    }
}
