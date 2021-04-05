package com.example.photos38;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    public static final String FILE_NAME = "data";
    private static final int ALBUM_OPTIONS_CODE = 1;
    private static final int ADD_CODE = 2;
    public static ArrayList<Album> albums = new ArrayList<>();

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try { //adds the albums to the listview if the file exists
            FileInputStream fis = openFileInput(FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e){ }


        ArrayAdapter<Album> adapter = new ArrayAdapter<>(this, R.layout.list_cell, albums);

        listview = findViewById(R.id.albums_list);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listview.setOnItemClickListener((parent, view, position, id) -> showOptionDialog(position));

    }

    public boolean onCreateOptionsMenu(Menu menu){ //override my own menu bar
        getMenuInflater().inflate(R.menu.add_album_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_album_add:
                addAlbum();
                return true;
            case R.id.action_image_search:
                if(albums==null || albums.size()==0){
                    Bundle bundle = new Bundle();
                    bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                            "No Albums Exists!");
                    DialogFragment newFragment = new AlbumDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(), "badfields");
                    return true; // does not quit activity, just returns from method
                }
                AlertDialog.Builder box = new AlertDialog.Builder(this);
                box.setTitle("Choose Search").setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                }).setNegativeButton("Double-Tag", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), DoubleSearch.class);
                        startActivity(intent);
                    }
                }).setNeutralButton("Single-Tag", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), SingleSearch.class);
                        startActivity(intent);
                    }
                });
                box.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addAlbum(){
        Intent intent = new Intent(this, AddAlbum.class);
        startActivityForResult(intent, ADD_CODE);
    }

    public void showOptionDialog(int pos){
        Bundle bundle = new Bundle();
        Album album = albums.get(pos);
        bundle.putString(AlbumOptions.ALBUM_NAME, album.name);
        bundle.putInt(AlbumOptions.ALBUM_INDEX, pos);
        Intent intent = new Intent(this, AlbumOptions.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ALBUM_OPTIONS_CODE);
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }



        if (requestCode == ALBUM_OPTIONS_CODE) {
            if(bundle.getString(AlbumOptions.ALBUM_OPTION).equalsIgnoreCase("save")) {
                // gather all info passed back by launched activity
                String name = bundle.getString(AlbumOptions.ALBUM_NAME);
                int index = bundle.getInt(AlbumOptions.ALBUM_INDEX);
                Album album = albums.get(index);
                album.name = name;
            }

            if(bundle.getString(AlbumOptions.ALBUM_OPTION).equalsIgnoreCase("delete")){
                int index = bundle.getInt(AlbumOptions.ALBUM_INDEX);
                albums.remove(index);
            }

            try {
                FileOutputStream fos = openFileOutput(Home.FILE_NAME, MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(albums);
                os.close();
                fos.close();
            } catch(Exception e){ }
        }

        if(requestCode == ADD_CODE){
            String newAlbumName = bundle.getString(AddAlbum.NEW_ALBUM_NAME);
            albums.add(new Album(newAlbumName));

            try {
                FileOutputStream fos = openFileOutput(Home.FILE_NAME, MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(albums);
                os.close();
                fos.close();
            } catch(Exception e){ }
        }

        // redo the adapter to reflect change^K
        listview.setAdapter(new ArrayAdapter<Album>(this, R.layout.list_cell, albums));
    }
}