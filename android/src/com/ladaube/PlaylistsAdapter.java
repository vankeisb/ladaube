package com.ladaube;

import android.content.Context;

public class PlaylistsAdapter extends MyAdapterBase<Playlist> {

    public PlaylistsAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getText(Playlist obj) {
        return obj.getName();
    }
}
