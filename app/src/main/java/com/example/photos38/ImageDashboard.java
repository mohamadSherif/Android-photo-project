package com.example.photos38;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ImageDashboard extends AppCompatActivity {
    public static final String ALBUM_INDEX = "albumIndex";
    final int SELECT_PHOTO=1;
    final int PERMISSION_CODE=2;
    final int DISPLAY_CODE=3;
    Uri uri;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewAdapter adapter;
    ArrayList<Photo> photoList;
    RecyclerViewAdapter.RecyclerViewClickListener listener;

    ArrayList<Album> albums;
    int albumIndex;
    Album currentAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            albumIndex = bundle.getInt(ALBUM_INDEX);
        }

        try { //adds the albums to the listview if the file exists
            FileInputStream fis = openFileInput(Home.FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e){
            Log.e("check", "loda");
        }

        currentAlbum = albums.get(albumIndex);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        photoList = currentAlbum.photos;
        
        setOnClickListener();
        adapter = new RecyclerViewAdapter(photoList, listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setOnClickListener() {
        listener = new RecyclerViewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                AlertDialog.Builder box = new AlertDialog.Builder(ImageDashboard.this);
                box.setTitle("Options").setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                }).setNegativeButton("Display", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ImageDisplay.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(ImageDisplay.PHOTO_INDEX, position);
                        bundle.putInt(ImageDisplay.ALBUM_INDEX, albumIndex);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }).setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = photoList.get(position).caption+" deleted";
                        photoList.remove(position);
                        serialize();
                        adapter = new RecyclerViewAdapter(photoList, listener);
                        recyclerView.setAdapter(adapter);
                        Toast.makeText(ImageDashboard.this, str, Toast.LENGTH_SHORT).show();
                    }
                });

                box.show();

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //override my own menu bar
        getMenuInflater().inflate(R.menu.search_image_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_image_add:
                chooseImage();
                return true;
            case R.id.action_image_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(
                        new SearchView.OnQueryTextListener() {

                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            // This method is overridden to filter
                            // the adapter according to a search query
                            // when the user is typing search
                            @Override
                            public boolean onQueryTextChange(String newText) {
                                adapter.getFilter().filter(newText);
                                return false;
                            }
                        });
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void chooseImage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else{
                chooseImageHelper();
            }
        }
        else{
            chooseImageHelper();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    chooseImageHelper();
                else
                    Toast.makeText(this, "Access Denied!", Toast.LENGTH_SHORT).show();
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public void chooseImageHelper(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK && intent != null && intent.getData() != null){
            uri = intent.getData();
            String path = getPath(uri);

            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 128, 128);
            SerialBitmap sb = new SerialBitmap(thumbImage); // sometimes thumbImage is null and app crashes

            Photo newPhoto = new Photo(sb);
            newPhoto.caption= path.substring(path.lastIndexOf("/")+1);
            currentAlbum.photos.add(newPhoto);

            adapter.notifyDataSetChanged();

            try { //update the serialization file, data
                FileOutputStream fos = openFileOutput(Home.FILE_NAME, MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(albums);
                os.close();
                fos.close();
            } catch(Exception e){ }
        }
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    public void back(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void serialize() {
        try { //serialize the update
            FileOutputStream fos = openFileOutput(Home.FILE_NAME, MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(albums);
            os.close();
            fos.close();
        } catch (Exception e) {
        }
    }
}