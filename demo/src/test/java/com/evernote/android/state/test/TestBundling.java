package com.evernote.android.state.test;

import android.os.Bundle;
import android.os.Parcelable;

import com.evernote.android.state.StateSaver;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rwondratschek
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
@FixMethodOrder(MethodSorters.JVM)
public class TestBundling {

    private Bundle mBundle;

    @Before
    public void setUp() {
        mBundle = new Bundle();
    }

    @Test
    public void testSimple() {
        TestSimple object = createSavedInstance(TestSimple.class);
        object.field = 5;

        StateSaver.restoreInstanceState(object, mBundle);

        assertThat(object.field).isEqualTo(0);
    }

    @Test
    public void testProperty() {
        TestProperty object = createSavedInstance(TestProperty.class);
        object.setTest(5);

        StateSaver.restoreInstanceState(object, mBundle);

        assertThat(object.getTest()).isEqualTo(0);
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

    @Test
    public void testView() {
        TestView testView = new TestView(RuntimeEnvironment.application);
        Parcelable state = testView.onSaveInstanceState();

        testView.mState = 5;

        testView.onRestoreInstanceState(state);

        assertThat(testView.mState).isEqualTo(0);

        TestView.InnerView innerView = new TestView.InnerView(RuntimeEnvironment.application);
        state = innerView.onSaveInstanceState();

        innerView.mState = 5;
        innerView.mStateInner = 6;
        innerView.onRestoreInstanceState(state);

        assertThat(innerView.mState).isEqualTo(0);
        assertThat(innerView.mStateInner).isEqualTo(0);

        innerView.mState = 5;
        innerView.mStateInner = 6;
        state = innerView.onSaveInstanceState();

        innerView.mState = 0;
        innerView.mStateInner = 0;
        innerView.onRestoreInstanceState(state);

        assertThat(innerView.mState).isEqualTo(5);
        assertThat(innerView.mStateInner).isEqualTo(6);
    }

    @Test
    public void testBundler() {
        TestBundler object = createSavedInstance(TestBundler.class);
        object.setData2(new TestBundler.Data());
        object.setDataRefl(new TestBundler.Data());

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.getData2()).isNull();
        assertThat(object.getDataRefl()).isNull();

        TestBundler.Data data = new TestBundler.Data();
        data.int1 = 1;
        data.int2 = 2;
        object.setData2(data);
        data = new TestBundler.Data();
        data.int1 = 3;
        data.int2 = 4;
        object.setDataRefl(data);

        StateSaver.saveInstanceState(object, mBundle);

        object.setData2(null);
        object.setDataRefl(null);
        assertThat(object.getData2()).isNull();
        assertThat(object.getDataRefl()).isNull();

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.getData2()).isNotNull();
        assertThat(object.getData2().int1).isEqualTo(1);
        assertThat(object.getData2().int2).isEqualTo(2);
        assertThat(object.getDataRefl()).isNotNull();
        assertThat(object.getDataRefl().int1).isEqualTo(3);
        assertThat(object.getDataRefl().int2).isEqualTo(4);
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
    public void testTypesReflection() {
        TestTypesReflection object = createSavedInstance(TestTypesReflection.class);
        object.setIntegerObj(5);

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.getIntegerObj()).isNull();

        object.setInt(5);
        object.setIntegerObj(6);
        object.setParcelableArrayList(new ArrayList<TestTypes.ParcelableImpl>() {{
            add(new TestTypes.ParcelableImpl(7));
        }});

        StateSaver.saveInstanceState(object, mBundle);
        object.setInt(0);
        object.setIntegerObj(null);
        object.setParcelableArrayList(null);

        StateSaver.restoreInstanceState(object, mBundle);
        assertThat(object.getInt()).isEqualTo(5);
        assertThat(object.getIntegerObj()).isNotNull().isEqualTo(6);
        assertThat(object.getParcelableArrayList()).isNotNull().isNotEmpty().containsExactly(new TestTypes.ParcelableImpl(7));
    }

    @Test
    public void testInheritance() {
        TestInheritance.InheritanceLevel2 level2 = createSavedInstance(TestInheritance.InheritanceLevel2.class);
        level2.mValue1 = 4;
        level2.mValue2 = 5;

        StateSaver.restoreInstanceState(level2, mBundle);
        assertThat(level2.mValue1).isEqualTo(0);
        assertThat(level2.mValue2).isEqualTo(0);

        level2.mValue1 = 4;
        level2.mValue2 = 5;

        StateSaver.saveInstanceState(level2, mBundle);
        level2.mValue1 = 0;
        level2.mValue2 = 0;

        StateSaver.restoreInstanceState(level2, mBundle);
        assertThat(level2.mValue1).isEqualTo(4);
        assertThat(level2.mValue2).isEqualTo(5);

        TestInheritance.InheritanceLevel1 level1 = createSavedInstance(TestInheritance.InheritanceLevel1.class);
        level1.mValue1 = 4;

        StateSaver.restoreInstanceState(level1, mBundle);
        assertThat(level1.mValue1).isEqualTo(0);
        level1.mValue1 = 4;

        StateSaver.saveInstanceState(level1, mBundle);
        level1.mValue1 = 0;

        StateSaver.restoreInstanceState(level1, mBundle);
        assertThat(level1.mValue1).isEqualTo(4);
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
