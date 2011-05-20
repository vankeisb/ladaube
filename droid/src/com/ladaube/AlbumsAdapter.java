package com.ladaube;

import android.content.Context;

public class AlbumsAdapter extends MyAdapterBase<Album> {

    public AlbumsAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getText(Album obj) {
        return obj.getName() + " - " + obj.getArtist();
    }
}
