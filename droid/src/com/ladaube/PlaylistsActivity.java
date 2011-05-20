package com.ladaube;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class PlaylistsActivity extends ListActivity {

    private PlaylistsAdapter playlistsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlistsAdapter = new PlaylistsAdapter(this);
        setListAdapter(playlistsAdapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Playlist p = playlistsAdapter.getItem(position);
                Intent intent = new Intent(PlaylistsActivity.this, TracksActivity.class);
                intent.putExtra("playlist", p);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        playlistsAdapter.setData(Client.getInstance().getPlaylists());
    }



}