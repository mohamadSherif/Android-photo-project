package com.example.photos38;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AddAlbum extends AppCompatActivity {

    public static final String NEW_ALBUM_NAME = "newAlbumName";
    private EditText albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumName = findViewById(R.id.add_album_name);
    }

    public void add(View view){
        String name = albumName.getText().toString();
        ArrayList<Album> albums = Album.getAlbums(this);

        for(Album a : albums){
            if(a.name.equalsIgnoreCase(name)){
                Bundle bundle = new Bundle();
                bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                        "Album " + name + " already exists");
                DialogFragment newFragment = new AlbumDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "badfields");
                return; // does not quit activity, just returns from method
            }
        }

        if(name == null || albumName.length()==0){
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Name is required");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return; // does not quit activity, just returns from method
        }

        Bundle bundle = new Bundle();
        bundle.putString(NEW_ALBUM_NAME, name);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();

    }


    public void back(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}