package com.ladaube;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ArtistsActivity extends ListActivity {

    private ArtistsAdapter artistsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artistsAdapter = new ArtistsAdapter(this);
        setListAdapter(artistsAdapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Artist a = artistsAdapter.getItem(position);
                Intent intent = new Intent(ArtistsActivity.this, AlbumsActivity.class);
                intent.putExtra("artist", a);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        artistsAdapter.setData(Client.getInstance().getArtists());
    }
}