package com.evernote.android.state;

import android.os.Bundle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields or properties with this annotation will be saved in a {@link Bundle} and restored
 * from it while using the {@link StateSaver}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface State {
    Class<? extends Bundler> value() default Bundler.class;

    boolean reflection() default false;
}
