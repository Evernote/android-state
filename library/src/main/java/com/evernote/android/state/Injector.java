package com.evernote.android.state;

import android.os.Bundle;
import android.os.Parcelable;

public class Injector {

    protected Injector() {
    }

    public abstract static class Object<T> extends Injector {
        /*package*/ static final Object<?> DEFAULT = new Object<java.lang.Object>() {
            @Override
            public void restore(java.lang.Object target, Bundle state) {
                // no op
            }

            @Override
            public void save(java.lang.Object target, Bundle state) {
                // no op
            }
        };

        public abstract void restore(T target, Bundle state);

        public abstract void save(T target, Bundle state);
    }

    public abstract static class View<T> extends Injector {

        /*package*/ static final View<?> DEFAULT = new View<java.lang.Object>() {
        };

        public Parcelable restore(T target, Parcelable state) {
            return state;
        }

        public Parcelable save(T target, Parcelable state) {
            return state;
        }
    }
}
