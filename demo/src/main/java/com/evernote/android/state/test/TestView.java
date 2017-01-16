package com.evernote.android.state.test;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

public class TestView extends View {

    @State
    public int mState;

    public TestView(Context context) {
        super(context);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state));
    }
}
