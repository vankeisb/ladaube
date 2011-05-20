package com.ladaube;

import android.content.Context;

public class ArtistsAdapter extends MyAdapterBase<Artist> {

    public ArtistsAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getText(Artist obj) {
        return obj.getName();
    }
}
