package com.ladaube;

import android.content.Context;

public class TracksAdapter extends MyAdapterBase<Track> {

    public TracksAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getText(Track obj) {
        return obj.getName();
    }
}
