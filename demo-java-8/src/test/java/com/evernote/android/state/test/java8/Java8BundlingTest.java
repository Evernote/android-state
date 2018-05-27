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
package com.evernote.android.state.test.java8;

import android.os.Bundle;

import com.evernote.android.state.StateSaver;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rwondratschek
 */
@FixMethodOrder(MethodSorters.JVM)
public class Java8BundlingTest {

    private Bundle mBundle;

    @Before
    public void setUp() {
        mBundle = new Bundle();
    }

    @Test
    public void testTypes() {
        TestTypes object = createSavedInstance(TestTypes.class);
        object.mBooleanObj = Boolean.FALSE;

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.mBooleanObj).isNull();

        object.mInt = 5;
        object.mIntegerObj = 6;
        object.mParcelableArrayList = new ArrayList<TestTypes.ParcelableImpl>() {{
            add(new TestTypes.ParcelableImpl(7));
        }};

        StateSaver.saveInstanceState(object, mBundle);
        object.mInt = 0;
        object.mIntegerObj = null;
        object.mParcelableArrayList = null;

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.mInt).isEqualTo(5);
        assertThat(object.mIntegerObj).isNotNull().isEqualTo(6);
        assertThat(object.mParcelableArrayList).isNotNull().isNotEmpty().containsExactly(new TestTypes.ParcelableImpl(7));
    }

    @Test
    public void testTypesProperty() {
        TestTypesProperty object = createSavedInstance(TestTypesProperty.class);
        object.setBooleanObj(Boolean.FALSE);

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.getBooleanObj()).isNull();

        object.setInt(5);
        object.setIntegerObj(6);
        object.setParcelableImplExtension(new TestTypesProperty.ParcelableImplExtension(7, 8));
        object.setParcelableArrayList(new ArrayList<TestTypes.ParcelableImpl>() {{
            add(new TestTypes.ParcelableImpl(9));
        }});

        StateSaver.saveInstanceState(object, mBundle);
        object.setInt(0);
        object.setIntegerObj(0);
        object.setParcelableImplExtension(null);
        object.setParcelableArrayList(null);

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.getInt()).isEqualTo(5);
        assertThat(object.getIntegerObj()).isNotNull().isEqualTo(6);
        assertThat(object.getParcelableImplExtension()).isNotNull().isEqualTo(new TestTypesProperty.ParcelableImplExtension(7, 8));
        assertThat(object.getParcelableArrayList()).isNotNull().isNotEmpty().containsExactly(new TestTypes.ParcelableImpl(9));
    }

    @Test
    public void testNested() {
        TestNested.Inner1.InnerInner1 object1 = createSavedInstance(TestNested.Inner1.InnerInner1.class);
        TestNested.Inner2.InnerInner1 object2 = createSavedInstance(TestNested.Inner2.InnerInner1.class);

        object1.test = 5;
        object2.test = 5;

        StateSaver.restoreInstanceState(object1, mBundle);
        StateSaver.restoreInstanceState(object2, mBundle);

        assertThat(object1.test).isEqualTo(0);
        assertThat(object2.test).isEqualTo(0);
    }

    private <T> T createSavedInstance(Class<T> clazz) {
        try {
            T instance = clazz.newInstance();
            StateSaver.saveInstanceState(instance, mBundle);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
