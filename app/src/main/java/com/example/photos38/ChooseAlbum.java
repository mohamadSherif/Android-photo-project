package com.example.photos38;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChooseAlbum extends AppCompatActivity {

    public static final String ALBUM_INDEX = "albumIndex";
    public static final String PHOTO_INDEX = "photoIndex";
    private Spinner albumSpinner;
    private ArrayList<Album> albums;
    private ArrayList<Photo> photos;
    int albumIndex, photoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle  = getIntent().getExtras();
        if(bundle!=null){
            albumIndex = bundle.getInt(ALBUM_INDEX);
            photoIndex = bundle.getInt(PHOTO_INDEX);
        }
        albumSpinner = findViewById(R.id.albumSpinner);

        try { //adds the albums to the listview if the file exists
            FileInputStream fis = openFileInput(Home.FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e){ }

        photos = albums.get(albumIndex).photos;
        albumSpinner.setAdapter(new ArrayAdapter<Album>(this, R.layout.list_cell, albums));
    }
    public void move(View view){
        String selectedAlbum = albumSpinner.getSelectedItem().toString();
        Photo pic = photos.get(photoIndex);
        photos.remove(photoIndex);

        for(int i=0; i<albums.size(); i++){
            Album a = albums.get(i);
            if(a.name.equalsIgnoreCase(selectedAlbum)){
                a.photos.add(pic);
                break;
            }
        }
        serialize();

        Toast toast=Toast. makeText(getApplicationContext(),"Image moved",Toast. LENGTH_SHORT);
        toast. show();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void serialize(){
        try { //serialize the update
            FileOutputStream fos = openFileOutput(Home.FILE_NAME, MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(albums);
            os.close();
            fos.close();
        } catch(Exception e){ }
    }
}