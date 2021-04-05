package com.example.photos38;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class DoubleSearch extends AppCompatActivity {

    RecyclerView totalRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewAdapter adapter;
    ArrayList<Photo> totalPhotos = new ArrayList<>();
    RecyclerViewAdapter.RecyclerViewClickListener listener;
    ArrayList<Album> albums;
    EditText t1, t2; //tag1, tag2
    String tag1, tag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_search);

        try { //adds the albums to the listview if the file exists
            FileInputStream fis = openFileInput(Home.FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e){ }

        for(Album a : albums){
            totalPhotos.addAll(a.photos);
        }

        t1 = findViewById(R.id.tag1);
        t2 = findViewById(R.id.tag2);

        totalRecyclerView = findViewById(R.id.totalDoubleRecyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        totalRecyclerView.setLayoutManager(layoutManager);

        setOnClickListener();
        adapter = new RecyclerViewAdapter(totalPhotos, listener);
        totalRecyclerView.setAdapter(adapter);
        totalRecyclerView.setHasFixedSize(true);
    }

    private void setOnClickListener() {
        listener = new RecyclerViewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) { }
        };
    }

    public void orSearch(View view){
        tag1 = ((EditText)findViewById(R.id.tag1)).getText().toString().trim().toLowerCase();
        tag2 = ((EditText)findViewById(R.id.tag2)).getText().toString().trim().toLowerCase();

        if(tag1==null || tag2==null || tag1.equals("") || tag2.equals("")){
            Toast.makeText(this, "Both tags should be non-empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Photo> temp = new ArrayList<>();
        for(Photo p : totalPhotos){
            if(p.hasSingleTag(tag1) || p.hasSingleTag(tag2)){
                temp.add(p);
            }
        }
        totalRecyclerView.setAdapter(new RecyclerViewAdapter(temp, listener));

    }

    public void andSearch(View view){
        tag1 = ((EditText)findViewById(R.id.tag1)).getText().toString().trim().toLowerCase();
        tag2 = ((EditText)findViewById(R.id.tag2)).getText().toString().trim().toLowerCase();

        if(tag1==null || tag2==null || tag1.equals("") || tag2.equals("")){
            Toast.makeText(this, "Both tags should be non-empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Photo> temp = new ArrayList<>();
        for(Photo p : totalPhotos){
            if(p.hasSingleTag(tag1) && p.hasSingleTag(tag2)){
                temp.add(p);
            }
        }
        totalRecyclerView.setAdapter(new RecyclerViewAdapter(temp, listener));
    }

    public void reset(View view){
        t1.setText(null);
        t2.setText(null);
        totalRecyclerView.setAdapter(new RecyclerViewAdapter(totalPhotos, listener));
    }

    public void back(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}