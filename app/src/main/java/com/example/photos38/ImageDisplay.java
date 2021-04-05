package com.example.photos38;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ImageDisplay extends AppCompatActivity {

    public static final String PHOTO_INDEX = "photoIndex";
    public static final String ALBUM_INDEX = "albumIndex";
    private static final int ADD_CODE = 2;//

    ImageView displayImage;
    ArrayList<Album> albums; //get through bundle
    ArrayList<Photo> photos; //get through bundle
    ArrayList<String> persons;

    int photoIndex, albumIndex; //get through bundle
    TextView location;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayImage = findViewById(R.id.displayImage);
        location = findViewById(R.id.location);
        listview = findViewById(R.id.personList);

        try { //adds the albums to the listview if the file exists
            FileInputStream fis = openFileInput(Home.FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e){ }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            photoIndex = bundle.getInt(PHOTO_INDEX);
            albumIndex = bundle.getInt(ALBUM_INDEX);
        }

        photos = albums.get(albumIndex).photos;
        Bitmap image = photos.get(photoIndex).byteThumbImage.getThumbImage();
        displayImage.setImageBitmap(image);
        location.setText(photos.get(photoIndex).location);

        persons = photos.get(photoIndex).person;
        listview.setAdapter(new ArrayAdapter<>(this, R.layout.person_list_cell, persons));
        listview.setOnItemClickListener((parent, view, position, id) -> showOptionDialog(position));
    }

    private void showOptionDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete person tag: " +persons.get(position)+" ?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        persons.remove(position);
                        listview.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.person_list_cell, persons));
                        serialize();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public boolean onCreateOptionsMenu(Menu menu){ //override my own menu bar
        getMenuInflater().inflate(R.menu.delete_image_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_image_delete:

                if(photos.size()==1){
                    photos.remove(photoIndex);
                    serialize();
                    Intent intent = new Intent(this, ImageDashboard.class);
                    Bundle bundle  = new Bundle();
                    bundle.putInt(ImageDashboard.ALBUM_INDEX, albumIndex);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(photoIndex == photos.size()-1){
                    photos.remove(photoIndex);
                    serialize();
                    scrollLeftHelper();
                }
                else{
                    photos.remove(photoIndex);
                    serialize();
                    scrollRightHelper();
                }
                if(photos.size()>0)
                    Toast.makeText(ImageDisplay.this, photos.get(photoIndex).caption+" deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void scrollLeft(View view){
        scrollLeftHelper();
    }

    public void scrollLeftHelper(){
        if(photoIndex==0)
            photoIndex=photos.size()-1;
        else
            photoIndex--;
        Bitmap image = photos.get(photoIndex).byteThumbImage.getThumbImage();
        displayImage.setImageBitmap(image);
        location.setText(photos.get(photoIndex).location);
        persons = photos.get(photoIndex).person;
        listview.setAdapter(new ArrayAdapter<String>(this, R.layout.person_list_cell, persons));
    }

    public void scrollRight(View view){
        scrollRightHelper();
    }

    public void scrollRightHelper(){
        photoIndex = (photoIndex+1)%photos.size();
        Bitmap image = photos.get(photoIndex).byteThumbImage.getThumbImage();
        displayImage.setImageBitmap(image);
        location.setText(photos.get(photoIndex).location);
        persons = photos.get(photoIndex).person;
        listview.setAdapter(new ArrayAdapter<String>(this, R.layout.person_list_cell, persons));
    }

    public void addLocation(View view){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.location_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        input.setText(photos.get(photoIndex).location);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        photos.get(photoIndex).location = input.getText().toString();
                        location.setText(photos.get(photoIndex).location);
                        try { //serialize the update
                            FileOutputStream fos = openFileOutput(Home.FILE_NAME, MODE_PRIVATE);
                            ObjectOutputStream os = new ObjectOutputStream(fos);
                            os.writeObject(albums);
                            os.close();
                            fos.close();
                        } catch(Exception e){ }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void addPerson(View view){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.person_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = input.getText().toString();
                        if(name.equals("") || name==null || persons.contains(name)){
                            Bundle bundle = new Bundle();
                            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                                    "Enter non-empty unique persone name");
                            DialogFragment newFragment = new AlbumDialogFragment();
                            newFragment.setArguments(bundle);
                            newFragment.show(getSupportFragmentManager(), "badfields");
                            return; // does not quit activity, just returns from method
                        }

                        persons.add(name);
                        listview.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.person_list_cell, persons));
                        try { //serialize the update
                            FileOutputStream fos = openFileOutput(Home.FILE_NAME, MODE_PRIVATE);
                            ObjectOutputStream os = new ObjectOutputStream(fos);
                            os.writeObject(albums);
                            os.close();
                            fos.close();
                        } catch(Exception e){ }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void moveImage(View view){
        Intent intent = new Intent(this, ChooseAlbum.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ChooseAlbum.ALBUM_INDEX, albumIndex);
        bundle.putInt(ChooseAlbum.PHOTO_INDEX, photoIndex);
        intent.putExtras(bundle);
        startActivity(intent);
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

    public void back(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt(ImageDashboard.ALBUM_INDEX, albumIndex);
        Intent intent = new Intent(this, ImageDashboard.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
