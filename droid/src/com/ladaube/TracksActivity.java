package com.ladaube;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

public class TracksActivity extends ListActivity {

    private TracksAdapter tracksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracksAdapter = new TracksAdapter(this);
        setListAdapter(tracksAdapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // play track !
                Track t = tracksAdapter.getItem(position);
                try {
                    String url = Client.getInstance().getBaseUrl() + "/stream/" + t.getId() + "/" + Client.getInstance().getSessionId();
//                    String url = "http://www.rvkb.com/pub/piano.mp3";
                    MediaPlayer mp = new MediaPlayer();
//                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            mediaPlayer.release();
//                        }
//                    });
                    mp.setDataSource(url);
                    mp.prepare();
                    mp.start();
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // feed with tracks list
        Intent intent = getIntent();
        Album album = (Album)intent.getSerializableExtra("album");
        if (album!=null) {
            // get tracks for album
            List<Track> tracks = Client.getInstance().getTracks(album);
            tracksAdapter.setData(tracks);
        } else {
            Playlist p = (Playlist)intent.getSerializableExtra("playlist");
            if (p!=null) {
                List<Track> tracks = Client.getInstance().getTracksInPlaylist(p);
                tracksAdapter.setData(tracks);
            } else {
                tracksAdapter.setData(Collections.<Track>emptyList());
            }
        }
    }


}