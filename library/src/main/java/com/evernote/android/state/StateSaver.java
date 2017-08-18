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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entry point to save and restore objects.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class StateSaver {

    public static final String SUFFIX = "$$StateSaver";
    public static final String ANDROID_PREFIX = "android.";
    public static final String JAVA_PREFIX = "java.";

    private static final Map<Class<?>, Injector> INJECTORS = new LinkedHashMap<>();

    private static Injector getInjector(Class<?> cls)
            throws IllegalAccessException, InstantiationException {
        Injector injector = INJECTORS.get(cls);
        if (injector != null || INJECTORS.contains(cls)) {
            return injector;
        }
        String clsName = cls.getName();
        if (clsName.startsWith(ANDROID_PREFIX) || clsName.startsWith(JAVA_PREFIX)) {
            return null;
        }
        try {
            Class<?> injectorClass = Class.forName(clsName + SUFFIX);
            injector = (Injector) injectorClass.newInstance();
        } catch (ClassNotFoundException e) {
            injector = getInjector(cls.getSuperclass());
        }
        INJECTORS.put(cls, injector);
        return injector;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Injector> T safeGet(Object target, Injector nop) {
        try {
            Class<?> targetClass = target.getClass();
            Injector injector = getInjector(targetClass);
            if (injector == null) {
                injector = nop;
            }
            return (T) injector;
        } catch (Exception e) {
            throw new RuntimeException("Unable to inject state for " + target, e);
        }
    }

    /**
     * Save the given {@code target} in the passed in {@link Bundle}.
     *
     * @param target The object containing fields annotated with {@link State}.
     * @param state  The object is saved into this bundle.
     */
    public static <T> void saveInstanceState(@NonNull T target, @NonNull Bundle state) {
        Injector.Object<T> injector = safeGet(target, Injector.Object.DEFAULT);
        injector.save(target, state);
    }

    /**
     * Restore the given {@code target} from the passed in {@link Bundle}.
     *
     * @param target The object containing fields annotated with {@link State}.
     * @param state  The object is being restored from this bundle. Nothing is restored if the argument is {@code null}.
     */
    public static <T> void restoreInstanceState(@NonNull T target, @Nullable Bundle state) {
        if (state != null) {
            Injector.Object<T> injector = safeGet(target, Injector.Object.DEFAULT);
            injector.restore(target, state);
        }
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
        Injector.View<T> injector = safeGet(target, Injector.View.DEFAULT);
        return injector.save(target, state);
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
        if (state != null) {
            Injector.View<T> injector = safeGet(target, Injector.View.DEFAULT);
            return injector.restore(target, state);
        } else {
            return null;
        }
    }

    private StateSaver() {
        throw new UnsupportedOperationException();
    }
}
