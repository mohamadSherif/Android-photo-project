package com.example.photos38;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class AlbumOptions extends AppCompatActivity {

    public static final String ALBUM_NAME = "albumName";
    public static final String ALBUM_INDEX = "albumIndex";
    public static final String ALBUM_OPTION = "albumOption";

    private int albumIndex;
    private EditText albumName;
    private ArrayList<Album> albumsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_options);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumName = findViewById(R.id.album_name);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumIndex = bundle.getInt(ALBUM_INDEX);
            albumName.setText(bundle.getString(ALBUM_NAME));
        }
    }

    public void save(View view){
        // gather all data from text fields
        String name = albumName.getText().toString();
        ArrayList<Album> albums = Album.getAlbums(this);

        for(int i=0; i<albums.size(); i++) {
            Album a = albums.get(i);
            if (a.name.equalsIgnoreCase(name) && i!=albumIndex) {
                Bundle bundle = new Bundle();
                bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                        "Album " + name + " already exists");
                DialogFragment newFragment = new AlbumDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "badfields");
                return; // does not quit activity, just returns from method
            }
        }


        // pop up dialog if errors in input, and return
        // name and year are mandatory
        if (name == null || name.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Name is required");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        }

        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_OPTION, "save");
        bundle.putInt(ALBUM_INDEX, albumIndex);
        bundle.putString(ALBUM_NAME,name);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish(); // pops activity from the call stack, returns to parent (Home.java)
    }

    public void delete(View view){
        String name = albumName.getText().toString();
        ArrayList<Album> albums = Album.getAlbums(this);

        if (name == null || name.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Name is required");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        }


        if (!albums.get(albumIndex).name.equalsIgnoreCase(name)) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Can Only Delete Selected Album: "+albums.get(albumIndex));
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        }

        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_OPTION, "delete");
        bundle.putInt(ALBUM_INDEX, albumIndex);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void open(View view){
        Bundle bundle = new Bundle();
        bundle.putInt(ImageDashboard.ALBUM_INDEX, albumIndex);

        Intent intent = new Intent(this, ImageDashboard.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void back(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}