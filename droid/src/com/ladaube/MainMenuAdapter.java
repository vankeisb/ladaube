package com.ladaube;

import android.content.Context;

import java.util.Arrays;

public class MainMenuAdapter extends MyAdapterBase<String> {

    private static final String[] ITEMS = {"Artists", "Albums", "Playlists", "Search" };

    public MainMenuAdapter(Context context) {
        super(context);
        setData(Arrays.asList(ITEMS));
    }


}
