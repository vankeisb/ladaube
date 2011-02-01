package com.ladaube;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class LaDaubeActivity extends ListActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Client.getInstance();

        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(this);
        setListAdapter(mainMenuAdapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0 : startActivity(ArtistsActivity.class); break;
                    case 1 : startActivity(AlbumsActivity.class); break;
                    case 2 : startActivity(PlaylistsActivity.class); break;
                    default: System.out.println("FooBar");
                }
            }
        });
    }

    private void startActivity(Class<?> clazz) {
        Intent i = new Intent(this, clazz);
        startActivity(i);
    }

}
