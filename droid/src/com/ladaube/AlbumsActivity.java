package com.ladaube;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class AlbumsActivity extends ListActivity {

    private AlbumsAdapter albumsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        albumsAdapter = new AlbumsAdapter(this);
        setListAdapter(albumsAdapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // activate Tracks activity
                Album a = albumsAdapter.getItem(position);
                Intent intent = new Intent(AlbumsActivity.this, TracksActivity.class);
                intent.putExtra("album", a);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Artist a = null;
        if (intent!=null) {
            a = (Artist)intent.getSerializableExtra("artist");
        }
        List<Album> albums = Client.getInstance().getAlbums(a);
        albumsAdapter.setData(albums);

    }
}