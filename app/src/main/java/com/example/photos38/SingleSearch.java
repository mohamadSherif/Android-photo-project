package com.example.photos38;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class SingleSearch extends AppCompatActivity {

    RecyclerView totalRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewAdapter adapter;
    ArrayList<Photo> totalPhotos = new ArrayList<>();
    RecyclerViewAdapter.RecyclerViewClickListener listener;
    SearchView searchview;

    ArrayList<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_search);
        try { //adds the albums to the listview if the file exists
            FileInputStream fis = openFileInput(Home.FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e){
            Log.e("check", "loda");
        }

        for(Album a : albums){
            totalPhotos.addAll(a.photos);
        }

        searchview = findViewById(R.id.searchSingleTag);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        totalRecyclerView = findViewById(R.id.totalRecyclerView);
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

    public void back(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}